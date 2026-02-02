package com.buulgyeonE202.frontend.data.manager

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // 토큰 저장
    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().apply {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            apply()
        }
    }

    // 이메일 저장 (로그인 성공 시 호출)
    fun saveUserEmail(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    // 액세스 토큰 가져오기
    fun getAccessToken(): String? {
        return prefs.getString("ACCESS_TOKEN", null)
    }

    // 이메일 가져오기 (설정 화면에서 사용)
    // 값이 없을 경우 "정보 없음" 또는 빈 문자열을 반환하도록 설정.
    fun getUserEmail(): String {
        return prefs.getString("USER_EMAIL", "정보 없음") ?: "정보 없음"
    }

    // 토큰 및 모든 정보 삭제 (로그아웃 시)
    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    // 자동 로그인 설정 저장
    fun setAutoLogin(isAuto: Boolean) {
        prefs.edit().putBoolean("IS_AUTO_LOGIN", isAuto).apply()
    }

    // 자동 로그인 설정 확인
    fun isAutoLogin(): Boolean {
        return prefs.getBoolean("IS_AUTO_LOGIN", false)
    }
}