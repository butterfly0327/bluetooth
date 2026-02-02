package com.buulgyeonE202.frontend.ui.gesture.model

data class GestureAction(
    val id: Long,            // DB의 PK와 일치
    val title: String,       // 기능 명칭
    val description: String, // 기능 설명
    val assignedGestureId: String? = null // DB에서 실시간으로 가져올 제스처 ID
)