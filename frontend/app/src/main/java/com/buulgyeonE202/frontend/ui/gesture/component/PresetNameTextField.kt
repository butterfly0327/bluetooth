package com.buulgyeonE202.frontend.ui.gesture.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PresetNameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "프리셋 이름",
    placeholder: String = "",
    enabled: Boolean = true,

    // 옵션 유지
    isError: Boolean = false,
    supportingText: String? = null,
    maxLength: Int? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null,
) {
    // 테마 기반 색상
    val borderColor = MaterialTheme.colorScheme.primary
    val containerBg = MaterialTheme.colorScheme.surface

    // X 버튼 스타일
    val clearBg = Color(0xFFD9D9D9)
    val clearIconTint = Color.White

    val fieldShape = RoundedCornerShape(28.dp)
    val borderWidth = 2.dp

    // 입력/플레이스홀더 공통 텍스트 스타일 (줄인 크기)
    val inputTextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (enabled) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // 라벨
        Text(
            text = label,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(fieldShape)
                .background(containerBg)
                .border(borderWidth, borderColor, fieldShape)
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 입력 영역
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = { newValue ->
                            val clipped = maxLength?.let { newValue.take(it) } ?: newValue
                            onValueChange(clipped)
                        },
                        enabled = enabled,
                        singleLine = true,
                        textStyle = inputTextStyle,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            imeAction = imeAction
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onImeAction?.invoke() },
                            onGo = { onImeAction?.invoke() },
                            onNext = { onImeAction?.invoke() },
                            onSearch = { onImeAction?.invoke() },
                            onSend = { onImeAction?.invoke() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // placeholder
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = inputTextStyle.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // X 버튼
                if (enabled && value.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(clearBg)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onValueChange("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear preset name",
                            tint = clearIconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 하단 텍스트
        when {
            !supportingText.isNullOrBlank() -> {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(top = 6.dp, start = 6.dp)
                )
            }

            maxLength != null -> {
                Text(
                    text = "${value.length} / $maxLength",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp, start = 6.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PresetNameTextFieldPreview_Empty() {
    var text by remember { mutableStateOf("") }

    PresetNameTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.padding(16.dp),
        placeholder = "프리셋 이름을 입력하세요",
        maxLength = 10
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PresetNameTextFieldPreview_Filled() {
    var text by remember { mutableStateOf("컨피") }

    PresetNameTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.padding(16.dp),
        maxLength = 10
    )
}
