package lk.voltgo.voltgo.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index
import androidx.room.PrimaryKey;

@Entity(
        tableName = "user",
)
data class UserEntity(
        @PrimaryKey
        @ColumnInfo(name = "user_id")
        val userId: String,

        @ColumnInfo(name = "display_name")
        val displayName: String,

        @ColumnInfo(name = "phone")
        val phone: String,

        @ColumnInfo(name = "email")
        val email: String?,

        @ColumnInfo(name = "role")
        val role: String, // BACKOFFICE, OPERATOR, EV_OWNER

        @ColumnInfo(name = "is_active")
        val isActive: Boolean = true,

        @ColumnInfo(name = "created_at")
        val createdAt: String,

        @ColumnInfo(name = "updated_at")
        val updatedAt: String
)

