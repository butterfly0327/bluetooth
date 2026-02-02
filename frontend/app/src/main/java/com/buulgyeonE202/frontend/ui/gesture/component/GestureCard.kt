package com.buulgyeonE202.frontend.ui.gesture.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buulgyeonE202.frontend.ui.gesture.model.GestureItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GestureCard(
    item: GestureItem,
    isEditMode: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onFavoriteClick: () -> Unit,
    dragModifier: Modifier = Modifier
) {
    val borderColor = if (isEditMode && item.isSelected) Color(0xFF7C4DFF) else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(138.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(2.dp, borderColor, RoundedCornerShape(24.dp))
            .then(dragModifier)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp)
    ) {

        // --- 1. 상단 아이콘 (체크박스 / 즐겨찾기) : 기존 유지 ---
        if (isEditMode) {
            Icon(
                imageVector = if (item.isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                contentDescription = "Select",
                tint = if (item.isSelected) Color(0xFF7C4DFF) else Color.Gray,
                modifier = Modifier.align(Alignment.TopStart).size(24.dp)
            )
        } else {
            if (item.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favorite",
                    tint = Color(0xFF7C4DFF),
                    modifier = Modifier.align(Alignment.TopEnd).combinedClickable(onClick = onFavoriteClick)
                )
            }
        }

        // --- 2. 텍스트 그룹 (Column으로 변경) ---
        Column(
            // 🔥 핵심 1: Column 전체를 바닥에 붙임 (위쪽 공간 확보)
            modifier = Modifier.align(Alignment.BottomStart),
            // (선택) 만약 내용물이 왼쪽 정렬이 안 되면 추가: horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color.Black
            )

            // 🔥 핵심 2: 제목과 하단 정보 사이 간격을 좁게 설정 (4dp)
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WavingHand,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.count}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}