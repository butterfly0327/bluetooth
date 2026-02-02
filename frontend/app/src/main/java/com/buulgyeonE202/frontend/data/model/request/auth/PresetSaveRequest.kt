package com.buulgyeonE202.frontend.data.model.request.auth

// 서버와 통신하는 DTO
data class PresetSaveRequest(
    // 🔥 [수정] 뷰모델에서 값을 채워넣으려면 var여야 합니다.
    var id: String? = null,          // 매핑 ID

    val name: String,                // 프리셋 이름

    // 제스처 정보
    val gestureId: String,           // 제스처 ID (1, 2...)
    val gestureName: String? = null, // 제스쳐 이름
    val gestureDescription: String? = null, // 제스쳐 설명

    // 액션 정보
    val actionId: String? = null,
    val actionTitle: String,         // 기능 이름
    val actionDescription: String? = null, // 기능 설명

    // 🔥 [수정] 뷰모델에서 값을 변경하려면 var여야 합니다.
    var isRepresentative: Boolean = false
)