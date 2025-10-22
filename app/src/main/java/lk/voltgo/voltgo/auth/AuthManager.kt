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

import android.os.Build
import androidx.annotation.RequiresApi
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

    data class TokenClaims(
        val userId: String? = null,
        val role: String? = null,
        val nic: String? = null
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun decodeClaimsFrom(token: String): TokenClaims {
        return try {
            // JWT: header.payload.signature -> we need payload (index 1)
            val parts = token.split(".")
            if (parts.size < 2) return TokenClaims()

            fun String.fixPadding(): String {
                var s = this.replace('-', '+').replace('_', '/')
                while (s.length % 4 != 0) s += "="
                return s
            }

            val payloadBytes = java.util.Base64.getDecoder().decode(parts[1].fixPadding())
            val json = org.json.JSONObject(String(payloadBytes, Charsets.UTF_8))

            TokenClaims(
                userId = json.optString("UserId", null),
                role   = json.optString("Role", null),
                nic    = json.optString("NIC", null)
            )
        } catch (_: Throwable) {
            TokenClaims()
        }
    }

    /** Suspend variant – okay to call from coroutines */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCurrentUserId(): String? {
        val token = authToken ?: tokenManager.getToken()
        return token?.let { decodeClaimsFrom(it).userId }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCurrentNIC(): String? {
        val token = authToken ?: tokenManager.getToken()
        return token?.let { decodeClaimsFrom(it).nic }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCurrentRole(): String? {
        val token = authToken ?: tokenManager.getToken()
        return token?.let { decodeClaimsFrom(it).role }
    }

    /** Fast, non-suspend cache-only access (valid right after login) */
    @RequiresApi(Build.VERSION_CODES.O)
    fun currentUserIdCached(): String? = authToken?.let { decodeClaimsFrom(it).userId }

}
