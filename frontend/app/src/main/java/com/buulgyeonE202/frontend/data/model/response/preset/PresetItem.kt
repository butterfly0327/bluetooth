package com.buulgyeonE202.frontend.data.model.response.preset

data class PresetItem(
    val presetId: Int,
    val orderIndex: Int,
    val title: String,
    val isRepresentative: Boolean,
    val gestureCount: Int
)