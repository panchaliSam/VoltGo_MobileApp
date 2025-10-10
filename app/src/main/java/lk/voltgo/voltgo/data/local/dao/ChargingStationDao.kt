/**
 * ------------------------------------------------------------
 * File: ChargingStationDao.kt
 * Author: Ishini Aponso
 * Date: 2025-10-10
 *
 * Description:
 * This interface defines the Data Access Object (DAO) for managing charging station data
 * within the VoltGo app. It provides CRUD operations to insert, update, delete, and query
 * charging stations from the Room database, supporting both individual and reactive data
 * access for features like dashboards and Google Maps integration.
 * ------------------------------------------------------------
 */
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
    // Observes all active charging stations as a reactive Flow for live updates.
    @Query("SELECT * FROM charging_station WHERE is_active = 1")
    fun observeActiveStations(): Flow<List<ChargingStationEntity>>

    // Retrieves a specific charging station by its unique ID.
    @Query("SELECT * FROM charging_station WHERE id = :id LIMIT 1")
    suspend fun getStationById(id: String): ChargingStationEntity?

    // Searches charging stations based on partial name or location matches.
    @Query("SELECT * FROM charging_station WHERE name LIKE :query OR location LIKE :query")
    suspend fun searchStations(query: String): List<ChargingStationEntity>

    // Inserts or updates a single charging station record in the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: ChargingStationEntity)

    // Inserts or updates multiple charging stations in a batch operation.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<ChargingStationEntity>)

    // Updates the details of an existing charging station.
    @Update
    suspend fun updateStation(station: ChargingStationEntity)

    // Deletes a specific charging station record from the database.
    @Delete
    suspend fun deleteStation(station: ChargingStationEntity)

    // Inserts demo charging stations used for pre-populating or testing.
    @Insert
    fun insertAll(demoStations: List<ChargingStationEntity>)
}

