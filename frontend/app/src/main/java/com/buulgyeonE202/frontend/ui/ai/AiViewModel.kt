package com.buulgyeonE202.frontend.ui.ai

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buulgyeonE202.frontend.data.manager.HidControlManager
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.buulgyeonE202.frontend.data.manager.AiManager

/**
 * 제스처 인식 ViewModel
 *
 * 역할:
 * - 상태 관리 (LiveData)
 * - AiManager에 로직 위임
 * - HidControlManager로 통신
 *
 * 통신: HidControlManager → 컴퓨터 (HID Keyboard)
 */
@HiltViewModel
class AiViewModel @Inject constructor(
    private val aiManager: AiManager,              // 순수 로직 담당
    private val hidControlManager: HidControlManager  // 컴퓨터 HID 통신
) : ViewModel() {

    companion object {
        private const val TAG = "AiViewModel"
    }

    // 디버깅 및 UI용 LiveData
    private val _gestureLog = MutableLiveData<String>()
    val gestureLog: LiveData<String> get() = _gestureLog

    private val _lastActionId = MutableLiveData<String>()
    val lastActionId: LiveData<String> get() = _lastActionId

    // ★ 2초 딜레이용
    private var lastSentTime = 0L
    private val sendDelayMs = 1200L  // 2초

    // 제스처 → HID 키코드 매핑 (PPT 단축키 등)
    private val gestureToKeyCode = mapOf(
        "ID_VICTORY" to 0x4F.toByte(),          // → 다음 페이지 (Right Arrow)
        "ID_VICTORY_90" to 0x50.toByte(),        // ← 이전 페이지 (Left Arrow)
    )

    /**
     * MediaPipe 제스처 인식 결과 처리
     */
    fun onGestureDetected(result: GestureRecognizerResult) {
        // AiManager에 로직 위임
        val actionId = aiManager.processGestureResult(result)

        // 버퍼가 아직 안 찼으면 null 반환됨
        if (actionId == null) {
            _gestureLog.postValue("버퍼링 중... ${aiManager.getBufferStatus()}")
            return
        }

        // 상태 업데이트
        _lastActionId.postValue(actionId)
        _gestureLog.postValue("Action: $actionId")

        // HID로 컴퓨터에 전송
        sendToReceiver(actionId)

        Log.d(TAG, "Final Action Sent: $actionId")
    }

    /**
     * HID 통신 (컴퓨터로 키코드 전송)
     */
    private fun sendToReceiver(actionId: String) {
        if (!hidControlManager.connectionState.value) {
            Log.d("AiViewModel", "HID 연결 안됨")
            return
        }

        val keyCode = gestureToKeyCode[actionId]
        if (keyCode == null) {
            Log.d(TAG, "매핑되지 않은 제스처: $actionId")
            return
        }

        // ★ 2초 딜레이 체크
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSentTime < sendDelayMs) {
            return  // 아직 2초 안 지남
        }

        lastSentTime = currentTime
        hidControlManager.sendKey(keyCode)
        Log.d(TAG, "키 전송: $actionId")
    }

    /**
     * 버퍼 초기화
     */
    fun reset() {
        aiManager.reset()
        _gestureLog.postValue("초기화됨")
        _lastActionId.postValue("")
    }

    override fun onCleared() {
        super.onCleared()
        aiManager.reset()
    }
}
