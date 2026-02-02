package com.buulgyeonE202.frontend.ui.gesture.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.buulgyeonE202.frontend.ui.component.MainBottomBar // ✅ 탭바 import
import com.buulgyeonE202.frontend.ui.component.ScrollableScreenTemplate
import com.buulgyeonE202.frontend.ui.gesture.model.GestureActionItem
import com.buulgyeonE202.frontend.ui.gesture.component.GestureActionSelectionCard
import com.buulgyeonE202.frontend.ui.gesture.viewmodel.GestureViewModel
import com.buulgyeonE202.frontend.ui.theme.Primary500

@Composable
fun GestureActionSelectionScreen(
    navController: NavController, // ✅ 탭바 네비게이션용 추가
    mappingId: Int,
    onBackClick: () -> Unit,
    onActionSelected: (GestureActionItem) -> Unit,
    viewModel: GestureViewModel = hiltViewModel()
) {
    val actionList by viewModel.actionList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit, mappingId) {
        viewModel.fetchActions(mappingId)
    }

    val groupedActions = remember(actionList) {
        actionList.groupBy { it.category }
    }

    ScrollableScreenTemplate(
        title = "기능 선택",
        onBackClick = onBackClick,
        // ✅ 하단 탭바 추가
        bottomBar = {
            MainBottomBar(
                currentRoute = "gesture_home", // 제스처 탭 활성화 유지
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(20.dp)
        ) {
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary500)
                    }
                }
            } else if (actionList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("추가할 수 있는 기능이 없습니다.", color = Color.Gray)
                    }
                }
            } else {
                groupedActions.forEach { (category, actions) ->
                    item {
                        Text(
                            text = category,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 4.dp)
                        )
                    }

                    itemsIndexed(actions) { index, action ->
                        val isFirst = index == 0
                        val isLast = index == actions.size - 1
                        val shape = when {
                            isFirst && isLast -> RoundedCornerShape(24.dp)
                            isFirst -> RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            isLast -> RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                            else -> RoundedCornerShape(0.dp)
                        }

                        GestureActionSelectionCard(
                            action = action,
                            shape = shape,
                            showDivider = !isLast,
                            onClick = { onActionSelected(action) }
                        )
                    }
                }
            }
        }
    }
}