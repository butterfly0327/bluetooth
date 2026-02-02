package com.buulgyeonE202.frontend.di

import android.util.Log // 로그 필수!
import com.buulgyeonE202.frontend.BuildConfig
import com.buulgyeonE202.frontend.data.api.AuthService
import com.buulgyeonE202.frontend.data.api.GestureService
import com.buulgyeonE202.frontend.data.manager.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request()
                val path = request.url.encodedPath

                // 1. 토큰이 '절대' 들어가면 안 되는 Public API 목록
                val skipTokenPaths = listOf(
                    "/v1/auth/login",
                    "/v1/auth/signup",
                    "/v1/auth/signup/checkemail",
                    "/v1/auth/sendcode",
                    "/v1/auth/verifycode"
                )

                // 만약 현재 경로가 위 목록에 포함되어 있다면 토큰 없이 진행
                if (skipTokenPaths.any { path.contains(it) }) {
                    Log.d("API_CHECK", "Public API 호출 (토큰 제외): $path")
                    return@addInterceptor chain.proceed(request)
                }

                // 2. 그 외의 모든 API(비밀번호 변경 포함)는 토큰을 꺼내서 넣기
                val token = tokenManager.getAccessToken()

                Log.d("API_CHECK", "인증 API 호출: $path")
                Log.d("API_CHECK", "헤더에 넣을 토큰: $token")

                val newRequest = request.newBuilder()
                if (!token.isNullOrEmpty()) {
                    newRequest.addHeader("Authorization", "Bearer $token")
                } else {
                    Log.e("API_CHECK", "🚨 경고: 인증이 필요한 API인데 토큰이 없습니다!")
                }

                chain.proceed(newRequest.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideGestureService(retrofit: Retrofit): GestureService = retrofit.create(GestureService::class.java)
}