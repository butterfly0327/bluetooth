package com.buulgyeonE202.frontend.ui.auth.model

enum class AuthFlowType {
    SIGN_UP,         // 회원가입
    RESET_PASSWORD,  // 비밀번호 찾기 (비로그인)
    CHANGE_PASSWORD  // 비밀번호 변경 (로그인 상태)
}