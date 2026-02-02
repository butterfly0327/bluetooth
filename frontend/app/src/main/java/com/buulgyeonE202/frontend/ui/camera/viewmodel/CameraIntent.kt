package com.buulgyeonE202.frontend.ui.camera.viewmodel

import android.net.Uri
import com.buulgyeonE202.frontend.ui.camera.model.LensFacing

sealed interface CameraIntent {
    // 줌 초기화/정보 수신
    data class OnZoomBoundsReady(
        val lensFacing: LensFacing,
        val minZoom: Float,
        val maxZoom: Float,
        val currentZoom: Float
    ) : CameraIntent

    // 버튼 클릭으로 줌 변경
    data class OnSelectZoom(val zoom: Float) : CameraIntent

    // ✅ [추가] 두 손가락 핀치 줌 (비율 Factor)
    data class OnZoomPinch(val zoomFactor: Float) : CameraIntent

    // 실제 적용된 줌 상태 업데이트
    data class OnAppliedZoomChanged(val currentZoom: Float) : CameraIntent

    object OnSwitchLens : CameraIntent

    object OnStartRecording : CameraIntent
    object OnStopRecording : CameraIntent
    object OnRecordingFinalize : CameraIntent

    data class OnVideoSaved(val uri: Uri) : CameraIntent
    data class OnRecordingError(val throwable: Throwable) : CameraIntent
}