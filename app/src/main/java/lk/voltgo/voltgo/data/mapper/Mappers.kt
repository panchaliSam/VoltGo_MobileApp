package lk.voltgo.voltgo.data.mapper

/**
 * ------------------------------------------------------------
 * File: ReservationMapper.kt
 * Author: Panchali Samarasinghe
 * Created: October 20, 2025
 * Version: 1.0
 *
 * Description:
 *  Mapping utilities from API DTOs to lightweight UI models used
 *  by the My Reservations screen. Includes date/time formatting
 *  for mobile-friendly display and a best-effort station name
 *  extraction from notes.
 * ------------------------------------------------------------
 */

import android.os.Build
import androidx.annotation.RequiresApi
import lk.voltgo.voltgo.data.remote.dto.ReservationResponse
import lk.voltgo.voltgo.ui.screens.main.ReservationStatus
import lk.voltgo.voltgo.ui.screens.main.ReservationUi
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
private val dateFmt = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
@RequiresApi(Build.VERSION_CODES.O)
private val timeFmt = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
private val stationAtRegex = Regex(" at (.+)$")

@RequiresApi(Build.VERSION_CODES.O)
private fun parseZoned(iso: String): ZonedDateTime =
    ZonedDateTime.ofInstant(Instant.parse(iso), ZoneId.systemDefault())

@RequiresApi(Build.VERSION_CODES.O)
private fun buildTimeRange(start: ZonedDateTime): String {
    val end = start.plusHours(1)
    return "${start.format(timeFmt)}–${end.format(timeFmt)}"
}

private fun toStatus(s: String): ReservationStatus = when (s.trim().lowercase()) {
    "confirmed" -> ReservationStatus.Confirmed
    "pending"   -> ReservationStatus.Pending
    "completed" -> ReservationStatus.Completed
    "cancelled" -> ReservationStatus.Cancelled
    else        -> ReservationStatus.Pending // sensible fallback
}

private fun extractStationName(stationId: String, notes: String?): String {
    if (!notes.isNullOrBlank()) {
        val m = stationAtRegex.find(notes)
        if (m != null && m.groupValues.size > 1) {
            return m.groupValues[1].trim()
        }
    }
    // Fallback to an abbreviated id for now; replace with real lookup if you have a StationDao
    return "Station ${stationId.takeLast(6)}"
}

/** If backend doesn't send a qrCode, compose a compact JSON payload as fallback */
private fun buildQrPayload(
    bookingId: String,
    ownerNIC: String,
    stationId: String,
    reservationDateIso: String,
    status: ReservationStatus
): String {
    // Keep it simple & QR-friendly (no pretty spaces)
    return """{"BookingId":"$bookingId","OwnerNIC":"$ownerNIC","StationId":"$stationId","ReservationDate":"$reservationDateIso","Status":"${status.name}"}"""
}

@RequiresApi(Build.VERSION_CODES.O)
fun ReservationResponse.toUi(): ReservationUi {
    val start = parseZoned(reservationDate)
    val uiStatus = toStatus(status)

    return ReservationUi(
        id = "#${id.takeLast(6)}",
        station = extractStationName(stationId, notes),
        date = start.format(dateFmt),
        timeRange = buildTimeRange(start),
        status = uiStatus,
        qrCode = (qrCode?.takeIf { it.isNotBlank() }
            ?: buildQrPayload(
                bookingId = id,
                ownerNIC = ownerNIC,
                stationId = stationId,
                reservationDateIso = reservationDate,
                status = uiStatus
            ))
    )
}
