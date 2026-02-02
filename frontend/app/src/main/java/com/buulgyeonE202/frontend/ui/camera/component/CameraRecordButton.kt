package com.buulgyeonE202.frontend.ui.camera.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 바깥 흰색 링
    Box(
        modifier = modifier
            .size(78.dp)
            .border(width = 6.dp, color = Color.White, shape = CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // 안쪽 빨간 도형: 녹화 중이면 살짝 둥근 사각형, 아니면 원
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = Color(0xFFE53935),
                    shape = if (isRecording) RoundedCornerShape(8.dp) else CircleShape
                )
        )
    }
}
