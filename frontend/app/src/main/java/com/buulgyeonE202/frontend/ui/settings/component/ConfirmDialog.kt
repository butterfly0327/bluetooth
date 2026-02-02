package com.buulgyeonE202.frontend.ui.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.Primary500
import com.buulgyeonE202.frontend.ui.theme.White

/**
 * ✅ ConfirmDialog (비밀번호 변경 확인 팝업)
 * - 시안: 상단 보라색 헤더 + 본문 + 하단 2버튼
 * - ✅ 취소 버튼: 연보라 배경 + 흰 글씨
 * - ✅ 확인 버튼: 진보라 배경 + 흰 글씨
 */
@Composable
fun ConfirmDialog(
    title: String = "비밀번호 변경 확인",
    message: String = "비밀번호를 변경하시겠습니까?",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(300.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        color = White
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ✅ 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Primary500),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = White,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // ✅ 본문
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
            )

            // ✅ 하단 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                // 취소 (연보라)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Primary500.copy(alpha = 0.35f))
                        .clickable(onClick = onCancel),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "취소",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White
                    )
                }

                // 확인 (진보라)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Primary500)
                        .clickable(onClick = onConfirm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "확인",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 360)
@Composable
private fun ConfirmDialogPreview() {
    FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            ConfirmDialog(
                onCancel = {},
                onConfirm = {}
            )
        }
    }
}
