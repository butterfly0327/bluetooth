package com.buulgyeonE202.frontend.ui.ai

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class PoseLandmarkerHelper(
    val context: Context,
    private val resultListener: ((PoseLandmarkerResult, Int, Int) -> Unit)? = null
) {
    private var poseLandmarker: PoseLandmarker? = null

    init {
        setupPoseLandmarker()
    }

    private fun setupPoseLandmarker() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("pose_landmarker_lite.task")
            .setDelegate(Delegate.GPU)
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumPoses(1)
            .setMinPoseDetectionConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setResultListener { result, _ ->
                resultListener?.invoke(result, 0, 0)
            }
            .build()

        try {
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            Log.e("PoseHelper", "초기화 실패: ${e.message}")
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
            poseLandmarker?.detectAsync(mpImage, safeTime)
        } catch (e: Exception) {
            Log.e("PoseHelper", "인식 오류: ${e.message}")
        }
    }

    fun clear() {
        poseLandmarker?.close()
        poseLandmarker = null
    }
}