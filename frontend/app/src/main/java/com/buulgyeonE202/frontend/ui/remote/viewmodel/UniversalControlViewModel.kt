package com.buulgyeonE202.frontend.ui.remote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.data.manager.BluetoothManager
import com.buulgyeonE202.frontend.data.manager.HidControlManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UniversalControlViewModel @Inject constructor(
    private val hidManager: HidControlManager,
    private val bluetoothManager: BluetoothManager
) : ViewModel() {

    val pcConnected = hidManager.connectionState
    val piConnected = bluetoothManager.connectionState

    fun connectAll() {
        hidManager.initialize()
        viewModelScope.launch {
            if (!bluetoothManager.isConnected) {
                bluetoothManager.connectToPi()
            }
        }
    }

    // [PC 제어]
    fun pcNextSlide() { hidManager.sendKey(0x4F.toByte()) }
    fun startPptFromBeginning() { hidManager.sendKey(0x3E.toByte(), 2) }

    // [통합 제어]
    fun startPresentation() {
        viewModelScope.launch {
            hidManager.sendKey(0x4F.toByte())
            bluetoothManager.sendCoordinates_fix(0.5f, 0.5f)
        }
    }

    // 👇 [수정] 짐벌 각도별 개별 제어 함수들
    // (Python 로직: (1.0 - x) * 180 이므로, x=1.0이 0도, x=0.0이 180도)

    // 0도 (왼쪽 끝)
    fun gimbalAngleZero() {
        viewModelScope.launch {
            bluetoothManager.sendCoordinates_fix(1.0f, 0.5f) }
    }

    // 90도 (중앙)
    fun gimbalAngleNinety() {
        viewModelScope.launch { bluetoothManager.sendCoordinates_fix(0.5f, 0.5f) }
    }

    // 180도 (오른쪽 끝)
    fun gimbalAngleOneEighty() {
        viewModelScope.launch { bluetoothManager.sendCoordinates_fix(0.0f, 0.5f) }
    }
}