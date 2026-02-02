package com.buulgyeonE202.frontend.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.buulgyeonE202.frontend.data.model.GesturePreset
import kotlinx.coroutines.flow.Flow

@Dao
interface GestureDao {
    // 1. 모든 데이터 가져오기 (순서대로)
    @Query("SELECT * FROM gesture_presets ORDER BY orderIndex ASC")
    fun getAllPresets(): Flow<List<GesturePreset>>

    // 2. 데이터 추가 (단건)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: GesturePreset)

    // 🔥 [추가 1] 리스트 통째로 추가 (서버에서 받은 목록 저장용)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<GesturePreset>)

    // 3. 순서 업데이트
    @Update
    suspend fun updatePresets(presets: List<GesturePreset>)

    // 🔥 [추가 2] 모든 데이터 삭제 (서버 목록으로 새로고침 할 때 사용)
    @Query("DELETE FROM gesture_presets")
    suspend fun clearAllPresets()

    // 🔥 [추가 3] 특정 아이디 삭제 (삭제 기능용)
    @Query("DELETE FROM gesture_presets WHERE id = :id")
    suspend fun deletePresetById(id: Long)

    // 4. 대표 제스처 설정 (Transaction)
    @Query("UPDATE gesture_presets SET isFavorite = 0")
    suspend fun clearAllFavorites()

    @Query("UPDATE gesture_presets SET isFavorite = 1 WHERE id = :id")
    suspend fun setFavorite(id: Long)

    @Transaction
    suspend fun updateRepresentative(id: Long) {
        clearAllFavorites()
        setFavorite(id)
    }

    @Query("UPDATE gesture_presets SET iconName = :iconName WHERE description = :actionTitle")
    suspend fun updateGestureForAction(actionTitle: String, iconName: String)
}