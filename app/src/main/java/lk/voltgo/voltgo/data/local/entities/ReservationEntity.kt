/**
 * ------------------------------------------------------------
 * File: ReservationEntity.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-24
 * Description: Mirrors backend Booking (no external SlotId).
 * - physicalSlotNumber (Int)
 * - reservationDate (UTC date component)
 * - startTime / endTime (UTC)
 * Derived flags are exposed as computed properties (not columns).
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.entities

import androidx.room.*
import lk.voltgo.voltgo.data.local.converters.Converters
import lk.voltgo.voltgo.data.remote.types.StatusType

@TypeConverters(Converters::class)
@Entity(
    tableName = "reservation",
    indices = [
        Index(value = ["owner_nic", "reservation_date"], name = "idx_res_ownerNic_date"),
        Index(value = ["station_id"], name = "idx_res_station"),
        Index(value = ["physical_slot_number"], name = "idx_res_slot_num"),
        Index(value = ["start_time"], name = "idx_res_start_time")
    ]
)
data class ReservationEntity(
    @PrimaryKey
    @ColumnInfo(name = "reservation_id")
    val reservationId: String,

    // Not FK locally; just the NIC for filtering and display
    @ColumnInfo(name = "owner_nic")
    val ownerNic: String,

    @ColumnInfo(name = "station_id")
    val stationId: String,

    // NEW: mirrors Booking.PhysicalSlotNumber
    @ColumnInfo(name = "physical_slot_number")
    val physicalSlotNumber: Int,

    // Store ISO-8601 date (YYYY-MM-DD) or keep as String; converters can help
    @ColumnInfo(name = "reservation_date")
    val reservationDate: String,

    // ISO-8601 UTC time instants (e.g., 2025-10-26T10:45:18.279Z)
    @ColumnInfo(name = "start_time")
    val startTime: String,

    @ColumnInfo(name = "end_time")
    val endTime: String,

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

    @ColumnInfo(name = "server_synced")
    val serverSynced: Boolean = false,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
) {
    // ---- Derived flags (NOT persisted) to match backend logic ----
    @Ignore
    val canBeModified: Boolean = false

    @Ignore
    val canBeCancelled: Boolean = false

    @Ignore
    val isWithin7Days: Boolean = false
}