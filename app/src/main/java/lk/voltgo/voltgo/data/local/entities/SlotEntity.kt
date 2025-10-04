package lk.voltgo.voltgo.data.local.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(
    tableName = "slot",
)

data class SlotEntity(
    @PrimaryKey
    @ColumnInfo(name = "slot_id")
    val slotId: String,

    @ColumnInfo(name = "start_time")
    val startTime: String,         // ISO8601 UTC recommended

    @ColumnInfo(name = "end_time")
    val endTime: String,           // ISO8601 UTC recommended

    @ColumnInfo(name = "reservation_date")
    val reservationDate: String

)

