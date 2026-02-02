package com.buulgyeonE202.frontend.data.model.request.preset

import com.google.gson.annotations.SerializedName

// 서버 명세서의 "title" 키값과 맞추기 위해 data class로 정의.
data class MappingNameChangeRequest(
    @SerializedName("title")
    val title: String
)