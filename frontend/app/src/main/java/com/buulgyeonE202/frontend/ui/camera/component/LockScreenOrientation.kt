import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context.findActivity()

        if (activity != null) {
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = orientation

            onDispose {
                // 화면이 사라질 때 원래대로 복구
                activity.requestedOrientation = originalOrientation
            }
        } else {
            // Activity를 못 찾았을 경우에도 onDispose는 반드시 있어야 함
            onDispose { }
        }
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}