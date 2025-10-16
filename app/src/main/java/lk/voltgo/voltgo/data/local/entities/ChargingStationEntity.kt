/**
 * ------------------------------------------------------------
 * File: ChargingStationEntity.kt
 * Author: Ishini Aponso
 * Date: 2025-10-10
 *
 * Description:
 * This data class represents the entity for a charging station in the VoltGo app.
 * It defines the schema for the `charging_station` table in the Room database,
 * including columns for station details such as name, type, location, and availability.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "charging_station",

)

// Entity class representing a charging station record in the local database.
data class ChargingStationEntity(
    // Unique identifier for each charging station.
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    // Basic information about the charging station (name, type, and location).
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "location")
    val location: String,

    // Geographical coordinates of the charging station.
    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    // Number of available charging slots at the station.
    @ColumnInfo(name = "available_slots")
    val availableSlots: Int,

    // Indicates whether the charging station is currently active.
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
)



