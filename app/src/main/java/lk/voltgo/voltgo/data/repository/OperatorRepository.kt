package lk.voltgo.voltgo.data.repository

import lk.voltgo.voltgo.data.remote.api.OperatorApiService
import lk.voltgo.voltgo.data.remote.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OperatorRepository @Inject constructor(
    private val api: OperatorApiService
) {
    suspend fun scanQr(base64Qr: String): Result<ScanReservationQrResponse> = try {
        val resp = api.scanReservationQr(ScanReservationQrRequest(qrCode = base64Qr))
        if (resp.isSuccessful) Result.success(resp.body()!!)
        else Result.failure(IllegalStateException("Verify failed: HTTP ${resp.code()}"))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun complete(bookingId: String): Result<CompleteMessageResponse> = try {
        val resp = api.completeReservation(bookingId)
        if (resp.isSuccessful) {
            val body = resp.body() ?: return Result.failure(IllegalStateException("Empty body"))
            Result.success(body)
        } else {
            Result.failure(IllegalStateException("Complete failed: HTTP ${resp.code()}"))
        }
    } catch (t: Throwable) {
        Result.failure(t)
    }
}