package com.buulgyeonE202.frontend.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // 로그인 상태를 관리하는 변수 (로딩중? 성공? 실패?)
    private val _loginState = MutableStateFlow<String>("IDLE") // 초기 상태
    val loginState: StateFlow<String> = _loginState

    fun login(email: String, pw: String) {
        viewModelScope.launch {
            _loginState.value = "LOADING" // 로딩 시작

            try {
                // 서버 통신 시도
                val response = repository.login(email, pw)

                if (response.isSuccessful) {
                    val tokens = response.body()
                    Log.d("LOGIN", "성공! AccessToken: ${tokens?.accessToken}")
                    _loginState.value = "SUCCESS"
                    // TODO: 여기서 토큰 저장 (TokenManager) 필요
                } else {
                    Log.e("LOGIN", "실패: ${response.code()}")
                    _loginState.value = "ERROR"
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "에러 발생: ${e.message}")
                // 서버가 꺼져있으면 여기서 에러 (ConnectException)
                _loginState.value = "ERROR_NETWORK"
            }
        }
    }
}