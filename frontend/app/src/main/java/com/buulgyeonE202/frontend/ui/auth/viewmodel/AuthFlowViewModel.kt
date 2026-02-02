package com.buulgyeonE202.frontend.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.data.repository.AuthRepository
import com.buulgyeonE202.frontend.data.manager.TokenManager
import com.buulgyeonE202.frontend.ui.auth.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthFlowViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthFlowUiState())
    val uiState: StateFlow<AuthFlowUiState> = _uiState

    private var timerJob: Job? = null

    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email, emailError = null) } }
    fun onCodeChange(code: String) { _uiState.update { it.copy(code = code.take(4), codeError = null) } }
    fun onCurrentPasswordChange(pw: String) { _uiState.update { it.copy(currentPassword = pw, currentPasswordError = null) } }
    fun onPasswordChange(pw: String) { _uiState.update { it.copy(password = pw, passwordError = null) } }
    fun onPasswordConfirmChange(pw: String) { _uiState.update { it.copy(passwordConfirm = pw, passwordConfirmError = null) } }

    fun onPrimaryClick() {
        val state = _uiState.value
        when (state.step) {
            AuthStep.EMAIL -> handleEmailStep(state)
            AuthStep.CODE -> handleCodeStep(state)
            AuthStep.PASSWORD -> handlePasswordStep(state)
        }
    }

    // 설정창 진입 시 호출 (이메일이 있으면 바로 인증단계로)
    fun initFlow(type: AuthFlowType, email: String = "") {
        _uiState.update { it.copy(
            flowType = type,
            email = email,
            step = if (email.isNotEmpty()) AuthStep.CODE else AuthStep.EMAIL
        ) }

        if (email.isNotEmpty()) {
            sendVerificationCode(email)
        }
    }

    private fun handleEmailStep(state: AuthFlowUiState) {
        viewModelScope.launch {
            if (state.flowType == AuthFlowType.SIGN_UP) {
                val res = repository.checkEmailDuplicate(state.email)
                if (res.body()?.isDuplicate == true) {
                    _uiState.update { it.copy(emailError = "이미 가입된 이메일입니다.") }
                    return@launch
                }
            }
            sendVerificationCode(state.email)
        }
    }

    private fun sendVerificationCode(email: String) {
        _uiState.update { it.copy(secondsLeft = 180, step = AuthStep.CODE) }
        startTimer()

        viewModelScope.launch {
            try {
                val res = repository.sendCode(email)
                if (!res.isSuccessful) {
                    // 실패했을 때만 멈춤
                    stopTimer()
                    _uiState.update { it.copy(emailError = "발송 실패") }
                }
            } catch (e: Exception) {
                stopTimer()
            }
        }
    }

    private fun handleCodeStep(state: AuthFlowUiState) {
        viewModelScope.launch {
            val res = repository.verifyCode(state.email, state.code)
            if (res.isSuccessful) {
                stopTimer()
                _uiState.update { it.copy(step = AuthStep.PASSWORD, verificationToken = res.body()?.verificationToken) }
            } else {
                _uiState.update { it.copy(codeError = "인증번호가 일치하지 않습니다.") }
            }
        }
    }

    private fun handlePasswordStep(state: AuthFlowUiState) {
        viewModelScope.launch {
            when (state.flowType) {
                AuthFlowType.SIGN_UP -> {
                    val res = repository.signup(state.email, state.password, state.verificationToken)
                    if (res.isSuccessful) handleAutoLogin(state)
                }
                AuthFlowType.CHANGE_PASSWORD -> {
                    // 로그인 상태에서의 비밀번호 변경 (API 1.4)
                    val res = repository.changePassword(state.currentPassword, state.password, state.verificationToken ?: "")
                    if (res.isSuccessful) _uiState.update { it.copy(isComplete = true) }
                }
                AuthFlowType.RESET_PASSWORD -> {
                    // 비로그인 상태에서의 비밀번호 재설정 (API 1.5)
                    // 레포지토리에 resetPassword(email, newPw, token) 추가 필요
                    _uiState.update { it.copy(isComplete = true) }
                }
            }
        }
    }

    private fun handleAutoLogin(state: AuthFlowUiState) {
        viewModelScope.launch {
            val res = repository.login(state.email, state.password)
            if (res.isSuccessful && res.body() != null) {
                tokenManager.saveTokens(res.body()!!.accessToken, res.body()!!.refreshToken ?: "")
                _uiState.update { it.copy(navigateToHome = true) }
            }
        }
    }
    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (_uiState.value.secondsLeft > 0 && _uiState.value.step == AuthStep.CODE) {
                delay(1000)
                _uiState.update { it.copy(secondsLeft = (it.secondsLeft - 1).coerceAtLeast(0)) }
            }
        }
    }

    fun onBack() {
        _uiState.update { state ->
            when (state.step) {
                AuthStep.EMAIL -> state // 이메일 단계면 아무것도 안 함
                AuthStep.CODE -> {
                    stopTimer()
                    state.copy(step = AuthStep.EMAIL, codeError = null)
                }
                AuthStep.PASSWORD -> {
                    state.copy(step = AuthStep.CODE, passwordError = null)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}