/**
 * ------------------------------------------------------------
 * File: AuthManager.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * Handles all authentication-related logic for the VoltGo app,
 * including user login, registration, profile retrieval, and
 * token management using the UserRepository and TokenManager.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.auth

import lk.voltgo.voltgo.data.remote.dto.AuthResponse
import lk.voltgo.voltgo.data.remote.dto.UpdateProfileRequest
import lk.voltgo.voltgo.data.remote.dto.UserProfileResponse
import lk.voltgo.voltgo.data.repository.UserRepository
import lk.voltgo.voltgo.domain.model.User
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) {
    private var authToken: String? = null

    suspend fun isLoggedIn(): Boolean {
        val token = authToken ?: tokenManager.getToken()
        return !token.isNullOrEmpty()
    }

    suspend fun loginUser(username: String, password: String): Result<AuthResponse> {
        return userRepository.login(username, password).also { result ->
            result.getOrNull()?.let { response ->
                authToken = response.token
                tokenManager.saveToken(response.token)
            }
        }
    }

    suspend fun getUserProfile(): Result<UserProfileResponse> {
        // Interceptor adds the token automatically
        return userRepository.getProfile()
    }

    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): Result<Unit> {
        // No token check needed — interceptor adds header
        return userRepository.updateProfile(updateProfileRequest)
    }


    suspend fun registerUser(
        email: String,
        phone: String,
        password: String,
        nic: String,
        fullName: String,
        address: String
    ): Result<User> {
        return userRepository.register(
            email = email,
            phone = phone,
            password = password,
            nic = nic,
            fullName = fullName,
            address = address
        )
    }

    suspend fun logout(): Boolean {
        authToken = null
        tokenManager.clearToken()
        return true
    }
}
