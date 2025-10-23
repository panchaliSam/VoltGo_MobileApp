package lk.voltgo.voltgo.data.remote.api

import lk.voltgo.voltgo.data.remote.dto.NewReservationRequest
import lk.voltgo.voltgo.data.remote.dto.NewReservationResponse
import lk.voltgo.voltgo.data.remote.dto.ReservationMessageResponse
import lk.voltgo.voltgo.data.remote.dto.ReservationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationApiService {

    @GET("/api/Booking/mobile")
    suspend fun getMyReservations(): Response<List<ReservationResponse>>

    @GET("/api/Booking/{id}")
    suspend fun getReservationById(@Path("id") id: String): Response<ReservationResponse>

    @POST("/api/Booking/mobile/create")
    suspend fun createReservation(@Body request: NewReservationRequest): Response<NewReservationResponse>

    @PATCH("/api/Booking/{id}/cancel")
    suspend fun cancelReservation(@Path("id") id: String): Response<ReservationMessageResponse>

}