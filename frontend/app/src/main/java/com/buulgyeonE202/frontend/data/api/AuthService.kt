package com.buulgyeonE202.frontend.data.api

import com.buulgyeonE202.frontend.data.model.request.auth.EmailRequest
import com.buulgyeonE202.frontend.data.model.request.auth.EmailVerifyRequest
import com.buulgyeonE202.frontend.data.model.request.auth.LoginRequest
import com.buulgyeonE202.frontend.data.model.request.auth.PasswordChangeRequest
import com.buulgyeonE202.frontend.data.model.request.auth.SignupRequest
import com.buulgyeonE202.frontend.data.model.response.auth.CheckEmailResponse
import com.buulgyeonE202.frontend.data.model.response.auth.LoginResponse
import com.buulgyeonE202.frontend.data.model.response.auth.VerifyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthService {

    // 1. 로그인
    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // 2. 이메일 중복 확인
    @POST("v1/auth/signup/checkemail")
    suspend fun checkEmailDuplicate(@Body request: EmailRequest): Response<CheckEmailResponse>

    // 3. 인증코드 발송
    @POST("v1/auth/sendcode")
    suspend fun sendCode(@Body request: EmailRequest): Response<Unit>

    // 4. 인증코드 검증
    @POST("v1/auth/verifycode")
    suspend fun verifyCode(@Body request: EmailVerifyRequest): Response<VerifyResponse>

    // 5. 회원가입
    @POST("v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Unit>

    // 6. 비밀번호 변경
    @POST("v1/auth/password/change")
    suspend fun changePassword(@Body request: PasswordChangeRequest): Response<Unit>

    // 7. 회원 탈퇴
    @DELETE("v1/auth/delete")
    suspend fun withdraw(): Response<Unit>

    // 8. 로그 아웃
    @POST("v1/auth/logout")
    suspend fun logout(): Response<Unit>
}