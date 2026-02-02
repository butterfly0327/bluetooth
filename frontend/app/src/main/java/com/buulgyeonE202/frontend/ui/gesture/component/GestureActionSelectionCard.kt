package com.buulgyeonE202.frontend.ui.gesture.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buulgyeonE202.frontend.ui.gesture.model.GestureActionItem

@Composable
fun GestureActionSelectionCard(
    action: GestureActionItem,
    shape: Shape,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp), // 최소 높이 설정
        color = Color.White,
        shape = shape,
        onClick = onClick
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 15.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp)) // 상단 여백

                // 제목
                Text(
                    text = action.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(7.dp)) // 텍스트 간 간격

                // 설명 (서버 데이터가 비어있으면 빈 문자열)
                Text(
                    text = action.description ?: "",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(26.dp)) // 하단 여백
            }

            if (showDivider) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    thickness = 0.5.dp,
                    color = Color(0xFFF0F0F0)
                )
            }
        }
    }
}