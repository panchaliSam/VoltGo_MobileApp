package lk.voltgo.voltgo.data.remote.api

import lk.voltgo.voltgo.data.remote.dto.ReservationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReservationApiService {

    @GET("/api/Booking/mobile")
    suspend fun getMyReservations(): Response<List<ReservationResponse>>

    @GET("/api/Booking/{id}")
    suspend fun getReservationById(@Path("id") id: String): Response<ReservationResponse>

}