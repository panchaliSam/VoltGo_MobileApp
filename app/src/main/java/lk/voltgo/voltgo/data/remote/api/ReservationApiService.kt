package lk.voltgo.voltgo.data.remote.api

import lk.voltgo.voltgo.data.remote.dto.ReservationResponse
import retrofit2.Response
import retrofit2.http.GET

interface ReservationApiService {

    @GET("/api/Booking/mobile")
    suspend fun getMyReservations(): Response<List<ReservationResponse>>

}