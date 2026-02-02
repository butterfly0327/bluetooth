package com.buulgyeonE202.frontend.ui.camera.component

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.rotate
import kotlin.math.abs
import kotlin.math.roundToInt
@Composable
fun CameraZoomPresets(
    selectedZoom: Float,
    minZoom: Float,
    maxZoom: Float,
    onSelect: (Float) -> Unit,
    // 꾹 누르고 드래그할 때 호출될 콜백
    onLongPressDrag: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = remember(minZoom, maxZoom) { buildZoomSteps(minZoom, maxZoom) }

    // 현재 배율 상태를 기억해둠 (꾹 눌렀을 때 여기서부터 시작하기 위함)
    val currentZoomState by rememberUpdatedState(selectedZoom)

    val context = LocalContext.current
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

    if (steps.isEmpty()) return

    val selected = nearestStep(selectedZoom, steps)

    Row(
        modifier = modifier
            .wrapContentWidth()
            .background(Color(0x55000000), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        steps.forEach { step ->
            val isSelected = abs(step - selected) < 0.05f

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        color = if (isSelected) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(999.dp)
                    )
                    // 터치 및 제스처 감지
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)

                            // 🚨 [여기가 문제 해결의 핵심]
                            // 드래그 시작점을 '버튼의 값(step)'이 아니라 '현재 줌(currentZoomState)'으로 설정!
                            val dragStartZoom = currentZoomState

                            var totalDragDistance = 0f
                            var lastIntZoom = dragStartZoom.toInt()

                            try {
                                // 100ms 안에 손을 떼면 클릭(Tap)으로 처리
                                withTimeout(100) {
                                    waitForUpOrCancellation()
                                    // 탭 성공 -> 해당 버튼 값으로 이동
                                    onSelect(step)
                                }
                            } catch (e: androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException) {
                                // 롱터치가 인식되어 룰러가 뜨는 순간 진동 발생
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                                } else {
                                    @Suppress("DEPRECATION")
                                    vibrator.vibrate(30) // 룰러 시작은 조금 더 강하게 30ms
                                }
                                // 1. 룰러 켜기 (현재 줌 값 유지)
                                onLongPressDrag(dragStartZoom)

                                // 2. 손 떼기 전까지 드래그 루프
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull() ?: break

                                    if (change.pressed) {
                                        // 드래그 중
                                        val dragAmount = change.position.x - change.previousPosition.x
                                        change.consume()

                                        // 룰러와 동일한 계산 로직
                                        totalDragDistance += dragAmount
                                        val zoomChange = -(totalDragDistance / pixelsPerUnit)

                                        // 기준점(dragStartZoom) + 변화량
                                        val newZoom = (dragStartZoom + zoomChange).coerceIn(minZoom, maxZoom)

                                        // 📳 진동
                                        if (newZoom.toInt() != lastIntZoom) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                                            } else {
                                                @Suppress("DEPRECATION")
                                                vibrator.vibrate(15)
                                            }
                                            lastIntZoom = newZoom.toInt()
                                        }

                                        // 값 업데이트
                                        onLongPressDrag(newZoom)
                                    } else {
                                        // 손 뗌 -> 종료
                                        break
                                    }
                                }
                            }
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatZoom(step),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) Color.Black else Color.White
                )
            }
        }
    }
}

private fun buildZoomSteps(minZoom: Float, maxZoom: Float): List<Float> {
    val base = listOf(0.5f, 0.6f, 1f, 2f, 3f, 5f, 10f, 20f, 50f, 100f)
    val result = mutableListOf<Float>()
    result.add(minZoom)
    result.addAll(base.filter { it > minZoom + 0.05f && it < maxZoom - 0.05f })
    result.add(maxZoom)
    return result
        .map { roundForUi(it) }
        .distinct()
        .sorted()
}

private fun roundForUi(v: Float): Float = (v * 10f).roundToInt() / 10f

private fun formatZoom(v: Float): String {
    return if (v % 1.0f == 0f) "${v.toInt()}x" else "${v}x"
}

private fun nearestStep(value: Float, steps: List<Float>): Float {
    if (steps.isEmpty()) return value
    return steps.minByOrNull { abs(value - it) } ?: steps.first()
}