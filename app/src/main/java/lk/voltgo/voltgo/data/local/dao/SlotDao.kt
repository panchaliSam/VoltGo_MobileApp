/**
 * ------------------------------------------------------------
 * File: SlotDao.kt
 * Author: Ishini Aponso
 * Date: 2025-10-10
 *
 * Description:
 * This interface defines the Data Access Object (DAO) for managing charging slot data
 * in the VoltGo app. It provides methods to insert, update, delete, and retrieve slot
 * information from the Room database, supporting both single and reactive data operations.
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
import lk.voltgo.voltgo.data.local.entities.SlotEntity

@Dao
interface SlotDao {
    // Observes all slots for a specific charging station as a Flow for real-time updates.
    @Query("SELECT * FROM slot WHERE slot_id = :stationId")
    fun observeSlotsForStation(stationId: String): Flow<List<SlotEntity>>

    // Retrieves a single slot entity by its unique slot ID.
    @Query("SELECT * FROM slot WHERE slot_id = :slotId LIMIT 1")
    suspend fun getSlotById(slotId: String): SlotEntity?

    // Inserts or replaces a single slot record in the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: SlotEntity)

    // Inserts or replaces multiple slot records in the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(slots: List<SlotEntity>)

    // Updates details of an existing slot in the database.
    @Update
    suspend fun updateSlot(slot: SlotEntity)

    // Deletes a specific slot record from the database.
    @Delete
    suspend fun deleteSlot(slot: SlotEntity)

    // Deletes all slot records from the database.
    @Query("DELETE FROM slot")
    suspend fun deleteAll()
}