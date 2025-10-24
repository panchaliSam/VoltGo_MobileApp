/**
 * ------------------------------------------------------------
 * File: ReservationDao.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-24
 * Description: Updated for physical_slot_number and time-range overlap.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.ReservationEntity
import lk.voltgo.voltgo.data.remote.types.StatusType

@Dao
interface ReservationDao {

    @Query("SELECT COUNT(*) FROM reservation")
    suspend fun countReservations(): Int

    @Query("SELECT * FROM reservation WHERE reservation_id = :id LIMIT 1")
    suspend fun getById(id: String): ReservationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOne(entity: ReservationEntity)

    @Query("""
        SELECT * FROM reservation 
        WHERE owner_nic = :ownerNic 
        ORDER BY reservation_date DESC, start_time DESC
    """)
    fun observeByOwnerNic(ownerNic: String): Flow<List<ReservationEntity>>

    @Query("""
        SELECT * FROM reservation 
        WHERE owner_nic = :ownerNic 
        ORDER BY reservation_date DESC, start_time DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun pageByOwnerNic(ownerNic: String, limit: Int, offset: Int): List<ReservationEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reservation: ReservationEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(reservations: List<ReservationEntity>)

    @Update
    suspend fun update(reservation: ReservationEntity)

    @Query("""
        UPDATE reservation 
        SET status = :status, updated_at = :updatedAt 
        WHERE reservation_id = :reservationId
    """)
    suspend fun updateStatus(reservationId: String, status: StatusType, updatedAt: String)

    @Query("""
        UPDATE reservation 
        SET server_synced = 1, updated_at = :updatedAt 
        WHERE reservation_id = :reservationId
    """)
    suspend fun markSynced(reservationId: String, updatedAt: String)

    @Delete
    suspend fun delete(reservation: ReservationEntity)

    @Query("DELETE FROM reservation")
    suspend fun deleteAll()

    // Time-range conflict detection on same station, same physical slot, same date:
    // overlap if NOT( existing.end <= newStart OR existing.start >= newEnd )
    @Query("""
        SELECT * FROM reservation
        WHERE station_id = :stationId
          AND physical_slot_number = :physicalSlotNumber
          AND reservation_date = :reservationDate
          AND NOT (end_time <= :newStartTime OR start_time >= :newEndTime)
    """)
    suspend fun findOverlaps(
        stationId: String,
        physicalSlotNumber: Int,
        reservationDate: String,
        newStartTime: String,
        newEndTime: String
    ): List<ReservationEntity>

    @Query("DELETE FROM reservation WHERE owner_nic = :ownerNic")
    suspend fun deleteAllForOwnerNic(ownerNic: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(reservations: List<ReservationEntity>)

    @Query("DELETE FROM reservation WHERE reservation_id = :reservationId")
    suspend fun deleteById(reservationId: String)

    @Transaction
    suspend fun replaceAllForOwnerNic(ownerNic: String, reservations: List<ReservationEntity>) {
        deleteAllForOwnerNic(ownerNic)
        if (reservations.isNotEmpty()) upsertAll(reservations)
    }

    @Query("""
        SELECT * FROM reservation 
        WHERE owner_nic = :ownerNic 
        ORDER BY reservation_date DESC, start_time DESC
    """)
    suspend fun getAllByOwnerNic(ownerNic: String): List<ReservationEntity>
}