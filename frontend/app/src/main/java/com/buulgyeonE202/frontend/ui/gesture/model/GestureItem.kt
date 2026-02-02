// ui/gesture/model/GestureItem.kt
package com.buulgyeonE202.frontend.ui.gesture.model

data class GestureItem(
    val id: Int,
    val title: String,
    val count: Int,
    val isFavorite: Boolean = false,
    var isSelected: Boolean = false // 편집 모드 선택 상태
)