package com.buulgyeonE202.frontend.ui.auth.model

data class AuthFlowUiState(
    val flowType: AuthFlowType = AuthFlowType.SIGN_UP,
    val step: AuthStep = AuthStep.EMAIL,
    val email: String = "",
    val emailError: String? = null,

    val code: String = "",
    val codeLength: Int = 4,
    val secondsLeft: Int = 180,
    val codeError: String? = null,

    val verificationToken: String? = null,

    val currentPassword: String = "",
    val currentPasswordError: String? = null,
    val password: String = "",
    val passwordConfirm: String = "",
    val passwordError: String? = null,
    val passwordConfirmError: String? = null,

    val navigateToHome: Boolean = false,
    val isComplete: Boolean = false
)