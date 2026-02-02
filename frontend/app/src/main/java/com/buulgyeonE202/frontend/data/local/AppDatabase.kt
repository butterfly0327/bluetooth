package com.buulgyeonE202.frontend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.buulgyeonE202.frontend.data.model.GesturePreset

@Database(entities = [GesturePreset::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gestureDao(): GestureDao
}