/**
 * ------------------------------------------------------------
 * File: ReservationEntity.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * Room entity for reservations. Each reservation links to a user (owner),
 * a station, and a slot. Includes start/end times, status, and sync flags.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservation",
    indices = [
        Index(value = ["owner_id", "start_time"], name = "idx_res_owner_time"),
        Index(value = ["station_id"], name = "idx_res_station"),
        Index(value = ["slot_id"], name = "idx_res_slot")
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["owner_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey( // link to charging_station
            entity = ChargingStationEntity::class,
            parentColumns = ["id"],
            childColumns = ["station_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey( // link to slot
            entity = SlotEntity::class,
            parentColumns = ["slot_id"],
            childColumns = ["slot_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class ReservationEntity(
    // Primary key (can be a Mongo ObjectId string mirrored from backend)
    @PrimaryKey
    @ColumnInfo(name = "reservation_id")
    val reservationId: String,

    // Local link to the app user who owns the reservation
    @ColumnInfo(name = "owner_id")
    val ownerId: String, // FK -> user.user_id

    // (Optional but useful for backend sync) NIC used by backend Booking.OwnerNIC
    @ColumnInfo(name = "owner_nic")
    val ownerNic: String? = null,

    // Links to station & slot
    @ColumnInfo(name = "station_id")
    val stationId: String, // FK -> charging_station.station_id

    @ColumnInfo(name = "slot_id")
    val slotId: String,    // FK -> slot.slot_id

    // Times (store as ISO-8601 UTC strings or use converters to Instant)
    @ColumnInfo(name = "start_time")
    val startTime: String,

    @ColumnInfo(name = "end_time")
    val endTime: String,

    // Status: Pending, Confirmed, Cancelled, Completed
    // (Consider an enum + @TypeConverter for safety)
    @ColumnInfo(name = "status")
    val status: String = "Pending",

    // Optional fields to mirror backend timestamps (nullable)
    @ColumnInfo(name = "confirmed_at")
    val confirmedAt: String? = null,

    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null,

    @ColumnInfo(name = "cancelled_at")
    val cancelledAt: String? = null,

    // Sync flag with backend
    @ColumnInfo(name = "server_synced")
    val serverSynced: Boolean = false,

    // Audit fields
    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
