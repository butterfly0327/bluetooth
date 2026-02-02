package com.buulgyeonE202.frontend.data.model.request.auth

data class SignupRequest(
    val email: String,
    val password: String,
    val verificationToken: String? = null
)