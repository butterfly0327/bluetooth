package com.buulgyeonE202.frontend.ui.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // 🔥 추가
import androidx.compose.foundation.verticalScroll // 🔥 추가
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buulgyeonE202.frontend.ui.component.MainBottomBar
import com.buulgyeonE202.frontend.ui.component.ScrollableScreenTemplate
import com.buulgyeonE202.frontend.ui.settings.component.AccountInfoStep
import com.buulgyeonE202.frontend.ui.settings.component.ConnectionMenuItemStep
import com.buulgyeonE202.frontend.ui.theme.LightGray
import com.buulgyeonE202.frontend.ui.theme.White

@Composable
fun SettingsPage(
    navController: NavController,
    email: String,
    onPasswordChangeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    gimbalStatusText: String,
    pcStatusText: String,
    gimbalConnected: Boolean,
    pcConnected: Boolean,
    onGimbalClick: () -> Unit,
    onPcClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 스크롤 상태 기억
    val scrollState = rememberScrollState()

    ScrollableScreenTemplate(
        title = "Settings",
        onBackClick = null,
        bottomBar = {
            MainBottomBar(
                currentRoute = "setting",
                onNavigate = { route ->
                    if (route != "setting") {
                        navController.navigate(route) {
                            popUpTo("gesture_home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightGray)
                // 수직 스크롤 추가
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1) 계정 정보 섹션
            AccountInfoStep(
                email = email,
                onPasswordChangeClick = onPasswordChangeClick,
                onLogoutClick = onLogoutClick,
                onWithdrawClick = onWithdrawClick
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 2) 네트워크 통신 연결 섹션 타이틀
            Text(
                text = "네트워크 통신 연결",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            // 3) 네트워크 통신 연결 카드
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = White
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ConnectionMenuItemStep(
                        title = "짐벌 연결",
                        statusText = gimbalStatusText,
                        isConnected = gimbalConnected,
                        onClick = onGimbalClick
                    )

                    ConnectionMenuItemStep(
                        title = "PC 연결",
                        statusText = pcStatusText,
                        isConnected = pcConnected,
                        onClick = onPcClick
                    )
                }
            }

            // 🔥 하단 바에 가려지지 않도록 마지막에 여백 추가
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}