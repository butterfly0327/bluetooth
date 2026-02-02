package com.buulgyeonE202.frontend.ui.gesture.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buulgyeonE202.frontend.R
import com.buulgyeonE202.frontend.data.model.response.preset.PresetItem
import com.buulgyeonE202.frontend.data.repository.AuthRepository
import com.buulgyeonE202.frontend.data.repository.GestureRepository
import com.buulgyeonE202.frontend.ui.gesture.model.GestureActionItem
import com.buulgyeonE202.frontend.ui.gesture.model.GestureDetailItem
import com.buulgyeonE202.frontend.ui.gesture.model.GestureOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestureViewModel @Inject constructor(
    private val repository: GestureRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _presetList = MutableStateFlow<List<PresetItem>>(emptyList())
    val presetList: StateFlow<List<PresetItem>> = _presetList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _actionList = MutableStateFlow<List<GestureActionItem>>(emptyList())
    val actionList: StateFlow<List<GestureActionItem>> = _actionList.asStateFlow()

    private val _gestureList = MutableStateFlow<List<GestureOption>>(emptyList())
    val gestureList: StateFlow<List<GestureOption>> = _gestureList.asStateFlow()

    private val _currentMappingId = MutableStateFlow<Int?>(null)
    val currentMappingId = _currentMappingId.asStateFlow()

    private val _mappingDetailItems = MutableStateFlow<List<GestureDetailItem>>(emptyList())
    val mappingDetailItems: StateFlow<List<GestureDetailItem>> = _mappingDetailItems.asStateFlow()

    private val _presetTitle = MutableStateFlow("")
    val presetTitle = _presetTitle.asStateFlow()

    fun loadInitialData() {
        fetchPresets()
    }

    // 프리셋 목록 조회
    fun fetchPresets() {
        viewModelScope.launch {
            try {
                val response = repository.getMappingList()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _presetList.value = response.body()?.data ?: emptyList()
                    Log.d("GestureVM", "✅ 프리셋 목록 로드 성공: ${_presetList.value.size}개")
                }
            } catch (e: Exception) {
                Log.e("GestureVM", "❌ 네트워크 에러", e)
            }
        }
    }

    // 초기 프리셋 생성
    fun createInitialPreset(name: String, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            val response = repository.createPreset(name)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                val id = response.body()?.mappingId ?: 0
                _currentMappingId.value = id
                onSuccess(id)
            }
        }
    }

    // 매핑 등록 (저장)
    fun registerMapping(mappingId: Int, actionId: Int, gestureId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            val response = repository.addMappingItem(mappingId, actionId, gestureId)

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                fetchPresets()
                onComplete()
            } else {
                Log.e("GestureVM", "매핑 등록 실패: ${response.code()}")
                onComplete()
            }
        }
    }

    // 1. 기능 목록 조회
    fun fetchActions(mappingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getAvailableActions(mappingId)

                if (response.isSuccessful) {
                    val serverData = response.body() ?: emptyList()

                    _actionList.value = serverData.map { item ->
                        GestureActionItem(
                            id = item.actionId,
                            title = item.name,
                            description = item.description ?: "",
                            category = item.category ?: "기타"
                        )
                    }
                } else if (response.code() == 404) {
                    _actionList.value = emptyList() // 404는 빈 리스트 처리
                }
            } catch (e: Exception) {
                Log.e("GestureVM", "기능 목록 에러", e)
                _actionList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 2. 제스처 목록 조회
    fun fetchGestures(mappingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getAvailableGestures(mappingId)

                if (response.isSuccessful) {
                    val serverData = response.body() ?: emptyList()

                    // 서버 모델(GestureResponseItem) -> UI 모델(GestureOption) 변환
                    _gestureList.value = serverData.map { item ->
                        GestureOption(
                            id = item.gestureId,     // 자동완성: gestureId
                            name = item.name, // 자동완성: gestureName
                            description = ""         // UI 모델 필수값 채우기
                        )
                    }
                } else if (response.code() == 404) {
                    _gestureList.value = emptyList() // 404는 빈 리스트 처리
                }
            } catch (e: Exception) {
                Log.e("GestureVM", "제스처 목록 에러", e)
                _gestureList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 상세 조회
    fun fetchMappingDetail(mappingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getMappingDetail(mappingId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val data = response.body()?.data
                    if (data != null) {
                        _presetTitle.value = data.title

                        _mappingDetailItems.value = data.items.map { item ->
                            GestureDetailItem(
                                id = item.mappingId,
                                actionId = item.actionId,
                                gestureId = item.gestureId, // 서버에서 받은 gestureId 연결
                                category = item.category ?: "기타",
                                actionName = item.actionName,
                                description = item.actionDescription,
                                gestureName = item.gestureName,
                                iconRes = getIconByGestureName(item.gestureName)
                            )
                        }
                    }
                } else {
                    _presetTitle.value = "정보 없음"
                    _mappingDetailItems.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("GestureVM", "❌ 상세 조회 에러", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getIconByGestureName(name: String): Int {
        return when (name) {
            "손 들기" -> R.drawable.ic_launcher_foreground
            // 필요한 경우 추가 아이콘 매핑
            else -> R.drawable.ic_launcher_foreground
        }
    }

    fun setRepresentative(presetId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.setRepresentative(presetId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    fetchPresets()
                }
            } catch (e: Exception) {
                Log.e("GestureVM", "대표 설정 에러", e)
            }
        }
    }

    fun deletePreset(mappingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deleteMapping(mappingId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    fetchPresets()
                }
            } catch (e: Exception) {
                Log.e("GestureVM", "삭제 에러", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteItem(mappingId: Int, mappingItemId: Int) {
        viewModelScope.launch {
            try {
                // repository 호출 시 mappingId 함께 전달
                val response = repository.deleteMappingItem(mappingId, mappingItemId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    // 성공 시 리스트 갱신
                    _mappingDetailItems.value = _mappingDetailItems.value.filter { it.id != mappingItemId }
                    Log.d("GESTURE", "삭제 성공: $mappingItemId")
                } else {
                    Log.e("GESTURE", "삭제 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("GESTURE", "삭제 중 에러", e)
            }
        }
    }

    // 편집 모드 상태 (true면 삭제 버튼 보임)

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    // 편집 모드 전환 함수
    fun setEditMode(enabled: Boolean) {
        _isEditMode.value = enabled
    }
}