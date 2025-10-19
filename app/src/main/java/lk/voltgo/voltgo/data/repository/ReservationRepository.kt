package lk.voltgo.voltgo.data.repository

import lk.voltgo.voltgo.data.local.dao.ReservationDao
import lk.voltgo.voltgo.data.remote.api.ReservationApiService
import lk.voltgo.voltgo.data.remote.dto.ReservationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
    private val reservationApiService: ReservationApiService,
    private val reservationDao: ReservationDao
) {

    suspend fun getMyReservations(): Result<List<ReservationResponse>> = try {
        val response = reservationApiService.getMyReservations()
        if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Failed to fetch reservations: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}