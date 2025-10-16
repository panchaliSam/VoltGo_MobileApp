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
){
    private var authToken: String? = null

    // Checks whether the user is currently logged in based on stored token.
    suspend fun isLoggedIn(): Boolean {
        return true
    }


    // Attempts to log in a user with the provided credentials and saves the auth token on success.
    suspend fun loginUser(username: String, password: String): Result<AuthResponse> {
        return userRepository.login(username, password).also { result ->
            result.getOrNull()?.let { response ->
                authToken = response.token
                tokenManager.saveToken(response.token)
            }
        }
    }

    // Fetches the authenticated user's profile from the server using the stored token.
    suspend fun getUserProfile(): Result<UserProfileResponse> {
        val token = authToken ?: tokenManager.getToken()
        return token?.let {
            userRepository.getProfile(it)
        } ?: Result.failure(Exception("Not authenticated"))
    }


    // Sends a request to update the user's profile details using the current auth token.
    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): Result<Unit> {
        return authToken?.let { token ->
            userRepository.updateProfile(token, updateProfileRequest)
        } ?: Result.failure(Exception("Not authenticated"))
    }

    // Registers a new user account with the provided personal details and credentials.
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

    // Logs out the current user by clearing the in-memory auth token.
    fun logout(): Boolean {
        authToken = null
        return true
    }
}