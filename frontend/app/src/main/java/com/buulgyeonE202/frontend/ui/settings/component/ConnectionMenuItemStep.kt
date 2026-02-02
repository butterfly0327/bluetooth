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
 * ✅ ConnectionMenuItemStep
 * - SettingsMenuItemStep와 동일한 구조(Row-in-Row)
 * - 바깥 Row: 클릭 영역(전체폭 / 고정높이) + 세로 센터
 * - 안쪽 Row: 콘텐츠 컨테이너(fillMaxWidth) + padding으로 정렬선 관리
 * - 좌: title / 우: statusText
 * - ✅ 상태 판단은 Boolean(isConnected) 기준
 */
@Composable
fun ConnectionMenuItemStep(
    title: String,
    statusText: String,
    isConnected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 22.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ✅ 안쪽 Row = 콘텐츠 컨테이너
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.weight(1f))

            val statusColor =
                if (isConnected) {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f)
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                }

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
        }
    }
}

/* =========================
   🔍 PREVIEW
   ========================= */

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ConnectionMenuItemStepConnectedPreview() {
    FrontendTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            ConnectionMenuItemStep(
                title = "짐벌 연결",
                statusText = "연결됨",
                isConnected = true,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ConnectionMenuItemStepDisconnectedPreview() {
    FrontendTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            ConnectionMenuItemStep(
                title = "PC 연결",
                statusText = "미연결",
                isConnected = false,
                onClick = {}
            )
        }
    }
}
