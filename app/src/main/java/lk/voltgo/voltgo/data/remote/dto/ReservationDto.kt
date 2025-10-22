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
    val slotId: String,
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
    val stationId: String,
    val slotId: String,
    val reservationDate: String,
    val notes: String?
)

data class NewReservationResponse(
    val message: String,
    val bookingId: String
)