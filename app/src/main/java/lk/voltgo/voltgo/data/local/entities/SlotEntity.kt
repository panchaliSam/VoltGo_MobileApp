package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "slot",
    indices = [
        Index(value = ["reservation_date"]),
        Index(value = ["start_time"])
    ]
)
data class SlotEntity(
    @PrimaryKey
    @ColumnInfo(name = "slot_id")
    val slotId: String,

    @ColumnInfo(name = "start_time")
    val startTime: String,         // ISO8601 UTC

    @ColumnInfo(name = "end_time")
    val endTime: String,           // ISO8601 UTC

    @ColumnInfo(name = "reservation_date")
    val reservationDate: String    // yyyy-MM-dd
)