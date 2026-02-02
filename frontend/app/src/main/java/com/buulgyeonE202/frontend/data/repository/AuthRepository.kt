package com.buulgyeonE202.frontend.data.repository

import com.buulgyeonE202.frontend.data.api.AuthService
import com.buulgyeonE202.frontend.data.model.request.auth.EmailRequest
import com.buulgyeonE202.frontend.data.model.request.auth.EmailVerifyRequest
import com.buulgyeonE202.frontend.data.model.request.auth.LoginRequest
import com.buulgyeonE202.frontend.data.model.request.auth.PasswordChangeRequest
import com.buulgyeonE202.frontend.data.model.request.auth.SignupRequest
import com.buulgyeonE202.frontend.data.model.response.auth.CheckEmailResponse
import com.buulgyeonE202.frontend.data.model.response.auth.LoginResponse
import com.buulgyeonE202.frontend.data.model.response.auth.VerifyResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    // 1. 이메일 중복 체크
    suspend fun checkEmailDuplicate(email: String): Response<CheckEmailResponse> {
        return authService.checkEmailDuplicate(EmailRequest(email))
    }

    // 2. 로그인
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return authService.login(LoginRequest(email, password))
    }

    // 3. 인증코드 발송
    suspend fun sendCode(email: String): Response<Unit> {
        return authService.sendCode(EmailRequest(email))
    }

    // 4. 인증코드 검증
    suspend fun verifyCode(email: String, code: String): Response<VerifyResponse> {
        return authService.verifyCode(EmailVerifyRequest(email, otp = code))
    }

    // 5. 회원가입
    suspend fun signup(email: String, password: String, verificationToken: String?): Response<Unit> {
        return authService.signup(SignupRequest(email, password, verificationToken))
    }

    // 6. 비밀번호 변경
    suspend fun changePassword(current: String, next: String, token: String): Response<Unit> {
        return authService.changePassword(
            PasswordChangeRequest(current, next, token)
        )
    }

    // 7. 회원 탈퇴
    suspend fun withdraw(): Response<Unit> {
        return authService.withdraw()
    }

    // 8. 로그아웃
    suspend fun logout(): Response<Unit> {
        return authService.logout()
    }
}