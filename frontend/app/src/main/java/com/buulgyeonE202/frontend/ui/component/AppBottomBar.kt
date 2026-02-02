package com.buulgyeonE202.frontend.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt // 🔥 카메라 아이콘 추가
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import com.buulgyeonE202.frontend.ui.theme.*

// ==========================================
// 1. 메인 하단바 (화면 이동용)
// ==========================================
@Composable
fun MainBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    uiRotation: Float = 0f
) {
    NavigationBar(
        containerColor = Color(0xFFF5F5F5)
    ) {
        // 1-1. Gesture 탭
        NavigationBarItem(
            selected = currentRoute == "gesture_home",
            onClick = { onNavigate("gesture_home") },
            icon = { Icon(Icons.Default.WavingHand, contentDescription = "Gesture") },
            label = { Text("Gesture") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = Black,
                selectedTextColor = Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        // 1-2. Camera 탭 (Videos -> Camera 변경)
        NavigationBarItem(
            selected = currentRoute == "camera", // 라우트 이름 변경
            onClick = { onNavigate("camera") },  // 클릭 시 "camera" 경로로 이동 요청
            icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Camera") }, // 아이콘 변경
            label = { Text("Camera") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = Black,
                selectedTextColor = Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        // 1-3. Setting 탭
        NavigationBarItem(
            selected = currentRoute == "setting",
            onClick = { onNavigate("setting") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") },
            label = { Text("Setting") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = Black,
                selectedTextColor = Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

@Composable
fun EditBottomBar(
    onSelectClick: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFFF5F5F5)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onSelectClick,
            icon = { Icon(Icons.Default.Star, contentDescription = "Save") },
            label = { Text("Favorite") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Primary500,
                unselectedTextColor = Primary500
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onRenameClick,
            icon = { Icon(Icons.Default.Edit, contentDescription = "Rename") },
            label = { Text("Rename") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Black,
                unselectedTextColor = Black
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onDeleteClick,
            icon = { Icon(Icons.Default.Delete, contentDescription = "Delete") },
            label = { Text("Delete") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TriadicRed,
                unselectedTextColor = TriadicRed
            )
        )
    }
}