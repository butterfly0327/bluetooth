package com.buulgyeonE202.frontend.ui.camera.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.camera.model.LensFacing

@Composable
fun CameraControlsBar(
    isRecording: Boolean,
    currentLens: LensFacing,
    selectedZoom: Float,
    minZoom: Float,
    maxZoom: Float,
    showRuler: Boolean,
    onRulerInteraction: () -> Unit,
    onRequestShowRuler: () -> Unit,
    onSwitchLens: () -> Unit,
    onSelectZoom: (Float) -> Unit,
    onToggleRecord: () -> Unit,
    uiRotation: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentLens == LensFacing.BACK) {
            Box(
                // Center -> BottomCenter (내용물을 바닥쪽으로 내림)
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // 터치 영역은 넓게 유지
            ) {
                val innerModifier = Modifier

                // 1. [바닥] 버튼 (Presets)
                val presetsAlpha by animateFloatAsState(targetValue = if (showRuler) 0f else 1f, label = "presetsAlpha")

                CameraZoomPresets(
                    selectedZoom = selectedZoom,
                    minZoom = minZoom,
                    maxZoom = maxZoom,
                    onSelect = onSelectZoom,
                    onLongPressDrag = { zoom ->
                        onRequestShowRuler()
                        onSelectZoom(zoom)
                        onRulerInteraction()
                    },
                    modifier = innerModifier.alpha(presetsAlpha),
                )

                // 2. [위] 룰러 (Ruler)
                val rulerAlpha by animateFloatAsState(targetValue = if (showRuler) 1f else 0f, label = "rulerAlpha")

                if (showRuler || rulerAlpha > 0f) {
                    CameraRulerSlider(
                        currentZoom = selectedZoom,
                        minZoom = minZoom,
                        maxZoom = maxZoom,
                        onZoomChange = { zoom ->
                            onSelectZoom(zoom)
                            onRulerInteraction()
                        },
                        onInteraction = onRulerInteraction,
                        modifier = innerModifier.alpha(rulerAlpha)
                    )
                }
            }
        }

        // 간격 조금 조절 (필요하면 줄이거나 늘리세요)
        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            CameraRecordButton(
                isRecording = isRecording,
                onClick = onToggleRecord,
                modifier = Modifier.align(Alignment.Center)
            )

            CameraSwitchLensButton(
                onClick = onSwitchLens,
                uiRotation = uiRotation,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 38.dp)
            )
        }
    }
}