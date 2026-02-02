package com.buulgyeonE202.frontend.data.manager

import android.graphics.PointF
import com.buulgyeonE202.frontend.utils.GestureClassifier
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 제스처 인식 순수 로직 담당 Manager
 *
 * 역할:
 * - 버퍼링 (5프레임 최빈값 계산)
 * - 기하학적 분석 (거리, 각도 계산)
 * - 젓가락, 다이얼 등 복합 제스처 판정
 */
@Singleton
class AiManager @Inject constructor() {

    companion object {
        private const val TAG = "AiManager"

        // 버퍼 크기
        const val FRAME_BUFFER_SIZE = 5

        // 임계값
        const val ANGLE_THRESHOLD = 30.0      // 다이얼 회전 각도 임계값
        const val DISTANCE_THRESHOLD = 0.05f  // 젓가락 거리 임계값

        // 다이얼 쿨다운 (밀리초)
        const val DIAL_COOLDOWN_MS = 300L
    }

    // ========== 버퍼 ==========
    // 제스처 레이블 버퍼
    private val gestureBuffer = ArrayDeque<String>(FRAME_BUFFER_SIZE)

    // 좌표 버퍼 - null 허용으로 타임라인 동기화
    private val coordinateBuffer = ArrayDeque<Pair<PointF, PointF>?>(FRAME_BUFFER_SIZE)

    // 다이얼 쿨다운 타이머
    private var lastDialDetectedTime: Long = 0

    /**
     * 제스처 인식 결과 처리
     * @return 최종 Action ID (버퍼가 꽉 찼을 때만 반환, 아니면 null)
     */
    fun processGestureResult(result: GestureRecognizerResult): String? {
        val landmarks = result.landmarks().firstOrNull() ?: return null
        val label = result.gestures().firstOrNull()?.firstOrNull()?.categoryName() ?: "None"

        // 버퍼 업데이트 (동기화 보장)
        updateBuffers(label, landmarks)

        // 버퍼가 꽉 찼을 때만 판정
        if (gestureBuffer.size < FRAME_BUFFER_SIZE) {
            return null
        }

        // 최빈값 계산
        val smoothedGesture = calculateMode(gestureBuffer.toList())

        // 기하학적 분석으로 최종 Action ID 결정
        return processHybridLogic(smoothedGesture, landmarks)
    }

    /**
     * 버퍼 업데이트 (동기화 보장)
     *
     * 핵심: 두 버퍼가 항상 같은 타임라인 인덱스를 유지
     */
    private fun updateBuffers(label: String, landmarks: List<NormalizedLandmark>) {
        // 버퍼 크기 초과 시 가장 오래된 데이터 제거 (동시에)
        if (gestureBuffer.size >= FRAME_BUFFER_SIZE) {
            gestureBuffer.removeFirst()
            coordinateBuffer.removeFirst()
        }

        // 제스처 버퍼: 항상 추가
        gestureBuffer.addLast(label)

        // 좌표 버퍼: 좌표가 없으면 null 추가 (타임라인 동기화)
        val wrist = landmarks.getOrNull(0)
        val indexTip = landmarks.getOrNull(8)

        if (wrist != null && indexTip != null) {
            coordinateBuffer.addLast(
                Pair(
                    PointF(wrist.x(), wrist.y()),
                    PointF(indexTip.x(), indexTip.y())
                )
            )
        } else {
            // 좌표를 못 잡아도 null로 채워서 동기화 유지
            coordinateBuffer.addLast(null)
        }
    }

    /**
     * 최빈값 계산 (Smoothing)
     */
    private fun calculateMode(buffer: List<String>): String {
        return buffer.groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: "None"
    }

    /**
     * 기하학적 분석으로 최종 Action ID 결정
     */
    private fun processHybridLogic(
        smoothedGesture: String,
        landmarks: List<NormalizedLandmark>
    ): String {
        // 기본 Action ID
        var actionId = "ID_${smoothedGesture.uppercase()}"

        // A. 젓가락 제스처 판정
        actionId = checkChopsticksGesture(smoothedGesture, landmarks, actionId)

        // B. 다이얼 제스처 판정
        val dialResult = checkDialGesture()
        if (dialResult != null) {
            return dialResult
        }

        return actionId
    }

    /**
     * 젓가락 제스처 판정
     */
    private fun checkChopsticksGesture(
        smoothedGesture: String,
        landmarks: List<NormalizedLandmark>,
        currentActionId: String
    ): String {
        if (smoothedGesture != "Victory" && smoothedGesture != "Victory_90") {
            return currentActionId
        }

        val indexTip = landmarks.getOrNull(8) ?: return currentActionId
        val middleTip = landmarks.getOrNull(12) ?: return currentActionId

        val distance = GestureClassifier.getDistance(indexTip, middleTip)

        if (distance < DISTANCE_THRESHOLD) {
            return if (smoothedGesture == "Victory") {
                "ID_CHOPSTICKS_HORIZONTAL"
            } else {
                "ID_CHOPSTICKS_VERTICAL"
            }
        }

        return currentActionId
    }

    /**
     * 다이얼 제스처 판정 (슬라이딩 윈도우 + 쿨다운)
     *
     * 변경점:
     * 1. null 데이터 필터링 후 유효한 좌표만 사용
     * 2. clear() 대신 쿨다운 타이머로 중복 방지
     * 3. 연속 회전 인식 가능
     */
    private fun checkDialGesture(): String? {
        // 버퍼 크기 체크
        if (coordinateBuffer.size < FRAME_BUFFER_SIZE) {
            return null
        }

        // 쿨다운 체크 (너무 빠른 연속 인식 방지)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastDialDetectedTime < DIAL_COOLDOWN_MS) {
            return null
        }

        // null이 아닌 유효한 좌표만 필터링
        val validCoordinates = coordinateBuffer.filterNotNull()

        // 유효한 좌표가 최소 2개 이상 있어야 각도 계산 가능
        if (validCoordinates.size < 2) {
            return null
        }

        // 가장 오래된 유효 좌표와 가장 최신 유효 좌표 비교
        val start = validCoordinates.first()
        val end = validCoordinates.last()

        val angleDiff = GestureClassifier.calculateAngleDiff(
            start.first,   // 시작 손목
            start.second,  // 시작 검지끝
            end.first,     // 끝 손목
            end.second     // 끝 검지끝
        )

        if (angleDiff >= ANGLE_THRESHOLD) {
            // 쿨다운 타이머 설정 (버퍼는 유지)
            lastDialDetectedTime = currentTime
            return "ID_DIAL_TURN"
        }

        return null
    }

    /**
     * 버퍼 초기화
     */
    fun reset() {
        gestureBuffer.clear()
        coordinateBuffer.clear()
        lastDialDetectedTime = 0
    }

    /**
     * 현재 버퍼 상태 (디버깅용)
     */
    fun getBufferStatus(): String {
        val validCoords = coordinateBuffer.count { it != null }
        return "Gesture: ${gestureBuffer.size}/$FRAME_BUFFER_SIZE, " +
                "Coord: $validCoords/${coordinateBuffer.size} (valid/total)"
    }
}
