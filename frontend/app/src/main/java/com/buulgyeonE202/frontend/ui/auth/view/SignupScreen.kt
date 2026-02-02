package com.buulgyeonE202.frontend.ui.auth.view

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // ✅ 추가
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.buulgyeonE202.frontend.ui.auth.model.AuthFlowType
import com.buulgyeonE202.frontend.ui.auth.model.AuthStep
import com.buulgyeonE202.frontend.ui.auth.viewmodel.AuthFlowViewModel

@Composable
fun SignupScreen(
    navController: NavController,
    viewModel: AuthFlowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            Toast.makeText(context, "회원가입 및 로그인 성공!", Toast.LENGTH_SHORT).show()

            // 홈 화면으로 이동하면서, 뒤로가기 눌러도 다시 로그인 화면으로 안 오게 스택 정리
            navController.navigate("gesture_home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val handleBack: () -> Unit = {
        if (uiState.step == AuthStep.EMAIL) {
            navController.popBackStack()
        } else {
            viewModel.onBack()
        }
    }

    when (uiState.step) {
        AuthStep.EMAIL -> AuthEmailPage(
            flowType = AuthFlowType.SIGN_UP,
            email = uiState.email,
            onEmailChange = viewModel::onEmailChange,

            // 기존엔 null이었음 -> uiState의 에러값으로 연결!
            emailError = uiState.emailError,

            onBack = handleBack,
            onNext = { viewModel.onPrimaryClick() }
        )
        AuthStep.CODE -> AuthCodePage(
            flowType = AuthFlowType.SIGN_UP,
            code = uiState.code,
            onCodeChange = { viewModel.onCodeChange(it) },
            secondsLeft = uiState.secondsLeft,

            codeError = uiState.codeError,

            onBack = handleBack,
            onNext = { viewModel.onPrimaryClick() }
        )
        AuthStep.PASSWORD -> AuthPasswordPage(
            flowType = AuthFlowType.SIGN_UP,
            password = uiState.password,
            passwordConfirm = uiState.passwordConfirm,
            onPasswordChange = viewModel::onPasswordChange,
            onPasswordConfirmChange = viewModel::onPasswordConfirmChange,

            passwordError = uiState.passwordError,
            passwordConfirmError = uiState.passwordConfirmError,

            onBack = handleBack,
            onDone = {
                viewModel.onPrimaryClick()
                // 성공 여부는 뷰모델 상태가 클리어되었는지로 판단 (혹은 별도 이벤트)
                if (uiState.passwordError == null && uiState.passwordConfirmError == null) {
                    // (참고: 실제론 여기서 바로 이동하기보다 ViewModel의 성공 이벤트를 구독하는 게 더 정확하지만, 현재 구조상 유지)
                }
            }
        )
    }
}