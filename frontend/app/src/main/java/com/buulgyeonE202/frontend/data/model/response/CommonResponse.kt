package com.buulgyeonE202.frontend.data.model.response

import com.google.gson.annotations.SerializedName

data class CommonResponse<T>(
    @SerializedName("isSuccess") val _isSuccess: Boolean = false,
    @SerializedName("code") val code: String? = "",
    @SerializedName("message") val message: String? = "",
    @SerializedName("data") val data: T? = null,
    @SerializedName("status") val status: Int? = 0 // 혹시 몰라 status도 추가
) {

    val isSuccess: Boolean
        get() = _isSuccess || code?.startsWith("S") == true || status == 200
}