package com.buulgyeonE202.frontend.ui.gesture.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.data.model.response.CommonResponse
import com.buulgyeonE202.frontend.data.model.response.preset.PresetItem
import com.buulgyeonE202.frontend.data.repository.AuthRepository
import com.buulgyeonE202.frontend.data.repository.GestureRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestureHomeViewModel @Inject constructor(
    private val repository: GestureRepository,
) : ViewModel() {

    private val _presetList = MutableStateFlow<List<PresetItem>>(emptyList())
    val presetList: StateFlow<List<PresetItem>> = _presetList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun loadInitialData() {
        fetchPresets()
    }

    // 프리셋 목록 조회
    fun fetchPresets() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getMappingList()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _presetList.value = response.body()?.data ?: emptyList()
                    Log.d("GestureHome", "목록 갱신 완료: ${_presetList.value.size}개")
                } else {
                    Log.e("GestureHome", "목록 로드 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("GestureHome", "네트워크 에러", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 신규 생성
    fun createNewPreset(title: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true // 로딩 시작
            try {
                val response = repository.createPreset(title)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    fetchPresets()
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("GestureHome", "생성 실패", e)
                _errorEvent.emit("생성 중 오류가 발생했습니다.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 대표 설정
    fun setRepresentative(presetId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.setRepresentative(presetId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    fetchPresets() // 목록 갱신 (정렬 반영)
                }
            } catch (e: Exception) {
                Log.e("GestureHome", "대표 설정 실패", e)
            }
        }
    }

    // 삭제
    fun deletePreset(mappingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deleteMapping(mappingId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d("GestureHomeVM", "✅ 삭제 성공")
                    fetchPresets() // 목록 갱신
                    _errorEvent.emit("삭제되었습니다.")
                } else {
                    val errorBody = parseErrorBody(response)
                    val message = when (errorBody?.code) {
                        "E409-002" -> "대표 매핑셋은 삭제할 수 없습니다."
                        "E403-001" -> "삭제 권한이 없습니다."
                        else -> errorBody?.message ?: "삭제에 실패했습니다."
                    }
                    _errorEvent.emit(message)
                }
            } catch (e: Exception) {
                Log.e("GestureHomeVM", "네트워크 오류", e)
                _errorEvent.emit("네트워크 오류가 발생했습니다.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 이름 변경
    fun updatePresetName(mappingId: Int, newTitle: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.updateMappingName(mappingId, newTitle)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    fetchPresets()
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("GestureHome", "이름 변경 실패", e)
            }
        }
    }

    private fun parseErrorBody(response: retrofit2.Response<*>): CommonResponse<*>? {
        return try {
            val errorJson = response.errorBody()?.string()
            Gson().fromJson(errorJson, CommonResponse::class.java)
        } catch (e: Exception) { null }
    }
}