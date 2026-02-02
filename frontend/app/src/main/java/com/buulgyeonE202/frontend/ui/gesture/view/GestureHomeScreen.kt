package com.buulgyeonE202.frontend.ui.gesture.view

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import com.buulgyeonE202.frontend.data.model.response.preset.PresetItem
import com.buulgyeonE202.frontend.ui.component.CustomPopupContainer
import com.buulgyeonE202.frontend.ui.component.CustomPopupMenuItem
import com.buulgyeonE202.frontend.ui.gesture.model.GestureItem

import com.buulgyeonE202.frontend.ui.gesture.viewmodel.GestureHomeViewModel
import com.buulgyeonE202.frontend.ui.component.ScrollableScreenTemplate
import com.buulgyeonE202.frontend.ui.component.MainBottomBar
import com.buulgyeonE202.frontend.ui.component.EditBottomBar
import com.buulgyeonE202.frontend.ui.gesture.component.GestureCard
import com.buulgyeonE202.frontend.ui.gesture.component.GestureRenameOverlay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GestureHomeScreen(
    navController: NavController,
    viewModel: GestureHomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val serverPresets by viewModel.presetList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 화면 진입 시 데이터 로드
    LaunchedEffect(Unit) { viewModel.fetchPresets() }

    var uiPresets by remember { mutableStateOf<List<PresetItem>>(emptyList()) }

    // 데이터가 들어오면 '대표 매핑셋'을 맨 앞으로 정렬
    LaunchedEffect(serverPresets) {
        uiPresets = serverPresets.sortedByDescending { it.isRepresentative }
    }

    // 상태 관리
    var isEditMode by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var selectedPreset by remember { mutableStateOf<PresetItem?>(null) }

    // 이름 변경 팝업 상태
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameInput by remember { mutableStateOf("") }

    // 팝업 시 키보드 자동 노출
    LaunchedEffect(showRenameDialog) {
        if (showRenameDialog) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // 뒤로가기 핸들링
    BackHandler(enabled = isEditMode || showRenameDialog) {
        when {
            showRenameDialog -> {
                showRenameDialog = false
                renameInput = ""
                keyboardController?.hide()
            }
            isEditMode -> {
                isEditMode = false
                selectedPreset = null
            }
        }
    }

    // 생명주기 (화면 복귀 시 갱신)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.fetchPresets()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScrollableScreenTemplate(
            title = "Gesture",
            onBackClick = if (isEditMode) {
                {
                    isEditMode = false
                    selectedPreset = null
                }
            } else {
                null
            },
            actions = {
                if (!isEditMode && !showRenameDialog) {
                    IconButton(onClick = { navController.navigate("gesture_name_input") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                    }
                    Box {
                        IconButton(onClick = { isMenuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Black)
                        }
                        CustomPopupContainer(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false },
                            offset = DpOffset(x = 0.dp, y = 40.dp)
                        ) {
                            CustomPopupMenuItem(
                                text = "편집",
                                icon = Icons.Default.Edit,
                                onClick = {
                                    isEditMode = true
                                    isMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (isEditMode && !showRenameDialog) {
                    EditBottomBar(
                        onSelectClick = {
                            selectedPreset?.let { preset ->
                                // 1. 대표 설정 API 호출
                                viewModel.setRepresentative(preset.presetId)

                                // 2. 토스트 안내
                                Toast.makeText(context, "'${preset.title}'이(가) 대표 매핑셋으로 설정되었습니다.", Toast.LENGTH_SHORT).show()

                                // 3. 편집 모드 종료
                                isEditMode = false
                                selectedPreset = null

                                // 4. 목록 즉시 갱신 (정렬 반영을 위해)
                                viewModel.fetchPresets()
                            } ?: run {
                                Toast.makeText(context, "프리셋을 선택해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onRenameClick = {
                            selectedPreset?.let { preset ->
                                renameInput = preset.title
                                showRenameDialog = true
                            } ?: run {
                                Toast.makeText(context, "수정할 프리셋을 선택해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDeleteClick = {
                            selectedPreset?.let { preset ->
                                viewModel.deletePreset(preset.presetId)
                                isEditMode = false
                                selectedPreset = null
                            } ?: run {
                                Toast.makeText(context, "삭제할 프리셋을 선택해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                } else if (!isEditMode && !showRenameDialog) {
                    MainBottomBar(currentRoute = "gesture_home", onNavigate = { route ->
                        if (route != "gesture_home") {
                            navController.navigate(route) {
                                popUpTo("gesture_home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    })
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                if (isLoading && uiPresets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = com.buulgyeonE202.frontend.ui.theme.Primary500)
                    }
                } else if (uiPresets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "등록된 제스처가 없습니다.\n+ 버튼을 눌러 추가해보세요!",
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                    ) {
                        items(uiPresets, key = { it.presetId }) { item ->
                            val isSelected = (selectedPreset?.presetId == item.presetId)

                            Box(modifier = Modifier.animateItemPlacement()) {
                                val gestureItem = GestureItem(
                                    id = item.presetId,
                                    title = item.title,
                                    count = item.gestureCount,
                                    isFavorite = item.isRepresentative,
                                    isSelected = isSelected
                                )

                                GestureCard(
                                    item = gestureItem,
                                    isEditMode = isEditMode,
                                    onClick = {
                                        if (isEditMode) {
                                            selectedPreset = if (isSelected) null else item
                                        } else {
                                            val encodedTitle = java.net.URLEncoder.encode(item.title, "UTF-8")
                                            navController.navigate("gesture_detail/${item.presetId}/$encodedTitle")
                                        }
                                    },
                                    onLongClick = if (isEditMode) null else {
                                        {
                                            isEditMode = true
                                            selectedPreset = item
                                        }
                                    },
                                    onFavoriteClick = {
                                        viewModel.setRepresentative(item.presetId)
                                        Toast.makeText(context, "'${item.title}'이(가) 대표 매핑셋으로 설정되었습니다.", Toast.LENGTH_SHORT).show()
                                        // 즐겨찾기 클릭 즉시 목록 갱신 (정렬을 위해)
                                        viewModel.fetchPresets()
                                    },
                                    dragModifier = Modifier
                                )
                            }
                        }
                    }
                }
            }
        }

        GestureRenameOverlay(
            isVisible = showRenameDialog,
            currentName = renameInput,
            onNameChange = { renameInput = it },
            onDismiss = {
                showRenameDialog = false
                keyboardController?.hide()
            },
            onConfirm = {
                if (renameInput.isNotBlank()) {
                    selectedPreset?.let { preset ->
                        viewModel.updatePresetName(preset.presetId, renameInput) {
                            Toast.makeText(context, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            showRenameDialog = false
                            isEditMode = false
                            selectedPreset = null
                            keyboardController?.hide()
                        }
                    }
                }
            },
            focusRequester = focusRequester
        )
    }
}