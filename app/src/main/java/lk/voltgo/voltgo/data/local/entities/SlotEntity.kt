/**
 * ------------------------------------------------------------
 * File: SlotEntity.kt
 * Author: Ishini Aposo
 * Date: 2025-10-10
 *
 * Description:
 * This file defines the Room entity representing a charging slot in the VoltGo app.
 * Each slot includes a start time, end time, and reservation date,
 * used to manage EV charging schedules and bookings.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.data.local.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(
    tableName = "slot",
)

// Room Entity representing a charging slot record in the local database.
data class SlotEntity(
    // Primary key uniquely identifying each slot record.
    @PrimaryKey
    @ColumnInfo(name = "slot_id")
    val slotId: String,

    // Column for the slot's start time (ISO8601 UTC recommended).
    @ColumnInfo(name = "start_time")
    val startTime: String,         // ISO8601 UTC recommended

    // Column for the slot's end time (ISO8601 UTC recommended).
    @ColumnInfo(name = "end_time")
    val endTime: String,           // ISO8601 UTC recommended

    // Column for the reservation date associated with this slot.
    @ColumnInfo(name = "reservation_date")
    val reservationDate: String

)

