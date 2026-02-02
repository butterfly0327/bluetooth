package com.buulgyeonE202.frontend.ui.camera.viewmodel

import androidx.lifecycle.ViewModel
import com.buulgyeonE202.frontend.ui.camera.CameraConstants
import com.buulgyeonE202.frontend.ui.camera.model.LensFacing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CameraViewModel : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state: StateFlow<CameraState> = _state

    fun onIntent(intent: CameraIntent) {
        when (intent) {
            is CameraIntent.OnZoomBoundsReady -> {
                _state.update { s ->
                    if (intent.lensFacing != s.lensFacing) return@update s

                    val min = intent.minZoom
                    val max = intent.maxZoom
                    val cur = intent.currentZoom.coerceIn(min, max)

                    s.copy(
                        minZoom = min,
                        maxZoom = max,
                        targetZoom = s.targetZoom.coerceIn(min, max),
                        currentZoom = cur
                    )
                }
            }

            is CameraIntent.OnSelectZoom -> {
                _state.update { s ->
                    s.copy(targetZoom = intent.zoom.coerceIn(s.minZoom, s.maxZoom))
                }
            }

            // ✅ [추가] 두 손가락 핀치 줌 로직
            is CameraIntent.OnZoomPinch -> {
                _state.update { s ->
                    // 전면 카메라는 줌 안 됨
                    if (s.lensFacing == LensFacing.FRONT) {
                        return@update s
                    }

                    // 1. 현재 값에 핀치 비율(zoomFactor)을 곱함 (부드러운 소수점 변화)
                    // 2. 너무 급격하게 변하지 않도록 범위 제한
                    val newZoom = (s.targetZoom * intent.zoomFactor)
                        .coerceIn(s.minZoom, s.maxZoom)

                    s.copy(targetZoom = newZoom)
                }
            }

            is CameraIntent.OnAppliedZoomChanged -> {
                _state.update { s -> s.copy(currentZoom = intent.currentZoom) }
            }

            CameraIntent.OnSwitchLens -> {
                _state.update { s ->
                    s.copy(
                        lensFacing = s.lensFacing.toggle(),
                        minZoom = CameraConstants.MIN_ZOOM,
                        maxZoom = CameraConstants.MIN_ZOOM,
                        currentZoom = CameraConstants.MIN_ZOOM,
                        targetZoom = CameraConstants.MIN_ZOOM
                    )
                }
            }

            CameraIntent.OnStartRecording -> {
                _state.update { s -> s.copy(isRecording = true, lastErrorMessage = null) }
            }

            CameraIntent.OnStopRecording -> {
                _state.update { s -> s.copy(isRecording = false) }
            }

            CameraIntent.OnRecordingFinalize -> {
                _state.update { s -> s.copy(isRecording = false) }
            }

            is CameraIntent.OnVideoSaved -> {
                // 저장 완료 처리
            }

            is CameraIntent.OnRecordingError -> {
                _state.update { s ->
                    s.copy(
                        isRecording = false,
                        lastErrorMessage = intent.throwable.message ?: intent.throwable.toString()
                    )
                }
            }
        }
    }
}