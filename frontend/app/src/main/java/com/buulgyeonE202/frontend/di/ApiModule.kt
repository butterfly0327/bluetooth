package com.buulgyeonE202.frontend.di

import com.buulgyeonE202.frontend.data.api.SystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideSystemService(retrofit: Retrofit): SystemService {
        return retrofit.create(SystemService::class.java)
    }
}