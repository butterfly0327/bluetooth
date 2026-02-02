package com.buulgyeonE202.frontend.ui.camera.component

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun CameraRulerSlider(
    currentZoom: Float,
    minZoom: Float,
    maxZoom: Float,
    onZoomChange: (Float) -> Unit,
    onInteraction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val context = LocalContext.current

    // 최신 줌 값을 항상 바라보는 상태 변수
    val currentZoomState by rememberUpdatedState(currentZoom)

    val vibrator = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    val pixelsPerUnit = 200f

    var dragStartZoom by remember { mutableFloatStateOf(currentZoom) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }
    var lastIntZoom by remember { mutableIntStateOf(currentZoom.toInt()) }

    // 눈금 스타일
    val majorTickHeight = 25.dp   // 긴 눈금 (노란색, 굵은 흰색)
    val middleTickHeight = 25.dp  // 중간 눈금
    val minorTickHeight = 15.dp   // 작은 눈금

    val majorStrokeWidth = 2.dp
    val minorStrokeWidth = 1.dp

    val currentZoomTextStyle = TextStyle(
        color = Color.Yellow,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        shadow = Shadow(color = Color.Black, blurRadius = 2f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            // 높이는 부모(CameraControlsBar)에서 정해주므로 fillMaxSize 권장
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        onInteraction()
                        dragStartZoom = currentZoomState
                        totalDragDistance = 0f
                    },
                    onDragEnd = { onInteraction() },
                    onDragCancel = { onInteraction() }
                ) { change, dragAmount ->
                    change.consume()
                    onInteraction()

                    totalDragDistance += dragAmount
                    val zoomChange = -(totalDragDistance / pixelsPerUnit)
                    val newZoom = (dragStartZoom + zoomChange).coerceIn(minZoom, maxZoom)

                    // 햅틱 피드백 (정수 단위 변경 시)
                    if (newZoom.toInt() != lastIntZoom) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(15)
                        }
                        lastIntZoom = newZoom.toInt()
                    }

                    onZoomChange(newZoom)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f

            val bottomY = size.height

            val visibleRange = (size.width / 2) / pixelsPerUnit
            val startVal = (currentZoom - visibleRange - 1).toInt()
            val endVal = (currentZoom + visibleRange + 1).toInt()

            for (i in startVal..endVal) {
                for (j in 0..9) {
                    val tickValue = i + (j * 0.1f)
                    if (tickValue < minZoom || tickValue > maxZoom) continue

                    val offsetX = (tickValue - currentZoom) * pixelsPerUnit
                    val drawX = centerX + offsetX

                    if (drawX < 0 || drawX > size.width) continue

                    val isMajor = (j == 0)
                    val isMiddle = (j == 5)

                    val distanceFromCenter = abs(drawX - centerX)
                    val alpha = (1f - (distanceFromCenter / (size.width / 2))).coerceIn(0f, 1f)

                    // -----------------------------------------------------------
                    // [그리기 로직] 바닥(bottomY) 기준으로 위로 뻗어나감
                    // -----------------------------------------------------------
                    if (isMajor) {
                        drawLine(
                            color = Color.White.copy(alpha = alpha),
                            // 바닥에서 눈금 높이만큼 뺀 곳에서 시작 (위->아래)
                            start = Offset(drawX, bottomY - majorTickHeight.toPx()),
                            // 바닥까지 그림
                            end = Offset(drawX, bottomY),
                            strokeWidth = majorStrokeWidth.toPx()
                        )
                    } else if (isMiddle) {
                        drawLine(
                            color = Color.White.copy(alpha = alpha * 0.8f),
                            start = Offset(drawX, bottomY - middleTickHeight.toPx()),
                            end = Offset(drawX, bottomY),
                            strokeWidth = minorStrokeWidth.toPx()
                        )
                    } else {
                        drawLine(
                            color = Color.Gray.copy(alpha = alpha * 0.6f),
                            start = Offset(drawX, bottomY - minorTickHeight.toPx()),
                            end = Offset(drawX, bottomY),
                            strokeWidth = minorStrokeWidth.toPx()
                        )
                    }
                }
            }

// 중앙 기준선 (노란색)
            drawLine(
                color = Color.Yellow,
                // 노란선은 조금 더 길게 위로 솟아오르게
                start = Offset(centerX, bottomY - majorTickHeight.toPx() - 10.dp.toPx()),
                end = Offset(centerX, bottomY),
                strokeWidth = 2.dp.toPx()
            )

            // 현재 배율 텍스트
            val currentZoomText = String.format("%.1fx", currentZoom)
            val currentTextLayout = textMeasurer.measure(
                text = currentZoomText,
                style = currentZoomTextStyle
            )

            // 텍스트 위치: 노란선 끝보다 조금 더 위에 그림
            drawText(
                currentTextLayout,
                topLeft = Offset(
                    centerX - (currentTextLayout.size.width / 2),
                    bottomY - majorTickHeight.toPx() - 10.dp.toPx() - currentTextLayout.size.height - 4.dp.toPx()
                )
            )
        }
    }
}