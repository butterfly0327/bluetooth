package com.buulgyeonE202.frontend.ui.auth

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

// 실제 기능 (Hilt)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState == "SUCCESS") {
            navController.navigate("gesture_home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LoginContent(
        onLoginClick = { email, password ->
            viewModel.login(email, password)
        },
        onSignupClick = {
            navController.navigate("signup")
        },
        isAutoLoginChecked = false,
        onAutoLoginChange = {}
    )
}

// 순수 디자인
@Composable
fun LoginContent(
    onLoginClick: (String, String) -> Unit,
    onSignupClick: () -> Unit,
    isAutoLoginChecked: Boolean,
    onAutoLoginChange: (Boolean) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAutoLogin by remember { mutableStateOf(isAutoLoginChecked) }

    val componentModifier = Modifier
        .width(355.dp)
        .height(49.dp)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                onValueChange = { email = it },
                placeholder = {
                    Text("이메일", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                },
                modifier = componentModifier, // [수정] 355x49 적용
                shape = RoundedCornerShape(30.dp),
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
                onValueChange = { password = it },
                placeholder = {
                    Text("비밀번호", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                },
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
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true
            )

            // 원형 체크박스
            Row(
                modifier = Modifier
                    .width(355.dp) // 입력창과 동일한 너비 안에서 정렬
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 원형 체크박스 + 텍스트 묶음
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
                            // [선택] 보라색 배경 + 흰색 체크 아이콘
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check, // 기본 라이브러리에 있는 Check 사용
                                    contentDescription = "선택됨",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            // [선택 안 됨] 회색 테두리 원
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

                    Text(
                        text = "아이디/비밀번호 찾기",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.clickable { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 로그인 버튼
            Button(
                onClick = { onLoginClick(email, password) },
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

            // 회원가입 링크
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
    com.buulgyeonE202.frontend.ui.theme.FrontendTheme {
        LoginContent(
            onLoginClick = { _, _ -> },
            onSignupClick = {},
            isAutoLoginChecked = false,
            onAutoLoginChange = {}
        )
    }
}