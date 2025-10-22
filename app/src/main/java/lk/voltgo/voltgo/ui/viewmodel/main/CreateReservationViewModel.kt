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
    val slotId: String = "",
    val reservationDateIso: String = "",
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

    private val stationId: String = checkNotNull(savedStateHandle["stationId"])
    private val slotId: String = checkNotNull(savedStateHandle["slotId"])
    private val reservationDateIso: String = checkNotNull(savedStateHandle["reservationDateIso"])

    private val _ui = MutableStateFlow(
        CreateReservationUiState(
            stationId = stationId,
            slotId = slotId,
            reservationDateIso = reservationDateIso
        )
    )
    val ui: StateFlow<CreateReservationUiState> = _ui

    fun updateNotes(notes: String) {
        _ui.value = _ui.value.copy(notes = notes)
    }

    fun submit() {
        val curr = _ui.value
        if (curr.isSubmitting) return
        _ui.value = curr.copy(isSubmitting = true, error = null)

        viewModelScope.launch {
            val res = repo.createReservation(
                NewReservationRequest(
                    stationId = curr.stationId,
                    slotId = curr.slotId,
                    reservationDate = curr.reservationDateIso,
                    notes = curr.notes.ifBlank { null }
                )
            )
            _ui.value = res.fold(
                onSuccess = { curr.copy(isSubmitting = false, success = it) },
                onFailure = { curr.copy(isSubmitting = false, error = it.message ?: "Failed to create reservation") }
            )
        }
    }

    fun clearError() { _ui.value = _ui.value.copy(error = null) }
}