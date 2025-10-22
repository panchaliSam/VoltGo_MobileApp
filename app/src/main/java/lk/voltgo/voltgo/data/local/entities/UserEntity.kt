package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import lk.voltgo.voltgo.data.remote.types.RoleType

@Entity(
        tableName = "user",
        indices = [
                Index(value = ["email"], unique = true),
                Index(value = ["phone"], unique = true),
                Index(value = ["nic"], unique = true)
        ]
)
data class UserEntity(
        @PrimaryKey
        @ColumnInfo(name = "user_id")
        val userId: String,

        @ColumnInfo(name = "email")
        val email: String,

        @ColumnInfo(name = "phone")
        val phone: String,

        @ColumnInfo(name = "role", defaultValue = "EV_OWNER")
        val role: RoleType = RoleType.EV_OWNER,

        @ColumnInfo(name = "is_active", defaultValue = "1")
        val isActive: Boolean = true,

        @ColumnInfo(name = "nic")
        val nic: String? = null,

        @ColumnInfo(name = "full_name")
        val fullName: String? = null,

        @ColumnInfo(name = "address")
        val address: String? = null,

        @ColumnInfo(name = "created_at")
        val createdAt: String,

        @ColumnInfo(name = "last_login_at")
        val lastLoginAt: String? = null,
)