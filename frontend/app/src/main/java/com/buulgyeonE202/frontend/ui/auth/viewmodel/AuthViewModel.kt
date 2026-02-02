package com.buulgyeonE202.frontend.ui.auth.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.data.manager.TokenManager // ✅ TokenManager 추가
import com.buulgyeonE202.frontend.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager // 토큰 저장을 위해 주입
) : ViewModel() {

    private val _loginState = MutableStateFlow("IDLE")
    val loginState: StateFlow<String> = _loginState

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _verificationToken = MutableStateFlow<String?>(null)
    val verificationToken: StateFlow<String?> = _verificationToken

    private val _authCodeError = MutableStateFlow<String?>(null)
    val authCodeError: StateFlow<String?> = _authCodeError

    private val _isPasswordChanged = MutableStateFlow(false)
    val isPasswordChanged: StateFlow<Boolean> = _isPasswordChanged

    private val _isWithdrawn = MutableStateFlow(false)
    val isWithdrawn: StateFlow<Boolean> = _isWithdrawn

    fun clearLoginError() {
        _loginError.value = null
    }

    fun login(email: String, pw: String, isAutoLogin: Boolean) {
        // 1. 입력 형식 검증
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = pw.isNotBlank()

        if (!isEmailValid || !isPasswordValid) {
            _loginState.value = "ERROR"
            _loginError.value = "이메일 또는 비밀번호가 올바르지 않습니다."
            return
        }

        viewModelScope.launch {
            _loginState.value = "LOADING"
            _loginError.value = null

            try {
                val response = repository.login(email, pw)

                if (response.isSuccessful) {
                    val loginData = response.body()
                    if (loginData != null) {
                        // 토큰 저장
                        tokenManager.saveTokens(loginData.accessToken, loginData.refreshToken ?: "")
                        tokenManager.saveUserEmail(email)

                        // 자동 로그인 설정값 저장
                        Log.d("AUTH_TEST", "로그인 성공! 자동 로그인 설정: $isAutoLogin")
                        tokenManager.setAutoLogin(isAutoLogin)

                        _loginState.value = "SUCCESS"
                    } else {
                        Log.e("LOGIN", "데이터 없음")
                        _loginState.value = "ERROR"
                        _loginError.value = "아이디가 존재하지 않습니다."
                    }
                } else {
                    Log.e("LOGIN", "실패: ${response.code()}")
                    _loginState.value = "ERROR"
                    _loginError.value = "로그인 실패 (이메일, 비밀번호를 확인해주세요.)"
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "에러 발생: ${e.message}")
                _loginState.value = "ERROR_NETWORK"
                _loginError.value = "네트워크 연결이 불안정합니다."
            }
        }
    }

    // 앱 시작 시 자동 로그인 체크
    fun checkAutoLogin(onAuthorized: () -> Unit) {
        val token = tokenManager.getAccessToken()
        val isAuto = tokenManager.isAutoLogin()

        // 앱 켤 때 저장된 값 확인
        Log.d("AUTH_TEST", "자동 로그인 체크 - 토큰: ${token?.take(10)}..., 설정값: $isAuto")

        if (!token.isNullOrEmpty() && isAuto) {
            Log.d("AUTH_TEST", "✅ 조건 만족! 메인으로 이동")
            onAuthorized()
        } else {
            Log.d("AUTH_TEST", "❌ 조건 불만족. 로그인 화면 유지")
        }
    }   

    /**
     * 1.7 인증번호 발송
     */
    fun sendVerificationCode(email: String) {
        viewModelScope.launch {
            try {
                val response = repository.sendCode(email)
                if (response.isSuccessful) {
                    Log.d("AUTH", "인증번호 발송 성공")
                }
            } catch (e: Exception) {
                Log.e("AUTH", "발송 실패", e)
            }
        }
    }

    /**
     * 1.8 인증번호 확인 (성공 시 토큰 획득)
     */
    fun verifyCode(email: String, code: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.verifyCode(email, code)
                if (response.isSuccessful && response.body() != null) {
                    _verificationToken.value = response.body()?.verificationToken
                    _authCodeError.value = null
                    onSuccess() // 화면 이동 콜백
                } else {
                    _authCodeError.value = "인증번호가 올바르지 않습니다."
                }
            } catch (e: Exception) {
                _authCodeError.value = "네트워크 연결이 불안정합니다."
            }
        }
    }

    /**
     * 1.4 비밀번호 변경
     */
    fun changePassword(current: String, next: String, token: String) {
        viewModelScope.launch {
            try {
                val response = repository.changePassword(current, next, token)
                if (response.isSuccessful) {
                    _isPasswordChanged.value = true
                }
            } catch (e: Exception) {
                Log.e("AUTH", "비밀번호 변경 실패", e)
            }
        }
    }

    /**
     * 1.6 회원 탈퇴 로직
     */
    fun withdrawAccount() {
        viewModelScope.launch {
            try {
                // 1. 서버에 탈퇴 요청 (인터셉터에서 토큰이 자동으로 실려 나갑니다)
                val response = repository.withdraw()

                if (response.isSuccessful) {
                    // 2. 로컬 토큰 및 유저 정보 삭제
                    tokenManager.clearTokens()

                    // 3. 탈퇴 성공 신호 전송
                    _isWithdrawn.value = true
                    Log.d("AUTH", "✅ 회원 탈퇴 성공")
                } else {
                    Log.e("AUTH", "❌ 탈퇴 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AUTH", "❌ 네트워크 에러로 탈퇴 실패", e)
            }
        }
    }

    /**
     * 로그아웃
     * 1. 서버 API 호출 (토큰 무효화)
     * 2. 로컬 데이터(토큰, 자동로그인 설정) 삭제
     * 3. 로그인 화면으로 이동
     */
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // 1. 서버에 로그아웃 요청
                val response = repository.logout()

                if (response.isSuccessful) {
                    Log.d("AUTH", "서버 로그아웃 성공")
                } else {
                    Log.w("AUTH", "서버 로그아웃 실패: ${response.code()}")
                }

            } catch (e: Exception) {
                // 네트워크 에러가 나도 앱에서는 로그아웃 처리해야 함
                Log.e("AUTH", "로그아웃 통신 에러 (무시하고 진행)", e)
            } finally {
                // 2. 서버 응답과 관계없이 로컬 데이터는 무조건 삭제
                tokenManager.clearTokens()
                Log.d("AUTH", "로컬 데이터 삭제 완료")

                // 3. 화면 이동
                onLogoutSuccess()
            }
        }
    }
}