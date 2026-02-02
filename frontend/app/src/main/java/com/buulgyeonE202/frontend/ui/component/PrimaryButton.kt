// 파일 위치: com.buulgyeonE202.frontend.ui.component.PrimaryButton.kt (패키지명 변경 확인!)
package com.buulgyeonE202.frontend.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.Primary500
import com.buulgyeonE202.frontend.ui.theme.White

/**
 * ✅ 공통 PrimaryButton
 * - enabled 속성 추가: 입력값이 없을 때 비활성화 처리 가능하도록 수정
 * - modifier 속성 추가: 외부에서 padding이나 위치 조정 가능하도록 수정
 */
@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier, // 🔥 외부에서 위치/여백 조정용
    enabled: Boolean = true,       // 🔥 비활성화 상태 제어용
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary500, // 보라색 (활성)
            contentColor = White,
            // 비활성화일 때 색상 (디자인 시안에 맞춘 연한 보라색)
            disabledContainerColor = Color(0xFFC8C6FA),
            disabledContentColor = White
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}