package com.buulgyeonE202.frontend.ui.settings.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme

/**
 * ✅ SettingsMenuItemStep (통합 Row 컴포넌트)
 * - title만 있으면: 일반 메뉴 Row
 * - description이 있으면: 이메일처럼 2줄 정보 Row
 * - 바깥 Row: 터치 영역
 * - 안쪽 Row: 콘텐츠 컨테이너 (padding / 정렬선 관리)
 */
@Composable
fun SettingsMenuItemStep(
    title: String,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 22.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (description == null) 56.dp else 72.dp)
            .let {
                if (onClick != null) it.clickable(onClick = onClick) else it
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ✅ 콘텐츠 컨테이너
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (description == null) {
                // 🔹 일반 메뉴
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                // 🔹 정보형 Row (이메일)
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SettingsMenuItemStepPreview() {
    FrontendTheme {
        Column {
            SettingsMenuItemStep(
                title = "이메일",
                description = "abcd@naver.com"
            )
            SettingsMenuItemStep(title = "비밀번호 변경", onClick = {})
            SettingsMenuItemStep(title = "로그아웃", onClick = {})
            SettingsMenuItemStep(title = "회원탈퇴", onClick = {})

        }
    }
}
