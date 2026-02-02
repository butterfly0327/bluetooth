// GestureActionItemCard.kt

package com.buulgyeonE202.frontend.ui.gesture.component

import androidx.compose.foundation.ExperimentalFoundationApi // ✅ 추가
import androidx.compose.foundation.combinedClickable // ✅ 추가
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buulgyeonE202.frontend.ui.gesture.model.GestureDetailItem

@OptIn(ExperimentalFoundationApi::class) // ✅ combinedClickable 사용을 위해 필요
@Composable
fun GestureActionItemCard(
    action: GestureDetailItem,
    isEditMode: Boolean, // ✅ 편집 모드 여부 받기
    onClick: () -> Unit,
    onLongClick: () -> Unit, // ✅ 롱클릭 콜백 추가
    onDeleteClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            // ✅ combinedClickable로 변경 (클릭 & 롱클릭 모두 처리)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null, // 리플 효과 제거
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.actionName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = action.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ✅ 편집 모드일 때만 삭제 버튼 표시
            if (isEditMode) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color.LightGray // 또는 Color.Red
                    )
                }
            }
        }
    }
}