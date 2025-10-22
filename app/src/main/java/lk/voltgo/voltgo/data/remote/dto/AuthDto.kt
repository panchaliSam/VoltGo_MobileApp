/**
 * ------------------------------------------------------------
 * File: AuthDto.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This file defines all authentication-related data transfer objects (DTOs)
 * used for communicating with the backend API. It includes models for login,
 * registration, user profile, and API response handling within the VoltGo app.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.data.remote.dto

import com.google.gson.annotations.SerializedName
import lk.voltgo.voltgo.data.remote.types.RoleType

// Represents the response returned from the authentication API after login.
data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("role")
    val role: RoleType,
)

// Represents the user object included in the AuthResponse.
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

// Represents the data structure for a user registration request.
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: RoleType = RoleType.EV_OWNER,
    @SerializedName("nic")
    val nic: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("address")
    val address: String
)

// Represents the data structure for a user login request.
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

// Represents a simple message-based response from the API, typically after registration.
data class MessageResponse(
    val message: String
)

// Represents the response structure for fetching a user’s profile details.
data class UserProfileResponse(
    val id: String,
    val email: String,
    val username: String?,
    val phone: String,
    val password: String?,
    val role: String,
    val isActive: Boolean,
    val nic: String,
    val fullName: String,
    val address: String,
    val createdAt: String,
    val lastLoginAt: String
)

// Represents the request body used when updating a user’s profile information.
data class UpdateProfileRequest(
    val email: String,
    val phone: String,
    val fullName: String,
    val address: String
)