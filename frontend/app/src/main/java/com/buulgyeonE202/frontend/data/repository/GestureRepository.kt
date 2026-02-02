package com.buulgyeonE202.frontend.data.repository

import DeleteMappingItemResponse
import com.buulgyeonE202.frontend.data.api.GestureService
import com.buulgyeonE202.frontend.data.model.request.preset.AddPresetItemRequest
import com.buulgyeonE202.frontend.data.model.request.preset.CreatePresetRequest
import com.buulgyeonE202.frontend.data.model.request.preset.MappingNameChangeRequest
import com.buulgyeonE202.frontend.data.model.request.preset.MappingUpdateRequest
import com.buulgyeonE202.frontend.data.model.response.CommonResponse
import com.buulgyeonE202.frontend.data.model.response.action.ActionItem
import com.buulgyeonE202.frontend.data.model.response.action.GestureResponseItem
import com.buulgyeonE202.frontend.data.model.response.mapping.MappingDetailData
import com.buulgyeonE202.frontend.data.model.response.preset.CreatePresetResponse
import com.buulgyeonE202.frontend.data.model.response.preset.PresetItem
import com.buulgyeonE202.frontend.data.model.response.preset.RepresentativeResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GestureRepository @Inject constructor(
    private val gestureService: GestureService
) {
    // 3.5 매핑 목록 조회
    suspend fun getMappingList(): Response<CommonResponse<List<PresetItem>>> {
        return gestureService.getMappingList()
    }

    // 3.1 프리셋 생성
    suspend fun createPreset(title: String): Response<CreatePresetResponse> {
        return gestureService.createPreset(CreatePresetRequest(title = title))
    }

    // 🔥 [중요] 404 처리를 위해 Response 타입으로 반환
    suspend fun getAvailableActions(mappingId: Int): Response<List<ActionItem>> {
        return gestureService.getAvailableActions(mappingId)
    }

    // 🔥 [중요] 404 처리를 위해 Response 타입으로 반환
    suspend fun getAvailableGestures(mappingId: Int): Response<List<GestureResponseItem>> {
        return gestureService.getAvailableGestures(mappingId)
    }

    // 3.2 아이템 추가
    suspend fun addMappingItem(mappingId: Int, actionId: Int, gestureId: Int): Response<CommonResponse<Any>> {
        val request = AddPresetItemRequest(actionId, gestureId)
        return gestureService.addMappingItem(mappingId, request)
    }

    // 3.0 매핑 수정
    suspend fun updateMapping(mappingId: Int, actionId: Int, gestureId: Int): Response<CommonResponse<Any>> {
        val request = MappingUpdateRequest(actionId = actionId, gestureId = gestureId)
        return gestureService.updateMapping(mappingId, request)
    }

    // 3.3 상세 조회
    suspend fun getMappingDetail(mappingId: Int): Response<CommonResponse<MappingDetailData>> {
        return gestureService.getMappingDetail(mappingId)
    }

    // 3.6 이름 변경
    suspend fun updateMappingName(mappingId: Int, newTitle: String): Response<CommonResponse<Any>> {
        val request = MappingNameChangeRequest(title = newTitle)
        return gestureService.updateMappingName(mappingId, request)
    }

    // 3.7 삭제
    suspend fun deleteMapping(mappingId: Int): Response<CommonResponse<Any>> {
        return gestureService.deleteMapping(mappingId)
    }

    // 3.4 대표 설정
    suspend fun setRepresentative(presetId: Int): Response<CommonResponse<RepresentativeResponse>> {
        return gestureService.applyRepresentative(presetId)
    }

    // 매핑 아이템 삭제 함수
    suspend fun deleteMappingItem(mappingId: Int, mappingItemId: Int): Response<DeleteMappingItemResponse> {
        return gestureService.deleteMappingItem(mappingId, mappingItemId)
    }
}