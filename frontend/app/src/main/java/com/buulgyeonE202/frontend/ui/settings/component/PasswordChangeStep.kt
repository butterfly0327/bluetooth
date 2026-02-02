package com.buulgyeonE202.frontend.ui.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray
import com.buulgyeonE202.frontend.ui.theme.Primary500
import com.buulgyeonE202.frontend.ui.theme.White

/**
 * ✅ PasswordChangeStep
 * - 비밀번호 변경 화면의 입력 덩어리
 * - 현재 비밀번호 / 새 비밀번호 / 새 비밀번호 확인
 * - auth PasswordStep과 같은 라운드/높이/색감
 */
@Composable
fun PasswordChangeStep(
    currentPassword: String,
    newPassword: String,
    newPasswordConfirm: String,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onNewPasswordConfirmChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        LabelText("현재 비밀번호")
        Spacer(modifier = Modifier.height(10.dp))
        RoundPasswordInput(
            value = currentPassword,
            onValueChange = onCurrentPasswordChange
        )

        Spacer(modifier = Modifier.height(22.dp))

        LabelText("새 비밀번호")
        Spacer(modifier = Modifier.height(10.dp))
        RoundPasswordInput(
            value = newPassword,
            onValueChange = onNewPasswordChange
        )

        Spacer(modifier = Modifier.height(22.dp))

        LabelText("새 비밀번호 확인")
        Spacer(modifier = Modifier.height(10.dp))
        RoundPasswordInput(
            value = newPasswordConfirm,
            onValueChange = onNewPasswordConfirmChange
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
private fun RoundPasswordInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        visualTransformation = PasswordVisualTransformation(),
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
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun PasswordChangeStepPreview() {
    FrontendTheme {
        val (cur, setCur) = remember { mutableStateOf("") }
        val (next, setNext) = remember { mutableStateOf("") }
        val (nextC, setNextC) = remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
                .padding(top = 40.dp)
        ) {
            PasswordChangeStep(
                currentPassword = cur,
                newPassword = next,
                newPasswordConfirm = nextC,
                onCurrentPasswordChange = setCur,
                onNewPasswordChange = setNext,
                onNewPasswordConfirmChange = setNextC
            )
        }
    }
}
