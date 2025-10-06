package lk.voltgo.voltgo.auth

import lk.voltgo.voltgo.data.remote.dto.AuthResponse
import lk.voltgo.voltgo.data.repository.UserRepository
import lk.voltgo.voltgo.domain.model.User
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val userRepository: UserRepository
){

    fun isLoggedIn(): Boolean {
        // Implement actual authentication check logic here
        return true
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

    suspend fun loginUser(username: String, password: String): Result<AuthResponse> {
        return userRepository.login(username, password)
    }

    fun logout(): Boolean {
        // Implement logout logic here
        return false
    }

}