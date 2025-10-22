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

@Entity(tableName = "slot")
data class SlotEntity(
    @PrimaryKey
    @ColumnInfo(name = "slot_id")
    val slotId: String,

    @ColumnInfo(name = "start_time")
    val startTime: String, // ISO 8601 datetime string

    @ColumnInfo(name = "end_time")
    val endTime: String, // ISO 8601 datetime string

    @ColumnInfo(name = "reservation_date")
    val reservationDate: String, // ISO 8601 date string

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean,

    @ColumnInfo(name = "station_id")
    val stationId: String
)

