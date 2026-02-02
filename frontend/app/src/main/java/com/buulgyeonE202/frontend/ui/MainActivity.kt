package com.buulgyeonE202.frontend.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buulgyeonE202.frontend.ui.auth.LoginScreen
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), // 화면 전체 채우기
                    color = MaterialTheme.colorScheme.background // 배경색 적용
                ) {
                    val navController = rememberNavController()

                    // 네비게이션 호스트
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        // 1. 로그인 화면
                        composable("login") {
                            LoginScreen(navController = navController)
                        }

                        // 2. 회원가입 화면 (임시)
                        composable("signup") {
                            PlaceholderScreen("회원가입 화면")
                        }

                        // 3. 메인 홈 화면 (임시)
                        composable("gesture_home") {
                            PlaceholderScreen("제스처 홈 화면")
                        }
                    }
                }
            }
        }
    }
}

// 빈 화면용 컴포넌트
@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}