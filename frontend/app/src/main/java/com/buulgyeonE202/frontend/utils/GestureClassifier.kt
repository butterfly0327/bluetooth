package com.buulgyeonE202.frontend.utils

import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import android.graphics.PointF

import kotlin.math.*

object GestureClassifier {
    /**
     * 1. 젓가락 판정용: 두 포인트 사이의 유클리드 거리 계산
     */
    fun getDistance(p1: NormalizedLandmark, p2: NormalizedLandmark): Float {
        return sqrt((p1.x() - p2.x()).toDouble().pow(2.0) + (p1.y() - p2.y()).toDouble().pow(2.0)).toFloat()
    }

    /**
     * 2. 다이얼 판정용: 두 프레임 사이의 각도 차이 계산
     */
    fun calculateAngleDiff(startWrist: PointF, startTip: PointF, endWrist: PointF, endTip: PointF): Double {
        fun getAngle(wrist: PointF, tip: PointF): Double {
            // math.atan2(p2[1] - p1[1], p2[0] - p1[0]) 대응
            return atan2((tip.y - wrist.y).toDouble(), (tip.x - wrist.x).toDouble())
        }

        val angle1 = getAngle(startWrist, startTip)
        val angle2 = getAngle(endWrist, endTip)

        // abs(math.degrees(angle2 - angle1)) 대응
        return abs(Math.toDegrees(angle2 - angle1))
    }
}