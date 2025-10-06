package lk.voltgo.voltgo.data.remote.dto

import com.google.gson.annotations.SerializedName

// AuthResponse
data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("user")
    val user: UserDto
)

// User object inside the response
data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("nic")
    val nic: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("address")
    val address: String
)

//Register request
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String = "EVOwner",
    @SerializedName("nic")
    val nic: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("address")
    val address: String
)

// Login request
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

// Register response
data class MessageResponse(
    val message: String
)