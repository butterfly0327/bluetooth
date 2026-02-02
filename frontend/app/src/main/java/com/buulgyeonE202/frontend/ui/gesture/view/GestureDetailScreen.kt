package com.buulgyeonE202.frontend.ui.gesture.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.navigation.NavController
import com.buulgyeonE202.frontend.ui.component.MainBottomBar
import com.buulgyeonE202.frontend.ui.component.ScrollableScreenTemplate
import com.buulgyeonE202.frontend.ui.gesture.component.GestureActionItemCard
import com.buulgyeonE202.frontend.ui.gesture.viewmodel.GestureViewModel
import com.buulgyeonE202.frontend.ui.theme.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GestureDetailScreen(
    navController: NavController,
    mappingId: Int,
    initialTitle: String, // MainActivity에서 받아온 이름
    onAddClick: () -> Unit,
    viewModel: GestureViewModel = hiltViewModel()
) {
    // 1. 받아온 이름 디코딩 (즉시 사용 가능)
    val decodedTitle = remember(initialTitle) {
        java.net.URLDecoder.decode(initialTitle, "UTF-8")
    }

    // 상태 구독
    val detailItems by viewModel.mappingDetailItems.collectAsState()
    val presetTitle by viewModel.presetTitle.collectAsState() // 서버에서 불러온 이름 (로딩 필요)
    val isLoading by viewModel.isLoading.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()

    BackHandler(enabled = isEditMode) {
        viewModel.setEditMode(false)
    }

    LaunchedEffect(mappingId) {
        viewModel.fetchMappingDetail(mappingId)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && !isLoading) {
                viewModel.fetchMappingDetail(mappingId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val groupedItems = remember(detailItems) {
        detailItems.groupBy { it.category }
    }

    ScrollableScreenTemplate(
        // 서버 데이터가 아직 없으면(로딩중이면) 넘겨받은 decodedTitle을 먼저 보여줌
        title = presetTitle.ifEmpty { decodedTitle },
        onBackClick = {
            if (isEditMode) {
                viewModel.setEditMode(false)
            } else {
                navController.popBackStack()
            }
        },
        actions = {
            if (!isEditMode) {
                IconButton(onClick = onAddClick) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                }
            }
        },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (isEditMode) viewModel.setEditMode(false)
                }
        ) {
            if (isLoading && detailItems.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary500
                )
            } else if (detailItems.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("등록된 기능이 없습니다.", color = Color.Gray)
                    Text("+ 버튼을 눌러 기능을 추가해보세요!", color = Color.LightGray, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    groupedItems.forEach { (category, items) ->
                        item(key = category) {
                            Text(
                                text = category,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp, start = 4.dp)
                            )
                        }

                        items(items, key = { it.id }) { item ->
                            GestureActionItemCard(
                                action = item,
                                isEditMode = isEditMode,
                                onClick = {
                                    if (isEditMode) {
                                        // 편집 모드 클릭 동작 없음
                                    } else {
                                        val encodedTitle = URLEncoder.encode(item.actionName, StandardCharsets.UTF_8.toString())
                                        val encodedDesc = URLEncoder.encode(item.description, StandardCharsets.UTF_8.toString())
                                        navController.navigate("gesture_assignment/$mappingId/${item.actionId}/$encodedTitle/$encodedDesc/${item.gestureId}")
                                    }
                                },
                                onLongClick = {
                                    viewModel.setEditMode(true)
                                },
                                onDeleteClick = {
                                    viewModel.deleteItem(mappingId, item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}