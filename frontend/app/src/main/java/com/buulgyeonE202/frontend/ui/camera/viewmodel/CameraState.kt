package com.buulgyeonE202.frontend.ui.camera.viewmodel

import com.buulgyeonE202.frontend.ui.camera.CameraConstants
import com.buulgyeonE202.frontend.ui.camera.model.LensFacing

data class CameraState(
    val minZoom: Float = CameraConstants.MIN_ZOOM,
    val maxZoom: Float = CameraConstants.MAX_ZOOM,

    // UI가 선택한 줌(프리셋)
    val targetZoom: Float = 1f,

    // 실제 적용된 줌(선택적으로 사용)
    val currentZoom: Float = 1f,

    val lensFacing: LensFacing = LensFacing.BACK,
    val isRecording: Boolean = false,

    // 디버그/에러 표시용(원하면 나중에 UI로 노출)
    val lastErrorMessage: String? = null
)
