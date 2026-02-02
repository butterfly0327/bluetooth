package com.buulgyeonE202.frontend.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.buulgyeonE202.frontend.data.model.request.auth.SignupRequest

interface SystemService {
    @POST("/v1/auth/signup")
    suspend fun signUp(@Body request: SignupRequest): Response<String>
}