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
    // Get all slots for a station
    @Query("SELECT * FROM slot WHERE slot_id = :stationId")
    fun observeSlotsForStation(stationId: String): Flow<List<SlotEntity>>

    // Get a single slot
    @Query("SELECT * FROM slot WHERE slot_id = :slotId LIMIT 1")
    suspend fun getSlotById(slotId: String): SlotEntity?

    // Insert one slot
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: SlotEntity)

    // Insert multiple slots
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(slots: List<SlotEntity>)

    // Update a slot
    @Update
    suspend fun updateSlot(slot: SlotEntity)

    // Delete a slot
    @Delete
    suspend fun deleteSlot(slot: SlotEntity)

    // Delete all slots
    @Query("DELETE FROM slot")
    suspend fun deleteAll()
}