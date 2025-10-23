package lk.voltgo.voltgo.data.remote.dto

data class MessageResponse(val message: String)

data class ScanReservationQrRequest(
    val qrCode: String // base64 of the QR payload
)

data class ScanReservationQrResponse(
    val message: String,
    val booking: BookingDto?,
    val bookingData: BookingDataDto?
)

data class BookingDto(
    val id: String,
    val ownerNIC: String,
    val stationId: String,
    val slotId: String?,
    val reservationDate: String,
    val createdAt: String,
    val status: String,
    val qrCode: String?,
    val notes: String?,
    val confirmedAt: String?,
    val completedAt: String?,
    val cancelledAt: String?,
    val canBeModified: Boolean,
    val canBeCancelled: Boolean,
    val isWithin7Days: Boolean
)

data class BookingDataDto(
    val bookingId: String,
    val ownerNIC: String,
    val stationId: String,
    val slotId: String?,
    val reservationDate: String,
    val generatedAt: String
)

data class CompleteMessageResponse(
    val message: String
)