// ui/gesture/component/GestureSelectRow.kt
package com.buulgyeonE202.frontend.ui.gesture.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buulgyeonE202.frontend.ui.gesture.model.GestureOption

@Composable
fun GestureSelectRow(
    option: GestureOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 영역 (GestureOption의 imageRes 활용)
        Box(
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF9F9F9)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = option.imageRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 제스처 이름
        Text(
            text = option.name,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            color = if (isSelected) Color.Black else Color.Gray
        )

        // 체크 박스 아이콘
        Icon(
            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF7C4DFF) else Color(0xFFDDDDDD),
            modifier = Modifier.size(24.dp)
        )
    }
}