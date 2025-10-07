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

    suspend fun isLoggedIn(): Boolean {
        return true
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
        val token = authToken ?: tokenManager.getToken()
        return token?.let {
            userRepository.getProfile(it)
        } ?: Result.failure(Exception("Not authenticated"))
    }


    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): Result<Unit> {
        return authToken?.let { token ->
            userRepository.updateProfile(token, updateProfileRequest)
        } ?: Result.failure(Exception("Not authenticated"))
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

    fun logout(): Boolean {
        authToken = null
        return true
    }
}