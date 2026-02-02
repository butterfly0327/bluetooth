package com.buulgyeonE202.frontend.data.repository

import com.buulgyeonE202.frontend.data.api.AuthService
import com.buulgyeonE202.frontend.data.model.request.LoginRequest
import com.buulgyeonE202.frontend.data.model.response.LoginResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    // 로그인 함수
    suspend fun login(email: String, pw: String): Response<LoginResponse> {
        return authService.login(LoginRequest(email, pw))
    }
}