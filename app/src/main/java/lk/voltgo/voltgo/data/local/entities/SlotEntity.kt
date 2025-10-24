/**
 * ------------------------------------------------------------
 * File: StationPhysicalSlotEntity.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-24
 * Description: Local table for per-connector states of a station.
 * Mirrors backend StationPhysicalSlot: number, isActive, label, connectorType, maxKw.
 * Composite PK: (station_id, number)
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "station_physical_slot",
    primaryKeys = ["station_id", "number"]
)
data class StationPhysicalSlotEntity(
    @ColumnInfo(name = "station_id")
    val stationId: String,

    // 1..n (unique per station)
    @ColumnInfo(name = "number")
    val number: Int,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "label")
    val label: String? = null,

    @ColumnInfo(name = "connector_type")
    val connectorType: String? = null, // e.g., "Type2", "CCS2", "CHAdeMO"

    @ColumnInfo(name = "max_kw")
    val maxKw: Double? = null
)