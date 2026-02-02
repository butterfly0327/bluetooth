package com.buulgyeonE202.frontend.ui.auth.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.buulgyeonE202.frontend.ui.auth.flow.component.AuthTopBar // ✅ 추가
import com.buulgyeonE202.frontend.ui.component.CenteredConfirmPopup
import com.buulgyeonE202.frontend.ui.component.PrimaryButton
import com.buulgyeonE202.frontend.ui.theme.LightGray

@Composable
fun PasswordResetPage(
    onBack: () -> Unit = {},
    onSubmit: (newPw: String, confirmPw: String) -> Unit
) {
    // 상태 관리
    var newPassword by remember { mutableStateOf("") }
    var newPasswordConfirm by remember { mutableStateOf("") }
    var showConfirm by remember { mutableStateOf(false) }

    val componentModifier = Modifier
        .fillMaxWidth()
        .height(50.dp)

    BackHandler(onBack = onBack)

    // Scaffold 및 AuthTopBar 적용
    Scaffold(
        containerColor = LightGray,
        topBar = {
            AuthTopBar(
                title = "",
                progressText = "3/3", // 비밀번호 찾기 마지막 단계
                onBack = onBack
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 패딩 적용
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "비밀번호 재설정",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { Text("새 비밀번호", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = componentModifier,
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newPasswordConfirm,
                    onValueChange = { newPasswordConfirm = it },
                    placeholder = { Text("새 비밀번호 확인", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = componentModifier,
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "변경하기",
                    enabled = newPassword.isNotEmpty() && newPasswordConfirm.isNotEmpty(),
                    onClick = { showConfirm = true }
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            CenteredConfirmPopup(
                expanded = showConfirm,
                onDismissRequest = { showConfirm = false },
                title = "비밀번호 재설정",
                message = "비밀번호를 재설정하시겠습니까?",
                confirmText = "변경",
                onConfirm = {
                    showConfirm = false
                    onSubmit(newPassword, newPasswordConfirm)
                },
                onCancel = { showConfirm = false }
            )
        }
    }
}