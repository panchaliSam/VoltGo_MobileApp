package lk.voltgo.voltgo.data.remote.dto

import lk.voltgo.voltgo.ui.screens.main.ReservationStatus

/**
 * ------------------------------------------------------------
 * File: ReservationResponse.kt
 * Author: Panchali Samarasinghe
 * Created: October 2025
 * Description:
 *   Represents a reservation response object returned from
 *   the VoltGo backend API. It maps reservation details such as
 *   owner, station, slot, reservation date, status, and metadata.
 * ------------------------------------------------------------
 */

data class ReservationResponse(
    val id: String,
    val ownerNIC: String,
    val stationId: String,
    val physicalSlotNumber: Int,
    val reservationDate: String,
    val startTime: String,         // "2025-10-26T16:00:00Z"
    val endTime: String,           // "2025-10-26T17:00:00Z"
    val createdAt: String,         // "2025-10-24T03:53:41.802Z"
    val status: String,            // "Confirmed" | "Pending" | ...
    val qrCode: String?,           // JSON string (BookingId, etc.)
    val notes: String?,            // e.g., "Benz E200"
    val confirmedAt: String?,      // optional ISO-8601 timestamp
    val completedAt: String?,      // optional ISO-8601 timestamp
    val cancelledAt: String?,      // optional ISO-8601 timestamp
    val canBeModified: Boolean,
    val canBeCancelled: Boolean,
    val isWithin7Days: Boolean
)

data class ReservationDetailUi(
    val id: String,
    val stationName: String,
    val stationId: String,
    val slotId: String,
    val date: String,
    val timeRange: String,
    val status: ReservationStatus,
    val qrCode: String?,
    val notes: String?,
    val createdAt: String?,
    val confirmedAt: String?,
    val completedAt: String?,
    val cancelledAt: String?,
    val canBeModified: Boolean,
    val canBeCancelled: Boolean
)

data class NewReservationRequest(
    val stationId: String,          // "68fa3f0a4935d1e55425dc40"
    val physicalSlotNumber: Int,    // 2
    val reservationDate: String,    // "2025-10-26T03:45:49.523Z"
    val startTime: String,          // "2025-10-26T16:00:00.000Z"
    val endTime: String,            // "2025-10-26T17:00:00.000Z"
    val notes: String?              // "Benz E200"
)

data class NewReservationResponse(
    val message: String,
    val bookingId: String
)

data class ReservationMessageResponse(
    val message: String
)