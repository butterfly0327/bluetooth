package com.buulgyeonE202.frontend.data.model.response.preset

import com.google.gson.annotations.SerializedName

data class PresetResponse(
    @SerializedName("presetId") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("gestureId") val gestureId: String?,
    @SerializedName("actionTitle") val actionTitle: String?,
    @SerializedName("actionDescription") val actionDescription: String?,
    @SerializedName("isRepresentative") val isRepresentative: Boolean
)