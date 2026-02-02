package com.buulgyeonE202.frontend.ui.settings.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold // ✅ 추가
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.auth.flow.component.AuthTopBar // ✅ 추가
import com.buulgyeonE202.frontend.ui.component.CenteredConfirmPopup
import com.buulgyeonE202.frontend.ui.component.PrimaryButton
import com.buulgyeonE202.frontend.ui.settings.component.PasswordChangeStep
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray

@Composable
fun PasswordChangePage(
    onBack: () -> Unit = {},
    onSubmit: (current: String, next: String, nextConfirm: String) -> Unit = { _, _, _ -> }
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newPasswordConfirm by remember { mutableStateOf("") }

    var showConfirm by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = LightGray,
        topBar = {
            AuthTopBar(
                title = "",
                progressText = "", // 단일 화면이므로 비워둠
                onBack = onBack
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // ✅ 패딩 적용
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // ✅ 기존 Spacer(72.dp) 제거하고 적당한 간격만 유지
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "비밀번호 변경",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                PasswordChangeStep(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    newPasswordConfirm = newPasswordConfirm,
                    onCurrentPasswordChange = { currentPassword = it },
                    onNewPasswordChange = { newPassword = it },
                    onNewPasswordConfirmChange = { newPasswordConfirm = it }
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "비밀번호 변경",
                    onClick = { showConfirm = true }
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            CenteredConfirmPopup(
                expanded = showConfirm,
                onDismissRequest = { showConfirm = false },
                title = "비밀번호 변경 확인",
                message = "비밀번호를 변경하시겠습니까?",
                confirmText = "변경하기",
                onConfirm = {
                    showConfirm = false
                    onSubmit(currentPassword, newPassword, newPasswordConfirm)
                },
                onCancel = { showConfirm = false }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun PasswordChangePagePreview() {
    FrontendTheme {
        PasswordChangePage()
    }
}