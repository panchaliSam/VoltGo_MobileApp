package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charging_station")
data class ChargingStationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    // backend enum (AC/DC); keep as String locally
    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "location")
    val location: String,

    // nullable to mirror backend
    @ColumnInfo(name = "latitude")
    val latitude: Double?,

    @ColumnInfo(name = "longitude")
    val longitude: Double?,

    @ColumnInfo(name = "available_slots")
    val availableSlots: Int,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
)