package com.buulgyeonE202.frontend.ui.ai

import com.buulgyeonE202.frontend.data.manager.HumanTrackingManager
import android.content.Context
import android.graphics.* // Canvas, Color, Paint 통합
import android.util.AttributeSet
import android.view.View // 부모인 View로부터 자신의 크기 정보인 width, height를 물려받음
import android.util.Log


/**
 * 휴먼 트래킹 시각화 오버레이
 * - 눈(파랑), 어깨(녹색), 상체(빨강) 중심점 표시
 * - 1/3, 2/3 가이드라인 표시
 * - 방향 및 속도 텍스트 표시
 */
class TrackingOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var trackingState: HumanTrackingManager.TrackingState? = null // 각 인체 중심점 좌표 정보
    private val eyePaint = Paint()
    private val shoulderPaint = Paint()
    private val guidelinePaint = Paint() // 지금은 필요 X
    private val directionPaint = Paint() // 지금은 필요 X

    private var isFrontCamera: Boolean = true
    private var phoneRotation: Int = 0

    init {
        initPaints()
    }

    private fun initPaints() {
        eyePaint.apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }

        shoulderPaint.apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        guidelinePaint.apply {
            color = Color.LTGRAY
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }

        directionPaint.apply {
            color = Color.GREEN
            textSize = 56f
            isAntiAlias = true // 글자를 부드럽게
            isFakeBoldText = true // 글자를 굵게 강조
        }
    }

    fun setResults(
        state: HumanTrackingManager.TrackingState,
        frontCamera: Boolean = true,
        rotation: Int = 0,
    ) {
        trackingState = state
        isFrontCamera = frontCamera
        phoneRotation = rotation
        postInvalidate()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 가이드라인 그리기 (1/3, 2/3 지점)
//        canvas.drawLine(width / 3f, 0f, width / 3f, height.toFloat(), guidelinePaint)
//        canvas.drawLine(width * 2 / 3f, 0f, width * 2 / 3f, height.toFloat(), guidelinePaint)

        val state = trackingState ?: return
        val viewW = width.toFloat()
        val viewH = height.toFloat()

        // 기기 회전에 따른 이미지-뷰 간 종횡비(Aspect Ratio) 보정
        val isRotated = phoneRotation == 90 || phoneRotation == 270
        val actualImgW = if (isRotated) state.imageHeight else state.imageWidth
        val actualImgH = if (isRotated) state.imageWidth else state.imageHeight

        // PreviewView의 FILL_CENTER(중앙 꽉 채움) 방식에 따른 배율과 오프셋 계산
        val scale: Float
        val offsetX: Float
        val offsetY: Float

        if (actualImgW.toFloat() / actualImgH > viewW / viewH) {
            scale = viewH / actualImgH
            offsetX = (viewW - actualImgW * scale) / 2f
            offsetY = 0f
        } else {
            scale = viewW / actualImgW
            offsetX = 0f
            offsetY = (viewH - actualImgH * scale) / 2f
        }

        // 눈 중심점 (Blue) - HumanTrackingManager의 TrackingState.eyePos 사용
        state.eyePos?.let { (nx, ny) ->
            val (vx, vy) = getTransformedPoint(
                nx,
                ny,
                scale,
                offsetX,
                offsetY,
                actualImgW,
                actualImgH
            )
            canvas.drawCircle(vx, vy, 16f, eyePaint)
        }

        // 어깨 중심점 (Red) - HumanTrackingManager의 TrackingState.shoulderPos 사용
        state.shoulderPos?.let { (nx, ny) ->
            val (vx, vy) = getTransformedPoint(
                nx,
                ny,
                scale,
                offsetX,
                offsetY,
                actualImgW,
                actualImgH
            )
            canvas.drawCircle(vx, vy, 16f, shoulderPaint)
        }

//        // 방향 텍스트
//        val dirText = "DIR: ${state.direction}"
//        val textWidth = directionPaint.measureText(dirText)
//        canvas.drawText(dirText, (viewW - textWidth) / 2, 100f, directionPaint)

    }

    private fun getTransformedPoint(
        nx: Float,
        ny: Float,
        scale: Float,
        oX: Float,
        oY: Float,
        aW: Int,
        aH: Int
    ): Pair<Float, Float> {
        var finalX = nx
        var finalY = ny
        when (phoneRotation) {
            90 -> {
                finalX = 1f - ny; finalY = nx
            }

            180 -> {
                finalX = 1f - nx; finalY = 1f - ny
            }

            270 -> {
                finalX = ny; finalY = 1f - nx
            }
        }
        if (isFrontCamera) finalX = 1f - finalX
        return Pair(finalX * aW * scale + oX, finalY * aH * scale + oY)
    }
}