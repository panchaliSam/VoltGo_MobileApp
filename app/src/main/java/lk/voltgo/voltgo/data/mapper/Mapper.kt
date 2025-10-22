package lk.voltgo.voltgo.data.mapper

import lk.voltgo.voltgo.data.local.entities.ReservationEntity
import lk.voltgo.voltgo.data.remote.dto.ReservationDetailUi
import lk.voltgo.voltgo.data.remote.dto.ReservationResponse
import lk.voltgo.voltgo.data.remote.types.StatusType
import lk.voltgo.voltgo.ui.screens.main.ReservationStatus
import lk.voltgo.voltgo.ui.screens.main.ReservationUi

private fun String.toStatusTypeStrict(): StatusType = when (trim().lowercase()) {
    "confirmed" -> StatusType.Confirmed
    "pending"   -> StatusType.Pending
    "completed" -> StatusType.Completed
    "cancelled" -> StatusType.Cancelled
    else -> StatusType.Pending  // only used if server sends truly unexpected value
}

/** API DTO -> Room entity */
fun ReservationResponse.toEntity(fallbackOwnerNic: String? = null): ReservationEntity =
    ReservationEntity(
        reservationId = id,
        ownerNic = if (ownerNIC.isNullOrBlank()) (fallbackOwnerNic ?: "") else ownerNIC,
        stationId = stationId,
        slotId = slotId,
        reservationDate = reservationDate,
        createdAt = createdAt,
        status = status.toStatusTypeStrict(),
        qrCode = qrCode,
        notes = notes,
        canBeCancelled = canBeCancelled,
        confirmedAt = confirmedAt,
        completedAt = completedAt,
        cancelledAt = cancelledAt,
        canBeModified = canBeModified,
        isWithin7Days = isWithin7Days,
        serverSynced = true,
        updatedAt = ""
    )

/** Entity -> UI model (status mapped 1:1) */
private fun StatusType.toUiStatus(): ReservationStatus = when (this) {
    StatusType.Confirmed -> ReservationStatus.Confirmed
    StatusType.Pending   -> ReservationStatus.Pending
    StatusType.Completed -> ReservationStatus.Completed
    StatusType.Cancelled -> ReservationStatus.Cancelled
}

/** Provide a nice station label; fall back to ID if you have nothing else */
private fun ReservationEntity.stationLabel(): String =
    notes?.takeIf { it.isNotBlank() } ?: stationId

/** Room entity -> UI model */
fun ReservationEntity.toUi(): ReservationUi =
    ReservationUi(
        id        = reservationId,
        station   = stationLabel(),
        date      = reservationDate.substring(0, 10), // quick ISO cut; replace with your formatter
        timeRange = "",                                // fill if you compute from slot
        status    = status.toUiStatus(),
        qrCode    = qrCode
    )

fun ReservationEntity.toDetailUi(
    stationNameResolver: (String) -> String = { it } // plug a lookup if you have Station table
): ReservationDetailUi =
    ReservationDetailUi(
        id = reservationId,
        stationName = stationNameResolver(stationId),
        stationId = stationId,
        slotId = slotId,
        date = reservationDate.substring(0, 10),            // assume you already compute pretty date in toUi()/entity
        timeRange = "",  // same as above
        status = when (status.name.lowercase()) {
            "confirmed" -> ReservationStatus.Confirmed
            "pending"   -> ReservationStatus.Pending
            "completed" -> ReservationStatus.Completed
            "cancelled" -> ReservationStatus.Cancelled
            else        -> ReservationStatus.Pending
        },
        qrCode = qrCode,
        notes = notes,
        createdAt = createdAt,
        confirmedAt = confirmedAt,
        completedAt = completedAt,
        cancelledAt = cancelledAt,
        canBeModified = canBeModified,
        canBeCancelled = canBeCancelled
    )