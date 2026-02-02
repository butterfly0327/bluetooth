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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray
import com.buulgyeonE202.frontend.ui.theme.Primary500
import com.buulgyeonE202.frontend.ui.theme.White
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EmailStep(
    mainTitle: String,
    email: String,
    onEmailChange: (String) -> Unit,

    // ✅ 추가: 이메일 형식 에러 메시지(파라미터)
    errorMessage: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGray)
    ) {
        Text(
            text = mainTitle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(83.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),

            placeholder = {
                Text(
                    text = "example@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),

            // ✅ 추가: 에러 상태 표시
            isError = errorMessage != null,

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary500,
                unfocusedBorderColor = White,
                focusedContainerColor = White,
                unfocusedContainerColor = White
            )
        )

        // ✅ 추가: 에러 메시지 노출(있을 때만)
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailStepPreview() {
    FrontendTheme {
        EmailStep(
            mainTitle = "만나서 반가워요.\n이메일을 입력해주세요!",
            email = "example@example.com",
            onEmailChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmailStepPreview_Error() {
    FrontendTheme {
        EmailStep(
            mainTitle = "만나서 반가워요.\n이메일을 입력해주세요!",
            email = "abc",
            onEmailChange = {},
            errorMessage = "이메일 형식이 올바르지 않습니다."
        )
    }
}
