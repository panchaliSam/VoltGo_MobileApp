/**
 * ------------------------------------------------------------
 * File: ReservationEntity.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This file defines the Room entity representing a reservation record in the VoltGo app.
 * Each reservation is linked to a user (owner), station, and slot, and includes start/end times,
 * reservation status, and synchronization tracking with the backend server.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Room Entity representing a reservation record with foreign keys to User, Station, and Slot entities.
@Entity(
    tableName = "reservation",
    indices = [
        Index(value = ["owner_id", "start_time"], name = "idx_reservation_owner_time"),
        Index(value = ["station_id"]),
        Index(value = ["slot_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["owner_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
        // If you later create StationEntity / SlotEntity, add FKs similarly.
    ]
)
data class ReservationEntity(
    // Primary key uniquely identifying each reservation record.
    @PrimaryKey
    @ColumnInfo(name = "reservation_id")
    val reservationId: String,

    // Foreign key referencing the user who owns the reservation.
    @ColumnInfo(name = "owner_id")
    val ownerId: String,           // FK -> user.user_id

    // Foreign key referencing the station where the reservation is made.
    @ColumnInfo(name = "station_id")
    val stationId: String,

    // Foreign key referencing the slot allocated for the reservation.
    @ColumnInfo(name = "slot_id")
    val slotId: String,

    // Start time of the reservation (ISO8601 UTC recommended).
    @ColumnInfo(name = "start_time")
    val startTime: String,         // ISO8601 UTC recommended

    // End time of the reservation (ISO8601 UTC recommended).
    @ColumnInfo(name = "end_time")
    val endTime: String,           // ISO8601 UTC recommended

    // Current status of the reservation (PENDING, APPROVED, CANCELLED, COMPLETED).
    @ColumnInfo(name = "status")
    val status: String,            // PENDING, APPROVED, CANCELLED, COMPLETED

    // Indicates whether the reservation has been synced with the backend server.
    @ColumnInfo(name = "server_synced")
    val serverSynced: Boolean = false,

    // Timestamp when the reservation was created.
    @ColumnInfo(name = "created_at")
    val createdAt: String,

    // Timestamp when the reservation was last updated.
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
