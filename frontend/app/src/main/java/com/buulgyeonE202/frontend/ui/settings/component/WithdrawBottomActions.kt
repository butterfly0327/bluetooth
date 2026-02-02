package com.buulgyeonE202.frontend.ui.settings.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.Primary500

@Composable
fun WithdrawBottomActions(
    enabled: Boolean,
    onStay: () -> Unit,
    onWithdraw: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 남을게요
        Text(
            text = "남을게요",
            style = MaterialTheme.typography.titleMedium,
            color = Primary500,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onStay),
            textAlign = TextAlign.Center   // ✅ 가운데 정렬
        )

        // 탈퇴하기
        Text(
            text = "탈퇴하기",
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled)
                Color(0xFFE06B6B)
            else
                Color(0xFFE06B6B).copy(alpha = 0.45f),
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = enabled, onClick = onWithdraw),
            textAlign = TextAlign.Center   // ✅ 가운데 정렬
        )
    }
}


/* ===========================
   🔍 PREVIEW
   =========================== */

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun WithdrawBottomActionsEnabledPreview() {
    FrontendTheme {
        WithdrawBottomActions(
            enabled = true,
            onStay = {},
            onWithdraw = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun WithdrawBottomActionsDisabledPreview() {
    FrontendTheme {
        WithdrawBottomActions(
            enabled = false,
            onStay = {},
            onWithdraw = {}
        )
    }
}
