package lk.voltgo.voltgo.domain.model

import lk.voltgo.voltgo.data.local.entities.UserEntity

/**
 * Domain model for a signed-in / persisted user.
 * Note: No password here—use a separate DTO for registration/login.
 */
data class User(
    val userId: String,
    val email: String,
    val phone: String,
    val role: String = "EVOwner",
    val nic: String? = null,
    val fullName: String? = null,  // maps from username (if you treat it as full name)
    val address: String? = null,
    val isActive: Boolean = true
)

// Domain <- Entity
fun UserEntity.toDomain(): User {
    return User(
        userId = userId,
        email = email,
        phone = phone,
        role = role,
        nic = nic,
        // Prefer the properly-cased fullName column; fallback to legacy `fullname`
        fullName = fullName ?: fullname,
        address = address,
        isActive = isActive
    )
}

// Entity <- Domain
fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        email = email,
        // keep legacy `fullname` empty; use the proper `fullName` column
        fullname = null,
        phone = phone,
        role = role,
        isActive = isActive,
        nic = nic,
        fullName = fullName,
        address = address,
        // backend doesn’t return it—store empty string or consider making this nullable/defaulted
        createdAt = "",
        lastLoginAt = null
    )
}
