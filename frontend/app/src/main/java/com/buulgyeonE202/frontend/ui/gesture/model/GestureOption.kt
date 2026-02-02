package com.buulgyeonE202.frontend.ui.gesture.model

import com.buulgyeonE202.frontend.R

data class GestureOption(
    val id: Int,
    val name: String,
    val description: String
) {
    val imageRes: Int
        get() = when (id) {
            3 -> R.drawable.img_gesture_raise           // 손 들기
            8 -> R.drawable.img_gesture_raise           // 양손 들기 (일단 손 들기 이미지 사용)
            4 -> R.drawable.img_gesture_chopsticks      // 왼쪽 스와이프
            5 -> R.drawable.img_gesture_chopsticks_horizontal // 오른쪽 스와이프
            6 -> R.drawable.img_gesture_closed_fist     // 주먹 쥐기
            7 -> R.drawable.img_gesture_dial_turn       // 손바닥 펴기

            else -> R.drawable.img_gesture_ok           // 기본값 (ID 매칭 안될 때)
        }
}