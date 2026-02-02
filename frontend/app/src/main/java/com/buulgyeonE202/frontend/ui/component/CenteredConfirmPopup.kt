package com.buulgyeonE202.frontend.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.buulgyeonE202.frontend.ui.theme.*

@Composable
fun CenteredConfirmPopup(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "확인",
    cancelText: String = "취소",
    onConfirm: () -> Unit,
    onCancel: () -> Unit = onDismissRequest
) {
    if (expanded) {
        // Dialog는 기본적으로 딤(배경 어둡게) 처리와 바깥 클릭 닫기를 지원
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(0.9f) // 화면 너비의 90%
                    .wrapContentHeight()
            ) {
                Column {
                    // 헤더 (Primary500 진한 파랑)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Primary500)
                            .padding(vertical = 16.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = title,
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 메시지 영역
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message,
                            color = Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }

                    // 3️버튼 영역 (하단 가로 배치)
                    Row(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                        // 취소 버튼
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Primary100)
                                .clickable { onCancel() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = cancelText, color = White, fontWeight = FontWeight.Bold)
                        }
                        // 확인 버튼
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Primary500)
                                .clickable { onConfirm() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = confirmText, color = White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}