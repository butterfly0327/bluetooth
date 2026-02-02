package com.buulgyeonE202.frontend.ui.ai

import android.content.Context
import android.graphics.* // Canvas, Color, Paint 통합
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var results: GestureRecognizerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    // 전면 카메라 여부 (좌표 변환용)
    private var isFrontCamera: Boolean = true

    // 카메라 이미지 크기 (FILL_CENTER 보정용) — sensorRotation 적용 후, 폰 회전 적용 전
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0

    // 폰 기울기 회전 각도 (0, 90, 180, 270)
    private var phoneRotation: Int = 0

    // 회전 후 제스처 비트맵 크기 - 회전 후 비트맵을 또 생성하지 않으므로 제거 [백수연]
    // 아래 관련 변수도 동일하게 주석 처리
//    private var rotatedImgW: Int = 0
//    private var rotatedImgH: Int = 0

    init {
        initPaints()
    }

    // 260201 [백수연] 보기 깔끔하게 정리
    private fun initPaints() {
        // 선 설정 (손가락 마디 연결)
        linePaint.apply{
            color = Color.GREEN
            strokeWidth = 8f // 굵기
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND // 선 관절 마감
        }

        // 점 설정 (손가락 관절)
        pointPaint.apply {
            color = Color.YELLOW
//            strokeWidth = 15f
            style = Paint.Style.FILL
        }
    }

    /**
     * 분석 결과와 카메라 방향을 받아 화면을 다시 그립니다.
     */
    fun setResults(
        gestureRecognizerResult: GestureRecognizerResult,
        frontCamera: Boolean = true,
        imgW: Int = 0,
        imgH: Int = 0,
        rotation: Int = 0,
//        rotImgW: Int = 0,
//        rotImgH: Int = 0
    ) {
        results = gestureRecognizerResult
        isFrontCamera = frontCamera
        imageWidth = imgW
        imageHeight = imgH
        phoneRotation = rotation
//        rotatedImgW = rotImgW
//        rotatedImgH = rotImgH
        postInvalidate()
    }

//    /**
//     * 260201 - CameraPage 수정으로 인해 더 이상 필요 없는 함수, 주석 처리
//     * 회전된 비트맵의 정규화 좌표를 회전 전 좌표로 역변환
//     * MediaPipe는 회전된 비트맵에서 인식하므로, 오버레이에 그리려면 역회전 필요
//     */
//    private fun inverseRotateNormalized(nx: Float, ny: Float, rotation: Int): Pair<Float, Float> {
//        // CameraPage에서 postRotate(-Imgrotation) 적용:
//        //   Imgrotation=90  → postRotate(-90)=postRotate(270): (ox,oy)->(oy,1-ox)  역: (nx,ny)->(1-ny, nx)
//        //   Imgrotation=180 → postRotate(-180): (ox,oy)->(1-ox,1-oy)               역: (nx,ny)->(1-nx, 1-ny)
//        //   Imgrotation=270 → postRotate(-270)=postRotate(90): (ox,oy)->(1-oy,ox)   역: (nx,ny)->(ny, 1-nx)
//        return when (rotation) {
//            90 -> Pair(1f - ny, nx)
//            180 -> Pair(1f - nx, 1f - ny)
//            270 -> Pair(ny, 1f - nx)
//            else -> Pair(nx, ny)
//        }
//    }
//
//    /**
//     * 정규화 좌표를 PreviewView FILL_CENTER scaleType에 맞게 변환
//     */
//    private fun normalizedToView(nx: Float, ny: Float, viewW: Int, viewH: Int, imgW: Int, imgH: Int): Pair<Float, Float> {
//        if (imgW == 0 || imgH == 0) return Pair(nx * viewW, ny * viewH)
//
//        val imageAspect = imgW.toFloat() / imgH
//        val viewAspect = viewW.toFloat() / viewH
//
//        val scaledW: Float
//        val scaledH: Float
//        if (imageAspect > viewAspect) {
//            scaledH = viewH.toFloat()
//            scaledW = viewH * imageAspect
//        } else {
//            scaledW = viewW.toFloat()
//            scaledH = viewW / imageAspect
//        }
//
//        val offsetX = (scaledW - viewW) / 2f
//        val offsetY = (scaledH - viewH) / 2f
//
//        val x = nx * scaledW - offsetX
//        val y = ny * scaledH - offsetY
//        return Pair(x, y)
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val result = results ?: return // 분석 결과 없으면 안그림

        // 화면 비율 계산 (손 크기 비율 왜곡을 잡기 위해)
        val viewW = width.toFloat()
        val viewH = height.toFloat()

        // 기기 회전에 따른 이미지-뷰 간 종횡비(Aspect Ratio) 보정
        // 지금 우리는 자동 회전 기준을 쓰지 않는 것을 전제 조건으로 가짐
        // 실제 사용자는 폰을 똑바로 세운 환경에서 본다고 생각해야하므로
        // 가로로 분석된 이미지에 대해 가로, 세로를 바꿔주는 것
        val isRotated = phoneRotation == 90 || phoneRotation == 270
        val actualImgW = if (isRotated) imageHeight else imageWidth
        val actualImgH = if (isRotated) imageWidth else imageHeight

        // PreviewView의 FILL_CENTER(중앙 꽉 채움) 방식에 따른 배율과 오프셋 계산
        // PreviewView-FILL_CENTER: CameraX PreviewView의 기본값으로 CameraPreview 컴포넌트 내부에서 객체를 생성하여 카메라 화면을 보여줌
        // 해당 방식은 화면을 꽉 채워 보여주기 위해 이미지를 확대하고 자름
        // 실제 AI는 잘리기 전 원본 이미지에서 좌표를 그리므로 얼마나 확대했는지, 얼마를 잘라냈는지 알 필요가 있음
        val scale: Float
        val offsetX: Float
        val offsetY: Float

        // 이미지가 화면보다 너비가 넓을 때
        if (actualImgW.toFloat() / actualImgH > viewW / viewH) {
            scale = viewH / actualImgH
            offsetX = (viewW - actualImgW * scale) / 2f
            offsetY = 0f
        }
        // 이미지가 화면보다 높이가 높을 때
        else {
            scale = viewW / actualImgW
            offsetX = 0f
            offsetY = (viewH - actualImgH * scale) / 2f
        }

        for (landmarks in result.landmarks()) {
            // 연결선 그리기
            drawHandConnections(canvas, landmarks, scale, offsetX, offsetY, actualImgW, actualImgH)

            // 점 그리기
            for (landmark in landmarks) {
                val (vx, vy) = getTransformedPoint(
                    landmark.x(),
                    landmark.y(),
                    scale,
                    offsetX,
                    offsetY,
                    actualImgW,
                    actualImgH
                )
                canvas.drawCircle(vx, vy, 10f, pointPaint)
            }
        }
    }

    // 좌표 역회전 및 뷰 스케일 변환 통합 함수
    private fun getTransformedPoint(nx: Float, ny: Float, scale: Float, oX: Float, oY: Float, aW: Int, aH: Int): Pair<Float, Float> {
        var finalX = nx
        var finalY = ny

        // AI가 본 가로 이미지를 다시 사용자가 보는 각도로 역회전
        when (phoneRotation) {
            90 -> { finalX = 1f - ny; finalY = nx }
            180 -> { finalX = 1f - nx; finalY = 1f - ny }
            270 -> { finalX = ny; finalY = 1f - nx }
        }

        // 전면 카메라 좌우 반전
        if (isFrontCamera) finalX = 1f - finalX

        // 정규화 좌표(0~1)를 실제 뷰 좌표로 변환
        val x = finalX * aW * scale + oX
        val y = finalY * aH * scale + oY
        return Pair(x, y)
    }

    private fun drawHandConnections(canvas: Canvas, landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>, scale: Float, oX: Float, oY: Float, aW: Int, aH: Int) {
        val connections = listOf(
            0 to 1, 1 to 2, 2 to 3, 3 to 4, 0 to 5, 5 to 6, 6 to 7, 7 to 8,
            0 to 9, 9 to 10, 10 to 11, 11 to 12, 0 to 13, 13 to 14, 14 to 15, 15 to 16,
            0 to 17, 17 to 18, 18 to 19, 19 to 20, 5 to 9, 9 to 13, 13 to 17
        )
        for (conn in connections) {
            val s = landmarks[conn.first]
            val e = landmarks[conn.second]
            val (sx, sy) = getTransformedPoint(s.x(), s.y(), scale, oX, oY, aW, aH)
            val (ex, ey) = getTransformedPoint(e.x(), e.y(), scale, oX, oY, aW, aH)
            canvas.drawLine(sx, sy, ex, ey, linePaint)
        }
    }
}