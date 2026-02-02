// ui/auth/view/LoginScreen.kt

package com.buulgyeonE202.frontend.ui.auth.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.buulgyeonE202.frontend.ui.auth.viewmodel.AuthViewModel
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray

// 실제 기능 (Hilt)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    onFindPasswordClick: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    // 1. 로그인 상태가 "SUCCESS"로 바뀌면 화면 이동
    LaunchedEffect(loginState) {
        if (loginState == "SUCCESS") {
            navController.navigate("gesture_home") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }

    // 2. 자동 로그인 체크
    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin {
            navController.navigate("gesture_home") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }

    LoginContent(
        onLoginClick = { email, password, isAuto ->
            viewModel.login(email, password, isAuto)
        },
        onSignupClick = {
            navController.navigate("signup")
        },
        onFindPasswordClick = onFindPasswordClick,
        isAutoLoginChecked = false,
        onAutoLoginChange = {},
        loginError = loginError,
        onInputChange = { viewModel.clearLoginError() }
    )
}

// 순수 디자인
@Composable
fun LoginContent(
    onLoginClick: (String, String, Boolean) -> Unit,
    onSignupClick: () -> Unit,
    onFindPasswordClick: () -> Unit,
    isAutoLoginChecked: Boolean,
    onAutoLoginChange: (Boolean) -> Unit,
    loginError: String? = null,
    onInputChange: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAutoLogin by remember { mutableStateOf(isAutoLoginChecked) }

    val componentModifier = Modifier
        .width(355.dp)
        .height(49.dp)

    Scaffold(
        containerColor = LightGray
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Galaxy AI\nPresentation",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "in Your Hand",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(60.dp))

            // 이메일 입력
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    onInputChange()
                },
                placeholder = {
                    Text("이메일", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                },
                modifier = componentModifier,
                shape = RoundedCornerShape(30.dp),
                isError = loginError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호 입력
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    onInputChange()
                },
                placeholder = {
                    Text("비밀번호", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = componentModifier,
                shape = RoundedCornerShape(30.dp),
                isError = loginError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true
            )

            if (loginError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = loginError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .width(355.dp)
                        .padding(start = 6.dp)
                )
            }

            // 원형 체크박스 및 찾기 링크
            Row(
                modifier = Modifier
                    .width(355.dp)
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        isAutoLogin = !isAutoLogin
                        onAutoLoginChange(isAutoLogin)
                    }
                ) {
                    if (isAutoLogin) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "선택됨",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(width = 1.dp, color = Color.Gray, shape = CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "자동로그인",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                // 텍스트 변경 및 클릭 연결
                Text(
                    text = "비밀번호 찾기",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onFindPasswordClick() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onLoginClick(email, password, isAutoLogin)
                },
                modifier = componentModifier,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "로그인",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "계정이 없다면? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "지금 가입하기",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onSignupClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FrontendTheme {
        LoginContent(
            onLoginClick = { _, _, _ -> },
            onSignupClick = {},
            onFindPasswordClick = {}, // 🔥 빈 함수 전달
            isAutoLoginChecked = false,
            onAutoLoginChange = {},
            loginError = "이메일 또는 비밀번호가 올바르지 않습니다."
        )
    }
}