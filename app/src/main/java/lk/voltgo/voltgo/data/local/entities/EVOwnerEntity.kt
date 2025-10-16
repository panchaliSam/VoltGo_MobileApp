/**
 * ------------------------------------------------------------
 * File: EVOwnerEntity.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This file defines the Room entity representing an EV owner in the VoltGo app.
 * Each EV owner is associated with a user account and contains additional profile
 * details such as address and vehicle information.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Room Entity representing an EV owner with a foreign key relationship to the User entity.
@Entity(
    tableName = "ev_owner",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["user_id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class EVOwnerEntity(
    // Primary key referencing the associated user's ID.
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    // Address of the EV owner (nullable field to handle optional address information).
    @ColumnInfo(name = "address")
    val address: String?

    // TODO: Add vehicle-related fields such as vehicle model, license plate, and battery capacity.
    //Add Vehicle
)