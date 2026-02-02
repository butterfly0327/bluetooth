package com.buulgyeonE202.frontend.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 다크 모드 색상표
private val DarkColorScheme = darkColorScheme(
    primary = Primary400, // 다크모드에선 눈이 아프지 않게 살짝 연한 톤 사용
    onPrimary = Primary900,
    primaryContainer = Primary700,
    onPrimaryContainer = Primary100,

    secondary = Complementary400, // 노란색으로 강조
    onSecondary = Primary900,

    background = DarkGray,
    surface = DarkGray,
    onBackground = White,
    onSurface = White
)

// 라이트 모드 색상표 (기본)
private val LightColorScheme = lightColorScheme(
    primary = Primary500, // #5F5FE6
    onPrimary = White,    // Primary 위에 글씨는 흰색

    primaryContainer = Primary100,
    onPrimaryContainer = Primary900,

    secondary = AnalogousPurple,
    onSecondary = White,

    tertiary = TriadicPink,

    background = LightGray, // #F5F5F5
    surface = White,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun FrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Android 12+ 동적 컬러 사용 여부
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 상태바(맨 위 시계 뜨는 곳) 색상 설정
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 상태바를 배경색과 동일하게 투명 처리하거나 Primary 색으로 지정 가능
            window.statusBarColor = colorScheme.background.toArgb()
            // 상태바 아이콘 색상
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Typography.kt
        content = content
    )
}