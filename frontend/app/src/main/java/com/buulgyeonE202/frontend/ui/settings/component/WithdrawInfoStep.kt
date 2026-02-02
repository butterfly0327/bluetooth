package com.buulgyeonE202.frontend.ui.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.Primary500

@Composable
fun WithdrawInfoStep(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        // 큰 문구
        Text(
            text = "\"정말 떠나시나요?\"",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 설명
        Text(
            text = "탈퇴하시면 다음 정보가 즉시 삭제되며 복구할 수 없습니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 리스트
        BulletRow(emoji = "📁", text = "저장된 프레젠테이션 영상")
        Spacer(modifier = Modifier.height(10.dp))
        BulletRow(emoji = "⚙️", text = "커스텀 제스처 설정")
        Spacer(modifier = Modifier.height(10.dp))
        BulletRow(emoji = "📊", text = "발표 연습 기록 및 분석 리포트")

        Spacer(modifier = Modifier.height(22.dp))

        // 체크 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleCheck(
                checked = checked,
                onCheckedChange = onCheckedChange
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "회원 탈퇴 유의사항을 확인하였으며 동의합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun BulletRow(
    emoji: String,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "•", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = emoji, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun CircleCheck(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    if (checked) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(1.dp, Primary500, CircleShape)
                .clickable { onCheckedChange(false) },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(Primary500, CircleShape)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(1.dp, Color.Gray, CircleShape)
                .clickable { onCheckedChange(true) }
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun WithdrawInfoStepPreview() {
    FrontendTheme {
        val (checked, setChecked) = remember { mutableStateOf(false) }

        WithdrawInfoStep(
            checked = checked,
            onCheckedChange = setChecked
        )
    }
}
