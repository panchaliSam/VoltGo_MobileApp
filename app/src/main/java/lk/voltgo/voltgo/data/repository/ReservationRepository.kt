// ReservationRepository.kt
package lk.voltgo.voltgo.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lk.voltgo.voltgo.data.local.dao.ReservationDao
import lk.voltgo.voltgo.data.local.entities.ReservationEntity
import lk.voltgo.voltgo.data.mapper.toEntity
import lk.voltgo.voltgo.data.remote.api.ReservationApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
    private val reservationApiService: ReservationApiService,
    private val reservationDao: ReservationDao
) {

    /** Observe cached reservations for a user (UI uses this for live updates). */
    fun observeMyReservations(ownerNic: String) =
        reservationDao.observeByOwnerNic(ownerNic)

    /** Page locally (no network). */
    suspend fun pageMyReservations(ownerNic: String, limit: Int, offset: Int) =
        reservationDao.pageByOwnerNic(ownerNic, limit, offset)

    /**
     * Fetch from API and write into Room.
     * Returns the freshly cached entities (or cached data on failure).
     */
    suspend fun syncMyReservations(ownerNic: String): Result<List<ReservationEntity>> =
        withContext(Dispatchers.IO) {
            try {
                val resp = reservationApiService.getMyReservations()
                if (resp.isSuccessful) {
                    val dtos = resp.body().orEmpty()
                    val entities = dtos.map { it.toEntity(fallbackOwnerNic = ownerNic) }

                    // Replace all for this user to keep cache clean & simple
                    reservationDao.replaceAllForOwnerNic(ownerNic, entities)

                    Result.success(entities)
                } else {
                    // Fallback to cache
                    val cached = reservationDao.getAllByOwnerNic(ownerNic)
                    Result.success(cached)
                }
            } catch (e: Exception) {
                // Network error → fallback to cache
                val cached = reservationDao.getAllByOwnerNic(ownerNic)
                Result.success(cached)
            }
        }
}
