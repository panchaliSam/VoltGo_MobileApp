package lk.voltgo.voltgo.ui.viewmodel.main

/**
 * ------------------------------------------------------------
 * File: ReservationDetailsViewModel.kt
 * Author: Panchali Samarasinghe
 * Created: October 21, 2025
 * Version: 1.0
 * ------------------------------------------------------------
 */
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.repository.ReservationRepository
import lk.voltgo.voltgo.data.mapper.toDetailUi
import lk.voltgo.voltgo.data.remote.dto.ReservationDetailUi

data class ReservationDetailsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val data: ReservationDetailUi? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ReservationDetailsViewModel @Inject constructor(
    private val repo: ReservationRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationDetailsUiState())
    val state: StateFlow<ReservationDetailsUiState> = _state

    private val reservationId: String =
        savedStateHandle["reservationId"] ?: savedStateHandle["id"] ?: ""

    init { load() }

    fun load() {
        if (reservationId.isBlank()) {
            _state.value = ReservationDetailsUiState(isLoading = false, error = "Missing reservation id")
            return
        }
        _state.value = ReservationDetailsUiState(isLoading = true)
        viewModelScope.launch {
            val result = repo.getReservationById(reservationId)
            _state.value = result.fold(
                onSuccess = { entity ->
                    ReservationDetailsUiState(isLoading = false, data = entity.toDetailUi())
                },
                onFailure = { e ->
                    ReservationDetailsUiState(isLoading = false, error = e.message ?: "Failed to load reservation")
                }
            )
        }
    }
}