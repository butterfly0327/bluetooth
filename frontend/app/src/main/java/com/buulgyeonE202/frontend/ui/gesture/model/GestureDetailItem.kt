package com.buulgyeonE202.frontend.ui.gesture.model

data class GestureDetailItem(
    val id: Int,          // mappingItemId
    val actionId: Int,
    val gestureId: Int,
    val category: String,
    val actionName: String,
    val description: String,
    val gestureName: String,
    val iconRes: Int
)