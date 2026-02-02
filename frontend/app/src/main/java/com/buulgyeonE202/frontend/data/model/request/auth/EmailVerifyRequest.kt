package com.buulgyeonE202.frontend.data.model.request.auth
import com.google.gson.annotations.SerializedName

data class EmailVerifyRequest(
    val email: String,

    @SerializedName("otp")
    val otp: String
)