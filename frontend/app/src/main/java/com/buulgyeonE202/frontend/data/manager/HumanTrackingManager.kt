package com.buulgyeonE202.frontend.data.manager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.data.manager.BluetoothManager  // 라즈베리파이용
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt
import android.graphics.Matrix

/**
 * 휴먼 트래킹 전용 매니저
 *
 * 목적: 사람이 카메라 중앙에서 벗어나면 라즈베리파이에 연결된 모터가
 *       어느 방향으로 얼만큼 회전해야 하는지 알려줌
 *
 * 통신: BluetoothManager → 라즈베리파이 (SPP)
 * 전송 데이터: JSON {"ex", "ev", "sx", "sv"}
 */
@HiltViewModel
class HumanTrackingManager @Inject constructor(
    private val bluetoothManager: BluetoothManager  // 라즈베리파이 통신용
) : ViewModel() {

    // [10Hz] 전송 시 필요
    private var lastSendTime: Long = 0

    private var currentDirection: String = "CENTER"

    companion object {
        private const val TAG = "HumanTrackingManager"
        private const val VISIBILITY_THRESHOLD = 0.6

        // 규동씨랑 이야기해보고 오른쪽으로 갈 수록 0이 되는게 아니면 이걸 바꿀 것
        private const val OUT_LEFT_THRESHOLD = 0.33f    // 이보다 작으면 RIGHT 탈출
        private const val IN_LEFT_THRESHOLD = 0.4125f   // 이보다 커야 CENTER 복귀
        private const val IN_RIGHT_THRESHOLD = 0.5775f  // 이보다 작아야 CENTER 복귀
        private const val OUT_RIGHT_THRESHOLD = 0.66f   // 이보다 크면 LEFT 탈출
    }

    // [추가] 클래스 생성 시 자동 연결 시도
    init {
        connectToRaspberryPi()
    }

    fun connectBluetooth() {
        viewModelScope.launch {
            if (!bluetoothManager.isConnected) {
                val success = bluetoothManager.connectToPi()
                Log.d(TAG, if (success) "라즈베리파이 연결 성공" else "라즈베리파이 연결 실패")
            }
        }
    }

    private fun connectToRaspberryPi() {
        viewModelScope.launch {
            Log.d(TAG, "라즈베리파이 블루투스 자동 연결 시도 중...")
            val isSuccess = bluetoothManager.connectToPi()
            if (isSuccess) {
                Log.d(TAG, "라즈베리파이 블루투스 연결 성공!")
            } else {
                Log.e(TAG, "라즈베리파이 블루투스 연결 실패 (페어링 상태 및 파이썬 서버 확인)")
            }
        }
    }

    // 부위별 랜드마크 인덱스 (Python 코드와 동일)
    private val partsConfig = mapOf(
        "e" to listOf(2, 5),           // Eyes: 오른쪽 눈(2), 왼쪽 눈(5)
        "s" to listOf(11, 12),         // Shoulders: 왼쪽 어깨(11), 오른쪽 어깨(12)
//        "t" to listOf(11, 12, 23, 24)  // Torso: 어깨 + 골반, 현재 안씀
    )

    // 이전 프레임 데이터
    private var prevTime: Long = 0
    private val prevCoords = mutableMapOf<String, Pair<Float, Float>?>(
        "e" to null,
        "s" to null,
//        "t" to null
    )

    // 현재 속도
    private val speeds = mutableMapOf<String, Float>(
        "e" to 0f,
        "s" to 0f,
//        "t" to 0f
    )

    // UI 업데이트용 StateFlow
    data class TrackingState(
        val direction: String = "CENTER",
        val eyePos: Pair<Float, Float>? = null,
        val shoulderPos: Pair<Float, Float>? = null,
//        val torsoPos: Pair<Float, Float>? = null,
        val eyeSpeed: Int = 0,
        val shoulderSpeed: Int = 0,
//        val torsoSpeed: Int = 0
        val imageWidth: Int = 0,
        val imageHeight: Int = 0
    )


    private val _trackingState = MutableStateFlow(TrackingState())
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

    /**
     * 포즈 감지 결과 처리
     * 현재 요청한대로 정규화 값 그대로 쓸거라 width , heigth 사용 X
     * 혹시 나중을 위해 입력은 받아두는걸로
     */
    fun onPoseDetected(result: PoseLandmarkerResult, width: Int, height: Int, rotation: Int, isFront: Boolean) {
        val landmarks = result.landmarks().firstOrNull() ?: return

        val currTime = System.currentTimeMillis()
        val dt = if (prevTime == 0L) 0.033f else (currTime - prevTime) / 1000f
        prevTime = currTime

        val currentPixels = mutableMapOf<String, Pair<Float, Float>?>() // 시각화용
        val controlPixels = mutableMapOf<String, Float>() // 제어용

        // 각 부위별 처리
        for ((key, indices) in partsConfig) {
            val center = calculateCenterAll(landmarks, indices)

            if (center != null) {
                val px = center.first // 정규화 값 그대로 사용 (만약 화면 상의 픽셀이 필요할 경우 ->  * width)
                val py = center.second // 정규화 값 그대로 사용 (만약 화면 상의 픽셀이 필요할 경우 ->  * height)

                currentPixels[key] = Pair(px, py)
                Log.d(TAG, "currentPixels: $px $py")

                // 2. 제어용: 폰 회전 및 모터 방향에 맞춘 X 좌표 계산
                // 규동님이랑 이야기 해보고 바꾸기
                // 지금 오른쪽으로 갈 수록 좌표 작아짐, 반대였던거 같은데
                // 반대면 전부 그냥 px를 넣고
                var controlX = when {
                    rotation == 0 -> 1.0f - px
                    rotation == 90 -> 1.0f - px
                    rotation == 180 -> 1.0f - px
                    else -> 1.0f - px
                }

                controlPixels[key] = controlX // 제어용 좌표 맵에 저장
                Log.d(TAG, "controlPixels: $controlPixels")

                // 속도 계산
                speeds[key] = calculateSpeed(prevCoords[key], Pair(px, py), dt)
                prevCoords[key] = Pair(px, py)

            } else {
                currentPixels[key] = null
                speeds[key] = 0f
                prevCoords[key] = null
            }
        }

        // 방향 판정
        val validControlX = controlPixels.values.toList()
        val direction = determineDirection(validControlX)
        Log.d(TAG, "판정 방향: $direction")

        // StateFlow 업데이트 (UI 시각화용)
        // UI 업데이트용 데이터는 실제 픽셀 좌표로 변환하여 저장 (UI에 표시해야하므로 정규화 X)
        _trackingState.value = TrackingState(
            direction = direction,
            eyePos = currentPixels["e"],
            shoulderPos = currentPixels["s"],
//            torsoPos = currentPixels["t"]?.let { Pair(it.first.toInt(), it.second.toInt()) },
            eyeSpeed = (speeds["e"]?.let { it * 1000 } ?: 0f).toInt(),
            shoulderSpeed = (speeds["s"]?.let { it * 1000 } ?: 0f).toInt(),
//            torsoSpeed = speeds["t"]?.toInt() ?: 0
            imageWidth = width,
            imageHeight = height
        )

        // [10Hz] 전송 시 필요
        if (direction != "CENTER" && (currTime - lastSendTime >= 100)) {
            lastSendTime = currTime // 전송 시간 업데이트

            val jsonOutput = buildJsonString(controlPixels, speeds)
            sendToRaspberryPi(jsonOutput)
            Log.d("10hz", "통신")
            Log.d("10hz", "보내는 좌표: $jsonOutput")
        }

    }

    /**
     * 지정한 인덱스들의 중심 좌표 반환 (가시성 체크 포함)
     */
    private fun calculateCenterAll(
        landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>,
        indices: List<Int>
    ): Pair<Float, Float>? {
        val targetPoints = indices.mapNotNull { idx ->
            landmarks.getOrNull(idx)
        }

        if (targetPoints.size != indices.size) return null

        // 가시성 평균 계산
        val avgVisibility = targetPoints.map { it.visibility().orElse(0f) }.average()

        if (avgVisibility <= VISIBILITY_THRESHOLD) {
            return null
        }

        // 중심점 계산
        val avgX = targetPoints.map { it.x() }.average().toFloat()
        val avgY = targetPoints.map { it.y() }.average().toFloat()

        return Pair(avgX, avgY)
    }

    /**
     * 두 점 사이의 속도 계산 (픽셀/초)
     */
    private fun calculateSpeed(p1: Pair<Float, Float>?, p2: Pair<Float, Float>, dt: Float): Float {
        if (p1 == null || dt <= 0) return 0f
        val dist = sqrt(
            (p2.first - p1.first).pow(2) + (p2.second - p1.second).pow(2)
        )
        return dist / dt
    }

    /**
     * 방향 판정 (히스테리시스 로직)
     */
    private fun determineDirection(controlXCoords: List<Float>): String {
        if (controlXCoords.isEmpty()) return "CENTER"

        currentDirection = when (currentDirection) {
            "RIGHT" -> {
                // 이미 오른쪽(LEFT_OUT) 상태일 때 -> 모든 점이 0.4125를 넘어야 중앙 복귀
                if (controlXCoords.all { it >= IN_LEFT_THRESHOLD }) "CENTER" else "RIGHT"
            }
            "LEFT" -> {
                // 이미 왼쪽(RIGHT_OUT) 상태일 때 -> 모든 점이 0.5775보다 작아야 중앙 복귀
                if (controlXCoords.all { it <= IN_RIGHT_THRESHOLD }) "CENTER" else "LEFT"
            }
            else -> { // "CENTER" 상태일 때
                when {
                    // 하나라도 0.33 미만이면 RIGHT로 탈출
                    controlXCoords.any { it < OUT_LEFT_THRESHOLD } -> "RIGHT"
                    // 하나라도 0.66 초과면 LEFT로 탈출
                    controlXCoords.any { it > OUT_RIGHT_THRESHOLD } -> "LEFT"
                    else -> "CENTER"
                }
            }
        }
        return currentDirection
    }

    /**
     * JSON 문자열 생성 (Python 코드 포맷과 동일)
     * 라즈베리파이가 파싱할 수 있는 형태
     */
    private fun buildJsonString(
//        dir: String,
//        pixels: Map<String, Pair<Float, Float>?>,
        controlXMap: Map<String, Float>, // Pair 대신 Float 맵을 받음
        speeds: Map<String, Float>
    ): String {
//        val ex = pixels["e"]?.first?.let { (it * 1000).toInt() } ?: 0
        val ex = ((controlXMap["e"] ?: 0.5f) * 1000).toInt()
//        val ey = pixels["e"]?.second?.let {(it * 1000).toInt() } ?: 0
        val ev = ((speeds["e"] ?: 0f) * 1000).toInt()

//        val sx = pixels["s"]?.first?.let {(it * 1000).toInt() } ?: 0
        val sx = ((controlXMap["s"] ?: 0.5f) * 1000).toInt()
//        val sy = pixels["s"]?.second?.let {(it * 1000).toInt() } ?: 0
        val sv = ((speeds["s"] ?: 0f) * 1000).toInt()

//        val tx = pixels["t"]?.first?.toInt() ?: 0
//        val ty = pixels["t"]?.second?.toInt() ?: 0
//        val tv = speeds["t"]?.toInt() ?: 0

        return """{"ex":$ex, "ev":$ev,"sx":$sx, "sv":$sv}"""
    }

    /**
     * 라즈베리파이로 데이터 전송 (BluetoothManager 사용)
     */
    private fun sendToRaspberryPi(jsonString: String) {
        viewModelScope.launch {
            try {
                if (bluetoothManager.isConnected) {
                    bluetoothManager.sendCoordinates(jsonString)
                    Log.d(TAG, "라즈베리파이 전송: $jsonString")
                } else {
                    Log.d(TAG, "라즈베리파이 연결 안됨")
                }
            } catch (e: Exception) {
                Log.e(TAG, "전송 실패: ${e.message}")
            }
        }
    }

    /**
     * 상태 초기화
     */
    fun reset() {
        prevTime = 0
        prevCoords.keys.forEach { prevCoords[it] = null }
        speeds.keys.forEach { speeds[it] = 0f }
        _trackingState.value = TrackingState()
    }
}
