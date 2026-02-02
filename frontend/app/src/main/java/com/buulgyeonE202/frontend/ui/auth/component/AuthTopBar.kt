package com.buulgyeonE202.frontend.ui.auth.flow.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme

/**
 * ✅ 상단바 (뒤로가기 + 진행도)
 * - title 제거됨 (시안처럼 상단 제목 없이 사용)
 */
@Composable
fun AuthTopBar(
    progressText: String,     // 오른쪽 진행도(예: 1/3)
    onBack: () -> Unit,        // 뒤로가기 눌렀을 때 실행할 동작
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()              // ✅ 상태바(시계 영역) 아래로 내려줌
            .padding(horizontal = 4.dp)       // ✅ 아이콘이 너무 붙지 않게
            .padding(top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ✅ 뒤로가기
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(44.dp)   // ✅ 터치 영역 확보
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ✅ 진행도
        Text(
            text = progressText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            modifier = Modifier.padding(end = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthTopBarPreview() {
    FrontendTheme {
        AuthTopBar(
            title = "",
            progressText = "1/3",
            onBack = {},
        )
    }
}
