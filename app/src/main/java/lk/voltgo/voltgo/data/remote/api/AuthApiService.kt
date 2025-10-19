/**
 * ------------------------------------------------------------
 * File: AuthApiService.kt
 * Author: Panchali Samarasinghe
 * Date: [Insert Date]
 *
 * Description:
 * This interface defines API endpoints for user authentication
 * and profile management in the VoltGo app. It includes methods
 * for user login, registration, fetching user profiles, and
 * updating profile details. Retrofit is used for network
 * communication, and all requests are suspended functions.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.remote.api

import lk.voltgo.voltgo.data.remote.dto.AuthResponse
import lk.voltgo.voltgo.data.remote.dto.LoginRequest
import lk.voltgo.voltgo.data.remote.dto.MessageResponse
import lk.voltgo.voltgo.data.remote.dto.RegisterRequest
import lk.voltgo.voltgo.data.remote.dto.UpdateProfileRequest
import lk.voltgo.voltgo.data.remote.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {

    // Sends a login request with user credentials and returns an authentication token upon success.
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // Registers a new user by sending registration details to the backend.
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<MessageResponse>

    // Retrieves the currently logged-in user's profile using the authorization token.
    @GET("/api/auth/profile")
    suspend fun getProfile(
    ): Response<UserProfileResponse>

    // Updates the logged-in user's profile information using the provided token and request body.
    @PATCH("/api/auth/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<MessageResponse>
}