package com.buulgyeonE202.frontend.ui.auth.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold // ✅ 추가
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.auth.model.*
import com.buulgyeonE202.frontend.ui.auth.flow.component.AuthTopBar
import com.buulgyeonE202.frontend.ui.auth.flow.component.PasswordStep
import com.buulgyeonE202.frontend.ui.component.PrimaryButton
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray

@Composable
fun AuthPasswordPage(
    flowType: AuthFlowType,
    password: String,
    passwordConfirm: String,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    passwordError: String? = null,
    passwordConfirmError: String? = null,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val texts = textsFor(flowType, AuthStep.PASSWORD)

    // ✅ 1. Scaffold 적용
    Scaffold(
        containerColor = LightGray,
        topBar = {
            // ✅ 2. TopBar 이동
            AuthTopBar(
                title = "",
                progressText = "3/3",
                onBack = onBack
            )
        }
    ) { innerPadding ->

        // ✅ 3. innerPadding 적용 (필수)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 상단바 영역만큼 띄움
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            PasswordStep(
                mainTitle = texts.mainTitle,
                password = password,
                passwordConfirm = passwordConfirm,
                onPasswordChange = onPasswordChange,
                onPasswordConfirmChange = onPasswordConfirmChange,
                passwordError = passwordError,
                passwordConfirmError = passwordConfirmError
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = texts.buttonText,
                onClick = onDone
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=800dp")
@Composable
private fun AuthPasswordPagePreview_SignUp() {
    FrontendTheme {
        AuthPasswordPage(
            flowType = AuthFlowType.SIGN_UP,
            password = "",
            passwordConfirm = "",
            onPasswordChange = {},
            onPasswordConfirmChange = {},
            passwordError = "비밀번호는 8자 이상이어야 합니다.",
            passwordConfirmError = "비밀번호가 일치하지 않습니다.",
            onBack = {},
            onDone = {}
        )
    }
}