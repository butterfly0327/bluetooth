package com.buulgyeonE202.frontend.di

import com.buulgyeonE202.frontend.data.api.AuthService
import com.buulgyeonE202.frontend.data.api.GestureService
import com.buulgyeonE202.frontend.data.manager.BluetoothManager
import com.buulgyeonE202.frontend.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 4. BluetoothManager (라즈베리파이 통신 매니저) [추가된 부분]
    @Provides
    @Singleton
    fun provideBluetoothManager(): BluetoothManager {
        return BluetoothManager()
    }
}