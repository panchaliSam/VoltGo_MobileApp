package lk.voltgo.voltgo.data.remote.api

import lk.voltgo.voltgo.data.remote.dto.CompleteMessageResponse
import lk.voltgo.voltgo.data.remote.dto.ScanReservationQrRequest
import lk.voltgo.voltgo.data.remote.dto.ScanReservationQrResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OperatorApiService {

    @PATCH("/api/Booking/{id}/complete")
    suspend fun completeReservation(@Path("id") id: String): Response<CompleteMessageResponse>

    @POST("/api/Booking/mobile/scan-qr")
    suspend fun scanReservationQr(@Body request: ScanReservationQrRequest): Response<ScanReservationQrResponse>

}