package com.buulgyeonE202.frontend.data.model.response.mapping

data class MappingDetailData(
    val presetId: Int,
    val title: String,
    val items: List<MappingItemDto>
)

data class MappingItemDto(
    val mappingId: Int,
    val category: String,
    val actionId: Int,
    val actionName: String,
    val actionDescription: String,
    val gestureId: Int,
    val gestureName: String
)