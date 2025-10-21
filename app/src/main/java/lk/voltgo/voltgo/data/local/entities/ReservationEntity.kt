package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import lk.voltgo.voltgo.data.local.converters.Converters
import lk.voltgo.voltgo.data.remote.types.StatusType

@TypeConverters(Converters::class)
@Entity(
    tableName = "reservation",
    indices = [
        Index(value = ["owner_nic", "reservation_date"], name = "idx_res_ownerNic_date"),
        Index(value = ["station_id"], name = "idx_res_station"),
        Index(value = ["slot_id"], name = "idx_res_slot")
    ]
)
data class ReservationEntity(
    @PrimaryKey
    @ColumnInfo(name = "reservation_id")
    val reservationId: String,

    // NIC kept as a unique reference, not a foreign key
    @ColumnInfo(name = "owner_nic")
    val ownerNic: String,

    @ColumnInfo(name = "station_id")
    val stationId: String,

    @ColumnInfo(name = "slot_id")
    val slotId: String,

    @ColumnInfo(name = "reservation_date")
    val reservationDate: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "status")
    val status: StatusType,

    @ColumnInfo(name = "qr_code")
    val qrCode: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "confirmed_at")
    val confirmedAt: String? = null,

    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null,

    @ColumnInfo(name = "cancelled_at")
    val cancelledAt: String? = null,

    @ColumnInfo(name = "can_be_modified")
    val canBeModified: Boolean = false,

    @ColumnInfo(name = "can_be_cancelled")
    val canBeCancelled: Boolean = false,

    @ColumnInfo(name = "is_within_7_days")
    val isWithin7Days: Boolean = false,

    @ColumnInfo(name = "server_synced")
    val serverSynced: Boolean = false,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)