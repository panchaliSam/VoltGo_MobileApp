/**
 * ------------------------------------------------------------
 * File: UserRepository.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This repository class handles all user-related operations such as
 * login, registration, profile retrieval, and profile updates.
 * It communicates with the remote AuthApiService for API calls and
 * the local UserDao for storing user information locally.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.data.repository

import lk.voltgo.voltgo.data.local.dao.UserDao
import lk.voltgo.voltgo.data.local.entities.UserEntity
import lk.voltgo.voltgo.data.remote.api.AuthApiService
import lk.voltgo.voltgo.data.remote.dto.AuthResponse
import lk.voltgo.voltgo.data.remote.dto.LoginRequest
import lk.voltgo.voltgo.data.remote.dto.RegisterRequest
import lk.voltgo.voltgo.data.remote.dto.UpdateProfileRequest
import lk.voltgo.voltgo.data.remote.dto.UserProfileResponse
import lk.voltgo.voltgo.data.remote.types.RoleType
import lk.voltgo.voltgo.domain.model.User
import lk.voltgo.voltgo.domain.model.toDomain
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val userDao: UserDao
){
    // Handles user login by sending credentials to the backend API.
    // Returns an AuthResponse object on successful authentication.
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email = username.trim(), password = password)
            val response = authApiService.login(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Handles user registration by sending registration details to the backend.
    // On success, saves the user data locally using UserDao and returns the domain model.
    suspend fun register(
        email: String,
        phone: String,
        password: String,
        nic: String,
        fullName: String,
        address: String
    ): Result<User> {
        return try {
            // Default role as EVOwner
            val request = RegisterRequest(
                email = email.trim(),
                phone = phone.trim(),
                password = password,
                role = RoleType.EV_OWNER,
                nic = nic.trim(),
                fullName = fullName.trim(),
                address = address.trim()
            )

            val response = authApiService.register(request)

            if (response.isSuccessful) {
                val messageResponse = response.body()
                if (messageResponse != null) {
                    val userEntity = UserEntity(
                        userId = UUID.randomUUID().toString(),
                        email = email.trim(),
                        phone = phone.trim(),
                        role = RoleType.EV_OWNER,                // default as requested
                        isActive = true,
                        nic = nic.trim(),
                        fullName = fullName.trim(),
                        address = address.trim(),
                        createdAt = "",               // backend does not return createdAt
                        lastLoginAt = null
                    )
                    userDao.insertUser(userEntity)
                    Result.success(userEntity.toDomain())
                } else {
                    Result.failure(Exception("Registration failed: No response body"))
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    // Fetches the currently authenticated user's profile details using the provided token.
    suspend fun getProfile(token: String): Result<UserProfileResponse> {
        return try {
            val response = authApiService.getProfile("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Updates the authenticated user's profile information on the backend using the provided token and request data.
    suspend fun updateProfile(
        token: String,
        req: UpdateProfileRequest
    ): Result<Unit> = try {
        val response = authApiService.updateProfile(
            authHeader = "Bearer $token",
            request = req
        )
        if (response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("Update failed: ${response.code()} ${response.message()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }

}