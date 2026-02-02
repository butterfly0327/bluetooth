package com.buulgyeonE202.frontend.ui.ai

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class GestureRecognizerHelper(
    val context: Context,
    private val resultListener: ((GestureRecognizerResult, Int, Int) -> Unit)? = null
) {
    private var gestureRecognizer: GestureRecognizer? = null

    init {
        setupGestureRecognizer()
    }

    private fun setupGestureRecognizer() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("gesture_recognizer.task")
            .setDelegate(Delegate.GPU)
            .build()

        val options = GestureRecognizer.GestureRecognizerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumHands(2)
            .setResultListener { result, _ ->
                // 이미지 크기는 CameraPage에서 관리하므로 0으로 넘김 (OverlayView가 최신 크기 사용)
                resultListener?.invoke(result, 0, 0)
            }
            .build()

        try {
            gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
        } catch (e: Exception) {
            Log.e("GestureHelper", "초기화 실패: ${e.message}")
        }
    }

    // [이 함수가 없어서 에러가 났던 것입니다. 추가해주세요!]
    // 비동기 인식 돌리는 코드
    private var lastTimestamp = 0L

    fun recognizeBitmap(bitmap: Bitmap) {
        try {
            val frameTime = SystemClock.uptimeMillis()
            // 단조 증가 보장: 이전 timestamp와 같거나 작으면 +1
            val safeTime = if (frameTime <= lastTimestamp) lastTimestamp + 1 else frameTime
            lastTimestamp = safeTime
            val mpImage = BitmapImageBuilder(bitmap).build()
            gestureRecognizer?.recognizeAsync(mpImage, safeTime)
        } catch (e: Exception) {
            Log.e("GestureHelper", "인식 오류: ${e.message}")
        }
    }

    fun clear() {
        gestureRecognizer?.close()
        gestureRecognizer = null
    }
}