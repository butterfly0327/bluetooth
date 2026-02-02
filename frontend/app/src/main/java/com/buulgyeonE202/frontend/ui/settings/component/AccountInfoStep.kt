package com.buulgyeonE202.frontend.ui.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray
import com.buulgyeonE202.frontend.ui.theme.White

private val CardHorizontalPadding = 22.dp

/**
 * ✅ AccountInfoStep (설정 화면 - 계정 정보 카드)
 * - 순서: 이메일 / 비밀번호 변경 / 로그아웃 / 회원탈퇴
 * - 이메일도 SettingsMenuItemStep(title/description)으로 통일
 * - 메뉴 Row들은 "안쪽 Row 컨테이너"가 padding을 관리
 */
@Composable
fun AccountInfoStep(
    email: String,
    onPasswordChangeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // 카드 밖 타이틀
        Text(
            text = "계정 정보",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        // 카드
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = White
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // 1) 이메일 (description이 있으면 2줄 표시)
                SettingsMenuItemStep(
                    title = "이메일",
                    description = email,
                    horizontalPadding = CardHorizontalPadding
                )

                // 2) 비밀번호 변경
                SettingsMenuItemStep(
                    title = "비밀번호 변경",
                    onClick = onPasswordChangeClick,
                    horizontalPadding = CardHorizontalPadding
                )

                // 3) 로그아웃
                SettingsMenuItemStep(
                    title = "로그아웃",
                    onClick = onLogoutClick,
                    horizontalPadding = CardHorizontalPadding
                )

                // 4) 회원탈퇴
                SettingsMenuItemStep(
                    title = "회원탈퇴",
                    onClick = onWithdrawClick,
                    horizontalPadding = CardHorizontalPadding
                )

            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 520)
@Composable
private fun AccountInfoStepPreview() {
    FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
                .padding(20.dp)
        ) {
            AccountInfoStep(
                email = "abcd@naver.com",
                onPasswordChangeClick = {},
                onLogoutClick = {},
                onWithdrawClick = {}
            )
        }
    }
}
