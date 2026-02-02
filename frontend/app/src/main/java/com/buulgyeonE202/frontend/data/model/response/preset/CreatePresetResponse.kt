package com.buulgyeonE202.frontend.data.model.response.preset

data class CreatePresetResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val mappingId: Int
)