package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.ReservationEntity

@Dao
interface ReservationDao {

    @Query("SELECT COUNT(*) FROM reservation")
    suspend fun countReservations(): Int

    @Query("SELECT * FROM reservation WHERE reservation_id = :reservationId")
    suspend fun getById(reservationId: String): ReservationEntity?

    @Query("SELECT * FROM reservation WHERE owner_id = :ownerId ORDER BY start_time DESC")
    fun observeByOwner(ownerId: String): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservation WHERE owner_id = :ownerId ORDER BY start_time DESC LIMIT :limit OFFSET :offset")
    suspend fun pageByOwner(ownerId: String, limit: Int, offset: Int): List<ReservationEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reservation: ReservationEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(reservations: List<ReservationEntity>)

    @Update
    suspend fun update(reservation: ReservationEntity)

    @Query("UPDATE reservation SET status = :status, updated_at = :updatedAt WHERE reservation_id = :reservationId")
    suspend fun updateStatus(reservationId: String, status: String, updatedAt: String)

    @Query("UPDATE reservation SET server_synced = 1, updated_at = :updatedAt WHERE reservation_id = :reservationId")
    suspend fun markSynced(reservationId: String, updatedAt: String)

    @Delete
    suspend fun delete(reservation: ReservationEntity)

    @Query("DELETE FROM reservation")
    suspend fun deleteAll()

    // Optional: detect overlapping reservations for a station/slot window (basic, inclusive)
    @Query("""
        SELECT * FROM reservation
        WHERE station_id = :stationId
          AND slot_id = :slotId
          AND (
              (start_time < :endTime AND end_time > :startTime)
          )
    """)
    suspend fun findOverlaps(
        stationId: String,
        slotId: String,
        startTime: String,
        endTime: String
    ): List<ReservationEntity>
}
