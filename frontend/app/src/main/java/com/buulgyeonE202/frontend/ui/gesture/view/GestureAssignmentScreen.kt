package com.buulgyeonE202.frontend.ui.gesture.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.buulgyeonE202.frontend.ui.component.MainBottomBar // ✅ 탭바 import
import com.buulgyeonE202.frontend.ui.component.PrimaryButton
import com.buulgyeonE202.frontend.ui.component.ScrollableScreenTemplate
import com.buulgyeonE202.frontend.ui.gesture.component.GestureSelectRow
import com.buulgyeonE202.frontend.ui.gesture.viewmodel.GestureViewModel
import com.buulgyeonE202.frontend.ui.theme.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun GestureAssignmentScreen(
    navController: NavController, // ✅ 탭바 네비게이션용 추가
    mappingId: Int,
    actionId: Int,
    actionTitle: String,
    actionDescription: String,
    currentGestureId: Int,
    viewModel: GestureViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSaveComplete: () -> Unit
) {
    val context = LocalContext.current

    val decodedTitle = remember(actionTitle) {
        URLDecoder.decode(actionTitle, StandardCharsets.UTF_8.toString())
    }
    val decodedDescription = remember(actionDescription) {
        URLDecoder.decode(actionDescription, StandardCharsets.UTF_8.toString())
    }

    val gestureOptions by viewModel.gestureList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedGestureId by remember { mutableIntStateOf(currentGestureId) }

    LaunchedEffect(Unit, mappingId) {
        viewModel.fetchGestures(mappingId)
    }

    LaunchedEffect(gestureOptions) {
        if (gestureOptions.isNotEmpty() && selectedGestureId == -1) {
            selectedGestureId = gestureOptions[0].id
        }
    }

    val scrollState = rememberScrollState()

    ScrollableScreenTemplate(
        title = decodedTitle,
        onBackClick = onBackClick,
        // ✅ 1. 스캐폴드의 바텀바 자리에는 탭바(MainBottomBar)를 넣습니다.
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
    ) { paddingValues ->

        // ✅ 2. 내부 컨텐츠 영역을 분할 (위: 스크롤 내용 / 아래: 저장 버튼)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 상단바, 탭바 사이 영역
                .background(Color(0xFFF5F5F5))
        ) {
            // [A] 스크롤 되는 상세 내용 (남은 공간 꽉 채움)
            Column(
                modifier = Modifier
                    .weight(1f) // 버튼 위 공간을 모두 차지
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                Surface(
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = White
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("설명", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(decodedDescription, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(24.dp))

                        Text("제스처 설정", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Primary500)
                            }
                        } else if (gestureOptions.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                Text("사용 가능한 제스처가 없습니다.", color = Color.Gray)
                            }
                        } else {
                            gestureOptions.forEach { option ->
                                GestureSelectRow(
                                    option = option,
                                    isSelected = (selectedGestureId == option.id),
                                    onClick = { selectedGestureId = option.id }
                                )
                            }
                        }
                    }
                }
            }

            // [B] 저장 버튼 (탭바 바로 위에 고정)
            Surface(
                color = LightGray,
                shadowElevation = 8.dp // 살짝 그림자를 주어 리스트와 구분
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    PrimaryButton(
                        text = "저장",
                        enabled = selectedGestureId != -1,
                        onClick = {
                            if (selectedGestureId != -1) {
                                viewModel.registerMapping(
                                    mappingId = mappingId,
                                    actionId = actionId,
                                    gestureId = selectedGestureId
                                ) {
                                    Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                                    onSaveComplete()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}