package com.buulgyeonE202.frontend.ui.camera.model

import androidx.camera.core.CameraSelector

enum class LensFacing(val value: Int) {
    // CameraX의 상수값(0: Front, 1: Back)과 정확히 매핑
    BACK(CameraSelector.LENS_FACING_BACK),
    FRONT(CameraSelector.LENS_FACING_FRONT);

    fun toggle(): LensFacing = if (this == BACK) FRONT else BACK
}