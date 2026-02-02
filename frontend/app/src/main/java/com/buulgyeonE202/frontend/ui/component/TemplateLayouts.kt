package com.buulgyeonE202.frontend.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableScreenTemplate(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current

    val expandedHeight = 300.dp
    val collapsedHeight = 60.dp

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val maxOffset = with(density) { (expandedHeight - collapsedHeight).toPx() }

    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != -maxOffset) {
            scrollBehavior.state.heightOffsetLimit = -maxOffset
        }
    }

    val offset = scrollBehavior.state.heightOffset
    val fraction = if (maxOffset != 0f) ((-offset) / maxOffset).coerceIn(0f, 1f) else 0f

    val titleSize = androidx.compose.ui.unit.lerp(48.sp, 24.sp, fraction)
    val titleBiasX = androidx.compose.ui.util.lerp(0f, -1f, fraction)
    val titleBiasY = androidx.compose.ui.util.lerp(0f, 1f, fraction)

    val titleXOffset = if (onBackClick != null) {
        androidx.compose.ui.unit.lerp(0.dp, 48.dp, fraction)
    } else {
        0.dp
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize() // 전체 화면을 채워야 하단 바가 제자리에 보입니다.
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color(0xFFF5F5F5),
        // contentWindowInsets 설정을 제거하여 기본값(시스템 바 포함) 사용
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .statusBarsPadding()
                    .height(expandedHeight + with(density) { offset.toDp() })
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            ) {
                // 뒤로가기 버튼
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                }

                // 타이틀
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = titleXOffset),
                    contentAlignment = BiasAlignment(titleBiasX, titleBiasY)
                ) {
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = title,
                            fontSize = titleSize,
                            lineHeight = titleSize,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Visible,
                            color = Color.Black,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    }
                }

                // 액션 버튼
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions()
                }
            }
        },
        bottomBar = {
            // 하단 바가 시스템 네비게이션(소프트키)에 가려지지 않도록 패딩 처리
            Box(modifier = Modifier.navigationBarsPadding()) {
                bottomBar()
            }
        }
    ) { paddingValues ->
        // content 영역
        content(paddingValues)
    }
}