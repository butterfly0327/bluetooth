package com.buulgyeonE202.frontend.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gesture_presets")
data class GesturePreset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String, // 아이콘 식별자
    val description: String = "",

    // 🔥 [이게 빠져있어서 에러가 난 겁니다] 순서 저장 필드 추가!
    val orderIndex: Int = 0,

    val isFavorite: Boolean = false
)