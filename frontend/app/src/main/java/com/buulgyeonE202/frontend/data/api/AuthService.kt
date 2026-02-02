package com.buulgyeonE202.frontend.data.api

import com.buulgyeonE202.frontend.data.model.request.EmailVerifyRequest
import com.buulgyeonE202.frontend.data.model.request.LoginRequest
import com.buulgyeonE202.frontend.data.model.request.SignupRequest
import com.buulgyeonE202.frontend.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    // 로그인
    @POST("v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 회원가입 (이메일, 비번 전송 -> 서버에서 인증메일 발송)
    @POST("v1/auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<Unit>

    // 이메일 인증번호 확인
    @POST("v1/auth/verify")
    suspend fun verifyEmail(
        @Body request: EmailVerifyRequest
    ): Response<Unit>
}