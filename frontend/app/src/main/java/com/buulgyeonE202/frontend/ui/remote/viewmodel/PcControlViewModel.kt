package com.buulgyeonE202.frontend.ui.remote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.data.manager.HidControlManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PcControlViewModel @Inject constructor(
    private val hidManager: HidControlManager
) : ViewModel() {

    // 초기화
    init {
        hidManager.initialize()
    }

    // 기능: PPT 다음장 (오른쪽 화살표)
    fun nextSlide() {
        viewModelScope.launch {
            hidManager.sendKey(0x4F.toByte()) // 0x4F: Right Arrow
        }
    }

    // 기능: 재생/정지 (스페이스바)
    fun toggleMedia() {
        viewModelScope.launch {
            hidManager.sendKey(0x2C.toByte()) // 0x2C: Spacebar
        }
    }
}