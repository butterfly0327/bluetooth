package com.buulgyeonE202.frontend.ui.remote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.data.manager.BluetoothManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : ViewModel() {

    // 연결 상태 텍스트
    private val _statusText = MutableStateFlow("연결 안됨")
    val statusText = _statusText.asStateFlow()

    // 1. 연결 함수
    fun connect() {
        viewModelScope.launch {
            _statusText.value = "연결 시도 중..."
            val isSuccess = bluetoothManager.connectToPi()
            if (isSuccess) {
                _statusText.value = "연결 성공! (제어 가능)"
            } else {
                _statusText.value = "연결 실패 (블루투스/전원 확인)"
            }
        }
    }

    // 2. 연결 해제
    fun disconnect() {
        bluetoothManager.disconnect()
        _statusText.value = "연결 해제됨"
    }

    // 3. 테스트 좌표 전송 (슬라이더 값 0.0 ~ 1.0)
    fun sendTestCoordinates(x: Float, y: Float) {
        if (bluetoothManager.isConnected) {
            viewModelScope.launch {
                bluetoothManager.sendCoordinates_fix(x, y)
            }
        }
    }

    // BluetoothTestViewModel.kt
    fun sendPanOnly(x: Float) {
        if (bluetoothManager.isConnected) {
            viewModelScope.launch {
                // y값은 0.5f(중앙)로 고정하여 전송
                bluetoothManager.sendCoordinates_fix(x, 0.5f)
            }
        }
    }
}