/**
 * ------------------------------------------------------------
 * File: ReservationDao.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This interface defines the Data Access Object (DAO) for managing reservation-related
 * operations in the VoltGo app. It includes methods for retrieving, inserting, updating,
 * and deleting reservation data, as well as handling synchronization and conflict detection
 * logic for EV charging reservations.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.ReservationEntity
import lk.voltgo.voltgo.data.remote.types.StatusType

@Dao
interface ReservationDao {

    // Returns the total number of reservations in the database.
    @Query("SELECT COUNT(*) FROM reservation")
    suspend fun countReservations(): Int

    // Retrieves a reservation by its unique ID.
    @Query("SELECT * FROM reservation WHERE reservation_id = :reservationId")
    suspend fun getById(reservationId: String): ReservationEntity?

    // Observes all reservations of a specific owner NIC as a reactive Flow, ordered by date (descending).
    @Query("""
        SELECT * FROM reservation 
        WHERE owner_nic = :ownerNic 
        ORDER BY reservation_date DESC
    """)
    fun observeByOwnerNic(ownerNic: String): Flow<List<ReservationEntity>>

    // Retrieves paginated reservations for a specific owner NIC, supporting limit and offset.
    @Query("""
        SELECT * FROM reservation 
        WHERE owner_nic = :ownerNic 
        ORDER BY reservation_date DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun pageByOwnerNic(ownerNic: String, limit: Int, offset: Int): List<ReservationEntity>

    // Inserts a single reservation record into the database; aborts if conflict occurs.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reservation: ReservationEntity)

    // Inserts multiple reservation records into the database; aborts on conflict.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(reservations: List<ReservationEntity>)

    // Updates an existing reservation in the database.
    @Update
    suspend fun update(reservation: ReservationEntity)

    // Updates the status of a reservation with the given ID.
    @Query("""
  UPDATE reservation 
  SET status = :status, updated_at = :updatedAt 
  WHERE reservation_id = :reservationId
""")
    suspend fun updateStatus(reservationId: String, status: StatusType, updatedAt: String)

    // Marks a reservation as synced with the server and updates its timestamp.
    @Query("""
        UPDATE reservation 
        SET server_synced = 1, updated_at = :updatedAt 
        WHERE reservation_id = :reservationId
    """)
    suspend fun markSynced(reservationId: String, updatedAt: String)

    // Deletes a specific reservation record from the database.
    @Delete
    suspend fun delete(reservation: ReservationEntity)

    // Deletes all reservation records from the database.
    @Query("DELETE FROM reservation")
    suspend fun deleteAll()

    // Detects conflicting reservations for a given station and slot on the same reservation date.
    @Query("""
        SELECT * FROM reservation
        WHERE station_id = :stationId
          AND slot_id = :slotId
          AND reservation_date = :reservationDate
    """)
    suspend fun findConflictsExact(
        stationId: String,
        slotId: String,
        reservationDate: String
    ): List<ReservationEntity>

    // Deletes all reservations for a given NIC.
    @Query("DELETE FROM reservation WHERE owner_nic = :ownerNic")
    suspend fun deleteAllForOwnerNic(ownerNic: String)

    // Upsert list (REPLACE keeps the latest from server)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(reservations: List<ReservationEntity>)

    @Query("DELETE FROM reservation WHERE reservation_id = :reservationId")
    suspend fun deleteById(reservationId: String)

    @Transaction
    suspend fun replaceAllForOwnerNic(ownerNic: String, reservations: List<ReservationEntity>) {
        deleteAllForOwnerNic(ownerNic)
        if (reservations.isNotEmpty()) upsertAll(reservations)
    }

    @Query("SELECT * FROM reservation WHERE owner_nic = :ownerNic ORDER BY reservation_date DESC")
    suspend fun getAllByOwnerNic(ownerNic: String): List<ReservationEntity>
}