// ReservationRepository.kt
package lk.voltgo.voltgo.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lk.voltgo.voltgo.data.local.dao.ReservationDao
import lk.voltgo.voltgo.data.local.entities.ReservationEntity
import lk.voltgo.voltgo.data.mapper.toEntity
import lk.voltgo.voltgo.data.remote.api.ReservationApiService
import lk.voltgo.voltgo.data.remote.dto.NewReservationRequest
import lk.voltgo.voltgo.data.remote.dto.NewReservationResponse
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

    // ReservationRepository.kt (add single-item fetch)
    suspend fun getReservationById(id: String): Result<ReservationEntity> = withContext(Dispatchers.IO) {
        try {
            val resp = reservationApiService.getReservationById(id)
            if (resp.isSuccessful) {
                val dto = resp.body() ?: return@withContext Result.failure(IllegalStateException("Empty"))
                val entity = dto.toEntity()
                reservationDao.upsertOne(entity)
                Result.success(entity)
            } else {
                // Fallback to cache
                val cached = reservationDao.getById(id)
                if (cached != null) Result.success(cached)
                else Result.failure(IllegalStateException("Not found"))
            }
        } catch (e: Exception) {
            val cached = reservationDao.getById(id)
            if (cached != null) Result.success(cached) else Result.failure(e)
        }
    }

    suspend fun createReservation(request: NewReservationRequest): Result<NewReservationResponse> =
        withContext(Dispatchers.IO) {
            try {
                val resp = reservationApiService.createReservation(request)
                if (resp.isSuccessful) {
                    Result.success(resp.body() ?: NewReservationResponse("OK", bookingId = ""))
                } else {
                    Result.failure(IllegalStateException("Create failed: HTTP ${resp.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
