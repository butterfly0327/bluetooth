package com.buulgyeonE202.frontend.ui.auth.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.* // ✅ remember, LaunchedEffect 추가
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester // ✅ 추가
import androidx.compose.ui.platform.LocalSoftwareKeyboardController // ✅ 추가
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.auth.model.*
import com.buulgyeonE202.frontend.ui.auth.flow.component.AuthTopBar
import com.buulgyeonE202.frontend.ui.auth.flow.component.CodeStep
import com.buulgyeonE202.frontend.ui.component.PrimaryButton
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray
import kotlinx.coroutines.delay

@Composable
fun AuthCodePage(
    flowType: AuthFlowType,
    code: String,
    onCodeChange: (String) -> Unit,
    secondsLeft: Int,
    codeError: String? = null,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val texts = textsFor(flowType, AuthStep.CODE)

    // 포커스 제어 및 키보드 컨트롤러 생성
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 화면 진입 시 자동 포커스 및 키보드 올리기
    LaunchedEffect(Unit) {
        // 화면에 들어오자마자 입력값을 빈 문자열로 리셋
        onCodeChange("")
        // 컴포지션이 완료된 후 약간의 딜레이를 주어야 안정적으로 포커스가 잡힘
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        containerColor = LightGray,
        topBar = {
            AuthTopBar(
                title = "",
                progressText = "2/3",
                onBack = onBack
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // focusRequester와 onDone(엔터 동작) 전달
            CodeStep(
                mainTitle = texts.mainTitle,
                code = code,
                onCodeChange = onCodeChange,
                codeLength = 4,
                secondsLeft = secondsLeft,
                errorMessage = codeError,
                focusRequester = focusRequester,
                onDone = {
                    keyboardController?.hide() // 키보드 내리고
                    onNext() // 다음 단계로 이동
                }
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