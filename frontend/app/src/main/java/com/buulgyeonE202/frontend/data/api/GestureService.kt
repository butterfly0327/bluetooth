package com.buulgyeonE202.frontend.data.api

import DeleteMappingItemResponse
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
import retrofit2.http.*

interface GestureService {

    // 매핑(프리셋) 목록 조회
    @GET("v1/mappings/list")
    suspend fun getMappingList(): Response<CommonResponse<List<PresetItem>>>

    // 프리셋 생성
    @POST("v1/mappings/create")
    suspend fun createPreset(@Body request: CreatePresetRequest): Response<CreatePresetResponse>

    // 3.11 사용 가능한 액션 조회
    @GET("v1/mappings/meta/actions/{mappingId}")
    suspend fun getAvailableActions(
        @Path("mappingId") mappingId: Int
    ): Response<List<ActionItem>>

    // 3.10 사용 가능한 제스처 조회
    @GET("v1/mappings/active/gestures/{mappingId}")
    suspend fun getAvailableGestures(
        @Path("mappingId") mappingId: Int
    ): Response<List<GestureResponseItem>>

    // 3.2 매핑 아이템 추가
    @POST("v1/mappings/{mappingId}/add-item")
    suspend fun addMappingItem(
        @Path("mappingId") mappingId: Int,
        @Body request: AddPresetItemRequest
    ): Response<CommonResponse<Any>>

    // 3.3 매핑셋 상세 조회
    @GET("v1/mappings/{mappingId}")
    suspend fun getMappingDetail(
        @Path("mappingId") mappingId: Int
    ): Response<CommonResponse<MappingDetailData>>

    // 2. 제스처 매핑 수정 (PATCH)
    @PATCH("v1/presets/mappings/{mappingId}")
    suspend fun updateMapping(
        @Path("mappingId") mappingId: Int,
        @Body request: MappingUpdateRequest
    ): Response<CommonResponse<Any>>

    // 3.6 이름 변경
    @PATCH("v1/mappings/{mappingId}/change-name")
    suspend fun updateMappingName(
        @Path("mappingId") mappingId: Int,
        @Body request: MappingNameChangeRequest
    ): Response<CommonResponse<Any>>

    // 3.7 삭제
    @DELETE("v1/mappings/{mappingId}")
    suspend fun deleteMapping(
        @Path("mappingId") mappingId: Int
    ): Response<CommonResponse<Any>>

    // 3.4 대표 설정
    @POST("v1/mappings/{presetId}/apply")
    suspend fun applyRepresentative(
        @Path("presetId") presetId: Int
    ): Response<CommonResponse<RepresentativeResponse>>

    // 매핑 아이템 삭제
    @DELETE("v1/mappings/{mappingId}/delete-item/{mappingItemId}")
    suspend fun deleteMappingItem(
        @Path("mappingId") mappingId: Int,
        @Path("mappingItemId") mappingItemId: Int
    ): Response<DeleteMappingItemResponse>
}