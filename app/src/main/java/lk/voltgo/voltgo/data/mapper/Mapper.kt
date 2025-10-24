package lk.voltgo.voltgo.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import lk.voltgo.voltgo.data.local.entities.ReservationEntity
import lk.voltgo.voltgo.data.remote.dto.ReservationDetailUi
import lk.voltgo.voltgo.data.remote.dto.ReservationResponse
import lk.voltgo.voltgo.data.remote.types.StatusType
import lk.voltgo.voltgo.ui.screens.main.ReservationStatus
import lk.voltgo.voltgo.ui.screens.main.ReservationUi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// ---------- helpers ----------

private fun String.toStatusTypeStrict(): StatusType = when (trim().lowercase()) {
    "confirmed" -> StatusType.Confirmed
    "pending"   -> StatusType.Pending
    "completed" -> StatusType.Completed
    "cancelled" -> StatusType.Cancelled
    else        -> StatusType.Pending  // safety fallback
}

/** best-effort parse of an ISO-8601 instant string; returns null if invalid */
@RequiresApi(Build.VERSION_CODES.O)
private fun String.toInstantOrNull(): Instant? = runCatching { Instant.parse(this) }.getOrNull()

/** HH:mm in device local time from ISO-8601 instant */
@RequiresApi(Build.VERSION_CODES.O)
private fun String.toLocalHmOrBlank(): String {
    val inst = toInstantOrNull() ?: return ""
    val zdt: ZonedDateTime = inst.atZone(ZoneId.systemDefault())
    return DateTimeFormatter.ofPattern("HH:mm").format(zdt)
}

/** yyyy-MM-dd from any ISO string; falls back to first 10 chars if needed */
@RequiresApi(Build.VERSION_CODES.O)
private fun String.toDateOnlyUnsafe(): String =
    runCatching {
        // If it's a pure date already, keep as-is; else parse instant and format local date
        if (this.length >= 10 && this[4] == '-' && this[7] == '-') this.substring(0, 10)
        else {
            val inst = Instant.parse(this)
            DateTimeFormatter.ISO_LOCAL_DATE.format(inst.atZone(ZoneId.systemDefault()).toLocalDate())
        }
    }.getOrElse { this.take(10) }

// ---------- status <-> ui ----------

/** Entity/DTO Status -> UI Status */
private fun StatusType.toUiStatus(): ReservationStatus = when (this) {
    StatusType.Confirmed -> ReservationStatus.Confirmed
    StatusType.Pending   -> ReservationStatus.Pending
    StatusType.Completed -> ReservationStatus.Completed
    StatusType.Cancelled -> ReservationStatus.Cancelled
}

// ---------- API DTO -> Room entity ----------

@RequiresApi(Build.VERSION_CODES.O)
fun ReservationResponse.toEntity(fallbackOwnerNic: String? = null): ReservationEntity =
    ReservationEntity(
        reservationId       = id,
        ownerNic            = if (ownerNIC.isNullOrBlank()) (fallbackOwnerNic ?: "") else ownerNIC,
        stationId           = stationId,
        physicalSlotNumber  = physicalSlotNumber,               // <— NEW (Int)
        reservationDate     = reservationDate.toDateOnlyUnsafe(),// store yyyy-MM-dd
        startTime           = startTime,                         // ISO-8601 UTC
        endTime             = endTime,                           // ISO-8601 UTC
        createdAt           = createdAt,
        status              = status.toStatusTypeStrict(),
        qrCode              = qrCode,
        notes               = notes,
        confirmedAt         = confirmedAt,
        completedAt         = completedAt,
        cancelledAt         = cancelledAt,
        serverSynced        = true,
        updatedAt           = ""
    )

// ---------- Room entity -> UI list item ----------

/** Provide a nice station label; fall back to ID if you have nothing else */
private fun ReservationEntity.stationLabel(): String =
    notes?.takeIf { it.isNotBlank() } ?: stationId

@RequiresApi(Build.VERSION_CODES.O)
fun ReservationEntity.toUi(): ReservationUi =
    ReservationUi(
        id        = reservationId,
        station   = stationLabel(),
        date      = reservationDate,                                      // already yyyy-MM-dd
        timeRange = "${startTime.toLocalHmOrBlank()}–${endTime.toLocalHmOrBlank()}",
        status    = status.toUiStatus(),
        qrCode    = qrCode
    )

// ---------- Derived flags (computed for UI; not stored) ----------

@RequiresApi(Build.VERSION_CODES.O)
private fun ReservationEntity.canModifyNow(): Boolean {
    val start = startTime.toInstantOrNull() ?: return false
    val threshold = Instant.now().plusSeconds(12 * 3600) // +12h
    return status == StatusType.Pending && start.isAfter(threshold)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun ReservationEntity.canCancelNow(): Boolean = canModifyNow()

@RequiresApi(Build.VERSION_CODES.O)
private fun ReservationEntity.isWithin7DaysNow(): Boolean {
    val start = startTime.toInstantOrNull() ?: return false
    val in7d = Instant.now().plusSeconds(7 * 24 * 3600)
    return !start.isAfter(in7d)
}

// ---------- Room entity -> UI detail ----------

@RequiresApi(Build.VERSION_CODES.O)
fun ReservationEntity.toDetailUi(
    stationNameResolver: (String) -> String = { it } // plug a lookup if you have Station table
): ReservationDetailUi =
    ReservationDetailUi(
        id              = reservationId,
        stationName     = stationNameResolver(stationId),
        stationId       = stationId,
        // UI model still expects 'slotId'; give the physical connector number as a string
        slotId          = physicalSlotNumber.toString(),
        date            = reservationDate,
        timeRange       = "${startTime.toLocalHmOrBlank()}–${endTime.toLocalHmOrBlank()}",
        status          = status.toUiStatus(),
        qrCode          = qrCode,
        notes           = notes,
        createdAt       = createdAt,
        confirmedAt     = confirmedAt,
        completedAt     = completedAt,
        cancelledAt     = cancelledAt,
        canBeModified   = canModifyNow(),
        canBeCancelled  = canCancelNow()
    )

