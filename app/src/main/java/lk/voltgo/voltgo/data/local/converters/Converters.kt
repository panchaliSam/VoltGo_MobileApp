package lk.voltgo.voltgo.data.local.converters


import androidx.room.TypeConverter
import lk.voltgo.voltgo.data.remote.types.StatusType

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromEnum(value: StatusType?): String? = value?.name // stores "Confirmed", "Pending", etc.

    @TypeConverter
    @JvmStatic
    fun toEnum(value: String?): StatusType =
        when (value?.trim()?.lowercase()) {
            "confirmed" -> StatusType.Confirmed
            "pending"   -> StatusType.Pending
            "completed" -> StatusType.Completed
            "cancelled" -> StatusType.Cancelled
            else        -> StatusType.Pending // last-ditch fallback
        }
}