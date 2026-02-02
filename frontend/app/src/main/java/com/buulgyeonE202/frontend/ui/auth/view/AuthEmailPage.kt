package com.buulgyeonE202.frontend.ui.auth.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold // ✅ 추가
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.auth.model.*
import com.buulgyeonE202.frontend.ui.auth.flow.component.AuthTopBar
import com.buulgyeonE202.frontend.ui.auth.flow.component.EmailStep
import com.buulgyeonE202.frontend.ui.component.PrimaryButton
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray

@Composable
fun AuthEmailPage(
    flowType: AuthFlowType,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String? = null,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val texts = textsFor(flowType, AuthStep.EMAIL)

    // ✅ 1. Scaffold 적용
    Scaffold(
        containerColor = LightGray,
        topBar = {
            // ✅ 2. TopBar 이동
            AuthTopBar(
                title = "",
                progressText = "1/3",
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

            EmailStep(
                mainTitle = texts.mainTitle,
                email = email,
                onEmailChange = onEmailChange,
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = texts.buttonText,
                onClick = onNext
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=800dp")
@Composable
private fun AuthEmailPagePreview_SignUp() {
    FrontendTheme {
        AuthEmailPage(
            flowType = AuthFlowType.SIGN_UP,
            email = "abc",
            onEmailChange = {},
            emailError = "이메일 형식이 올바르지 않습니다.",
            onBack = {},
            onNext = {}
        )
    }
}