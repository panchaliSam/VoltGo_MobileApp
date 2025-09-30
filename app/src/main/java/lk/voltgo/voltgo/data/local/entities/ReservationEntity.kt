package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    @PrimaryKey
    @ColumnInfo(name = "reservation_id")
    val reservationId: String,

    @ColumnInfo(name = "owner_id")
    val ownerId: String,           // FK -> user.user_id

    @ColumnInfo(name = "station_id")
    val stationId: String,

    @ColumnInfo(name = "slot_id")
    val slotId: String,

    @ColumnInfo(name = "start_time")
    val startTime: String,         // ISO8601 UTC recommended

    @ColumnInfo(name = "end_time")
    val endTime: String,           // ISO8601 UTC recommended

    @ColumnInfo(name = "status")
    val status: String,            // PENDING, APPROVED, CANCELLED, COMPLETED

    @ColumnInfo(name = "server_synced")
    val serverSynced: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
