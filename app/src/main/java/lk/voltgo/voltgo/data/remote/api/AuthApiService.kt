package lk.voltgo.voltgo.data.remote.api

import lk.voltgo.voltgo.data.remote.dto.AuthResponse
import lk.voltgo.voltgo.data.remote.dto.LoginRequest
import lk.voltgo.voltgo.data.remote.dto.MessageResponse
import lk.voltgo.voltgo.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    //Login
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    //Register
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<MessageResponse>
}