package com.buulgyeonE202.frontend.ui.camera

object CameraConstants {
    const val VIDEO_RELATIVE_PATH = "Movies/Gesture"
    const val VIDEO_MIME_TYPE = "video/mp4"

    // ✅ 줌 관련 (CameraPreview에서 사용)
    const val MIN_ZOOM = 1f
    const val MAX_ZOOM = 10f

    // 드래그 감도 (픽셀 -> 줌 변화량)
    const val ZOOM_SENSITIVITY = 0.0085f

    // ✅ 부드러운 추종 속도 (초당 최대 줌 변화량)
    const val MAX_ZOOM_CHANGE_PER_SEC = 6.5f
}
