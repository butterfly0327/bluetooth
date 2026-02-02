package com.buulgyeonE202.frontend.data.model.response.auth

data class LoginResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String
)