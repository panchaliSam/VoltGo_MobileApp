package lk.voltgo.voltgo.ui.viewmodel.main

/**
 * ------------------------------------------------------------
 * File: UpcomingReservationsViewModel.kt
 * Author: Panchali Samarasinghe
 * Created: October 21, 2025
 * Version: 1.0
 *
 * Description:
 *  ViewModel that loads reservations for the current EV owner and exposes
 *  ONLY upcoming sessions:
 *    - reservationDateTime > now (device time, java.time.Instant)
 *    - status != Completed
 *    - status != Cancelled
 *
 *  Uses the same ReservationRepository + mapping to ReservationUi.
 * ------------------------------------------------------------
 */

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.auth.AuthManager
import lk.voltgo.voltgo.data.repository.ReservationRepository
import lk.voltgo.voltgo.data.mapper.toUi
import lk.voltgo.voltgo.ui.screens.main.ReservationStatus
import lk.voltgo.voltgo.ui.screens.main.ReservationUi
import java.time.Instant

data class UpcomingReservationsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<ReservationUi> = emptyList(),
    val lastLoadedAt: Instant? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class UpcomingReservationsViewModel @Inject constructor(
    private val repo: ReservationRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(UpcomingReservationsUiState())
    val state: StateFlow<UpcomingReservationsUiState> = _state

    private var currentOwnerNic: String? = null

    init {
        viewModelScope.launch {
            currentOwnerNic = authManager.getCurrentNIC()
            refresh()
        }
    }

    fun refresh(ownerNic: String? = null) {
        val nic = ownerNic ?: currentOwnerNic
        if (nic.isNullOrBlank()) {
            _state.update { it.copy(isLoading = false, error = "Invalid Owner NIC") }
            return
        }
        currentOwnerNic = nic

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Pull from remote and upsert to Room (assumes repo.syncMyReservations exists)
                repo.syncMyReservations(nic)

                // Observe Room and filter only future sessions (exclude Completed/Cancelled)
                repo.observeMyReservations(nic).collectLatest { entities ->
                    val now = Instant.now()

                    val upcoming = entities
                        .filter { e ->
                            // Safe parse of ISO date; if parse fails, treat as NOT upcoming
                            val whenInstant = runCatching { Instant.parse(e.reservationDate) }.getOrNull()
                            val isFuture = whenInstant?.isAfter(now) == true
                            val isActive = e.status.name != "Completed" && e.status.name != "Cancelled"
                            isFuture && isActive
                        }
                        .map { it.toUi() }
                        // Sort by actual start time if you encode it in the date string; otherwise keep stable UI sort
                        .sortedBy { it.date + it.timeRange }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            items = upcoming,
                            lastLoadedAt = Instant.now()
                        )
                    }
                }
            } catch (t: Throwable) {
                _state.update { it.copy(isLoading = false, error = t.message ?: "Unknown error") }
            }
        }
    }
}