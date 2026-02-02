package com.buulgyeonE202.frontend.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.buulgyeonE202.frontend.data.local.AppDatabase
import com.buulgyeonE202.frontend.data.local.GestureDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gesture_database"
        )
            .fallbackToDestructiveMigration() // 버전 변경 시 초기화
            .build()
    }

    @Provides
    fun provideGestureDao(database: AppDatabase): GestureDao {
        return database.gestureDao()
    }
}