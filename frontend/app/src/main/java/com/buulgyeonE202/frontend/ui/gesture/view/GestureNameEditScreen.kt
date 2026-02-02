package com.buulgyeonE202.frontend.ui.gesture.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.buulgyeonE202.frontend.ui.component.MainBottomBar
import com.buulgyeonE202.frontend.ui.component.PrimaryButton
import com.buulgyeonE202.frontend.ui.gesture.viewmodel.GestureHomeViewModel
import com.buulgyeonE202.frontend.ui.theme.LightGray

@Composable
fun GestureNameEditScreen(
    navController: NavController,
    mappingId: Int,
    currentName: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: GestureHomeViewModel = hiltViewModel()
) {
    val isCreateMode = mappingId == 0

    var newName by remember { mutableStateOf(currentName) }
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    val isButtonEnabled = if (isCreateMode) {
        newName.isNotBlank() && !isLoading
    } else {
        newName.isNotBlank() && newName != currentName && !isLoading
    }

    // 키보드 올라왔을 때 스크롤 가능하도록 상태 추가
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFFF2F2F2),
        bottomBar = {
            MainBottomBar(
                currentRoute = "gesture_home",
                onNavigate = { route ->
                    if (route != "gesture_home") {
                        navController.navigate(route) {
                            popUpTo("gesture_home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.popBackStack("gesture_home", inclusive = false)
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF2F2F2))
        ) {
            // [A] 상단 컨텐츠 영역 (화면의 남은 공간을 모두 차지)
            Column(
                modifier = Modifier
                    .weight(1f) // 버튼 위 공간 꽉 채움
                    .verticalScroll(scrollState) // 키보드 대응용 스크롤
            ) {
                // 1. 헤더 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp, bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCreateMode) "이름 설정" else "이름 수정",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                // 2. 입력 영역
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onBackClick() },
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        placeholder = {
                            Text(
                                "프리셋 이름을 입력하세요",
                                color = Color(0xFFBCBCBC),
                                fontSize = 18.sp
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF635BFF)
                        ),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                }
            }

            // [B] 하단 버튼 영역 (탭바 바로 위에 고정)
            Surface(
                color = LightGray, // 버튼 영역 배경 흰색 (깔끔하게)
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    PrimaryButton(
                        text = if (isLoading) "처리 중..." else if (isCreateMode) "생성 완료" else "수정 완료",
                        enabled = isButtonEnabled,
                        onClick = {
                            if (isCreateMode) {
                                viewModel.createNewPreset(newName) {
                                    Toast.makeText(context, "새 프리셋이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                }
                            } else {
                                viewModel.updatePresetName(mappingId, newName) {
                                    Toast.makeText(context, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}