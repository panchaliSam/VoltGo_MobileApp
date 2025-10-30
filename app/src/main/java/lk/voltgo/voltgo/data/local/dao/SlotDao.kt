/**
 * ------------------------------------------------------------
 * File: SlotDao.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-24
 * Description: DAO for per-connector physical slots of a station.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.StationPhysicalSlotEntity

@Dao
interface SlotDao {

    @Query("SELECT * FROM station_physical_slot WHERE station_id = :stationId ORDER BY number ASC")
    suspend fun getByStation(stationId: String): List<StationPhysicalSlotEntity>

    @Query("SELECT * FROM station_physical_slot WHERE station_id = :stationId ORDER BY number ASC")
    fun observeByStation(stationId: String): Flow<List<StationPhysicalSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(slots: List<StationPhysicalSlotEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(slot: StationPhysicalSlotEntity)

    @Query("DELETE FROM station_physical_slot WHERE station_id = :stationId")
    suspend fun deleteForStation(stationId: String)

    @Query("DELETE FROM station_physical_slot WHERE station_id = :stationId AND number NOT IN (:numbers)")
    suspend fun deleteMissingForStation(stationId: String, numbers: List<Int>)
}