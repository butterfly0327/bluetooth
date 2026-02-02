package com.buulgyeonE202.frontend.data.model.response.preset

data class RepresentativeResponse(
    val appliedPreset: PresetInfo,
    val previousPreset: PresetInfo?, // 처음 설정할 때는 null이 올 수 있으므로 nullable 처리
    val appliedAt: String
)