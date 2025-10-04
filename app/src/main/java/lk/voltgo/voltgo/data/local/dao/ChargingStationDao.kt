package lk.voltgo.voltgo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.ChargingStationEntity

@Dao
interface ChargingStationDao {
    // Get all active stations (for Dashboard / Google Maps)
    @Query("SELECT * FROM charging_station WHERE is_active = 1")
    fun observeActiveStations(): Flow<List<ChargingStationEntity>>

    // Get a single station by ID
    @Query("SELECT * FROM charging_station WHERE id = :id LIMIT 1")
    suspend fun getStationById(id: Int): ChargingStationEntity?

    // Insert a station
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: ChargingStationEntity)

    // Insert multiple stations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<ChargingStationEntity>)

    // Update station
    @Update
    suspend fun updateStation(station: ChargingStationEntity)

    // Delete station
    @Delete
    suspend fun deleteStation(station: ChargingStationEntity)

    @Insert
    fun insertAll(demoStations: kotlin.collections.List<lk.voltgo.voltgo.data.local.entities.ChargingStationEntity>)
}

