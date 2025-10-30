// ------------------------------------------------------------
// File: CreateReservationViewModel.kt
// ------------------------------------------------------------
package lk.voltgo.voltgo.ui.viewmodel.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.remote.dto.NewReservationRequest
import lk.voltgo.voltgo.data.remote.dto.NewReservationResponse
import lk.voltgo.voltgo.data.repository.ReservationRepository

data class CreateReservationUiState(
    val stationId: String = "",
    val physicalSlotNumber: Int = 0,
    val reservationDateIso: String = "",   // ISO date (midnight Z) from server
    val startTimeIso: String = "",         // ISO-8601 Z (e.g., 2025-10-26T16:00:00Z)
    val endTimeIso: String = "",           // ISO-8601 Z
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val success: NewReservationResponse? = null
)

@HiltViewModel
class CreateReservationViewModel @Inject constructor(
    private val repo: ReservationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ---- Backward compatibility helpers ----
    private fun SavedStateHandle.requireString(key: String): String =
        checkNotNull(this[key]) { "Missing nav arg: $key" }

    private fun SavedStateHandle.optString(key: String): String? = this[key]

    private fun SavedStateHandle.requireIntOrFromString(intKey: String, legacyStringKey: String): Int {
        // Try modern key first (could be stored as Int or String depending on the NavType used)
        val anyVal: Any? = this.get<Any?>(intKey)
        when (anyVal) {
            is Int -> return anyVal
            is Number -> return anyVal.toInt()
            is String -> anyVal.toIntOrNull()?.let { return it }
        }

        // Fallback to legacy key (string "slotId")
        val legacy = this.get<String?>(legacyStringKey)
        return legacy?.toIntOrNull()
            ?: error("Missing or invalid nav arg: $intKey (or legacy $legacyStringKey)")
    }

    private val _ui = MutableStateFlow(CreateReservationUiState())
    val ui: StateFlow<CreateReservationUiState> = _ui

    fun updateNotes(notes: String) {
        _ui.value = _ui.value.copy(notes = notes)
    }

    fun initialize(
        stationId: String,
        physicalSlotNumber: Int,
        reservationDateIso: String,
        startTimeIso: String,
        endTimeIso: String
    ) {
        _ui.value = ui.value.copy(
            stationId = stationId,
            physicalSlotNumber = physicalSlotNumber + 1, // <-- Add 1 here
            reservationDateIso = reservationDateIso,
            startTimeIso = startTimeIso,
            endTimeIso = endTimeIso
        )
    }
    fun submit() {
        val curr = _ui.value
        if (curr.isSubmitting) return

        // Validation
        if (curr.physicalSlotNumber < 1) {
            _ui.value = curr.copy(error = "Invalid slot number")
            return
        }
        if (curr.startTimeIso.isBlank() || curr.endTimeIso.isBlank()) {
            _ui.value = curr.copy(error = "Start and end time are required")
            return
        }

        _ui.value = curr.copy(isSubmitting = true, error = null)

        viewModelScope.launch {
            val result = repo.createReservation(
                NewReservationRequest(
                    stationId = curr.stationId,
                    physicalSlotNumber = curr.physicalSlotNumber,
                    reservationDate = curr.reservationDateIso,
                    startTime = curr.startTimeIso,
                    endTime = curr.endTimeIso,
                    notes = curr.notes.ifBlank { null }
                )
            )

            _ui.value = result.fold(
                onSuccess = { curr.copy(success = it, isSubmitting = false) },
                onFailure = { curr.copy(error = it.message ?: "Reservation failed", isSubmitting = false) }
            )
        }
    }

    fun clearError() { _ui.value = _ui.value.copy(error = null) }
}