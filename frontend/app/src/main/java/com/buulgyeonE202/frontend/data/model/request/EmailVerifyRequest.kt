package com.buulgyeonE202.frontend.data.model.request

data class EmailVerifyRequest(
    val email: String,
    val code: String // 인증번호 6자리
)