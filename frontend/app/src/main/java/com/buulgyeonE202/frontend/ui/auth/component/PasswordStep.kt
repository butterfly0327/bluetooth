package com.buulgyeonE202.frontend.ui.auth.flow.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray
import com.buulgyeonE202.frontend.ui.theme.Primary500
import com.buulgyeonE202.frontend.ui.theme.White

@Composable
fun PasswordStep(
    mainTitle: String,
    password: String,
    passwordConfirm: String,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,

    // ✅ 추가: 에러 파라미터
    passwordError: String? = null,
    passwordConfirmError: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGray)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = mainTitle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(83.dp))

        LabelText("비밀번호")
        Spacer(modifier = Modifier.height(10.dp))
        RoundInput(
            value = password,
            onValueChange = onPasswordChange,
            isPassword = true,
            isError = passwordError != null,
            supportText = passwordError
        )

        Spacer(modifier = Modifier.height(26.dp))

        LabelText("비밀번호 확인")
        Spacer(modifier = Modifier.height(10.dp))
        RoundInput(
            value = passwordConfirm,
            onValueChange = onPasswordConfirmChange,
            isPassword = true,
            isError = passwordConfirmError != null,
            supportText = passwordConfirmError
        )
    }
}

@Composable
private fun LabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),
        modifier = Modifier.padding(start = 6.dp)
    )
}

@Composable
private fun RoundInput(
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean,

    // ✅ 추가
    isError: Boolean,
    supportText: String?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                disabledContainerColor = White,

                unfocusedBorderColor = White,
                disabledBorderColor = White,
                focusedBorderColor = Primary500,
                cursorColor = Primary500
            )
        )

        if (supportText != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = supportText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 520)
@Composable
fun PasswordStepPreview() {
    FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
        ) {
            PasswordStep(
                mainTitle = "비밀번호를 입력해주세요.",
                password = "",
                passwordConfirm = "",
                onPasswordChange = {},
                onPasswordConfirmChange = {},
                passwordError = "비밀번호는 8자 이상이어야 합니다.",
                passwordConfirmError = "비밀번호가 일치하지 않습니다."
            )
        }
    }
}
