// CameraPage.kt 맨 아래나 별도 파일에 추가

import android.view.OrientationEventListener
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberDeviceRotation(): Float {
    val context = LocalContext.current
    var rotation by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val listener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return

                // 폰의 물리적 각도에 따라 아이콘을 반대 방향으로 돌려야 정면으로 보임
                rotation = when (orientation) {
                    in 45 until 135 -> 270f  // 오른쪽으로 눕힘 (Reverse Landscape)
                    in 135 until 225 -> 180f // 거꾸로 듦 (Reverse Portrait)
                    in 225 until 315 -> 90f  // 왼쪽으로 눕힘 (Landscape)
                    else -> 0f               // 정방향 (Portrait)
                }
            }
        }
        listener.enable()
        onDispose { listener.disable() }
    }

    // 부드럽게 회전하도록 애니메이션 처리
    val animatedRotation by animateFloatAsState(targetValue = rotation, label = "rotation")
    return animatedRotation
}