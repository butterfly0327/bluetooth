package com.buulgyeonE202.frontend.ui.gesture.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun GestureRenameOverlay(
    isVisible: Boolean,
    currentName: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    focusRequester: FocusRequester
) {
    if (!isVisible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .zIndex(10f)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}

                // 1. 키보드가 올라오면 그만큼 밀어 올림 (adjustNothing 덕분에 중복 안 됨)
                .imePadding()
                // 2. 키보드가 없을 땐 네비게이션 바만큼 띄움 (없으면 바닥에 붙어버림)
                .navigationBarsPadding()

                .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 24.dp)
        ) {
            Text(
                text = "이름 변경",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PresetNameTextField(
                value = currentName,
                onValueChange = onNameChange,
                placeholder = "새 이름을 입력하세요",
                imeAction = ImeAction.Done,
                onImeAction = { onConfirm() },
                modifier = Modifier.focusRequester(focusRequester)
            )
        }
    }
}