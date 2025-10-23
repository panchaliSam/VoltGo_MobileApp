package lk.voltgo.voltgo.ui.viewmodel.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import lk.voltgo.voltgo.auth.AuthManager
import lk.voltgo.voltgo.data.mapper.toUi
import lk.voltgo.voltgo.data.repository.ReservationRepository
import lk.voltgo.voltgo.ui.screens.main.ReservationStatus
import lk.voltgo.voltgo.ui.screens.main.ReservationUi
import java.time.Instant
import javax.inject.Inject

enum class ReservationFilter { All, Confirmed, Pending, Completed, Cancelled }

data class ReservationsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val all: List<ReservationUi> = emptyList(),
    val filtered: List<ReservationUi> = emptyList(),
    val activeFilter: ReservationFilter = ReservationFilter.All,
    val lastLoadedAt: Instant? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val repo: ReservationRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationsUiState(isLoading = false))
    val state: StateFlow<ReservationsUiState> = _state

    // DO NOT call suspend funcs at init time; load NIC asynchronously.
    private var currentOwnerNic: String? = null

    init {
        viewModelScope.launch {
            currentOwnerNic = authManager.getCurrentNIC()
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
                // Sync from remote -> upsert to Room (assumes you implemented this)
                repo.syncMyReservations(nic)

                // Re-collect latest stream and map to UI
                repo.observeMyReservations(nic).collectLatest { entities ->
                    val uiList = entities.map { it.toUi() }
                        .sortedBy { it.date + it.timeRange } // stable sort key for display

                    _state.update { s ->
                        val filtered = applyFilter(uiList, s.activeFilter)
                        s.copy(
                            isLoading = false,
                            error = null,
                            all = uiList,
                            filtered = filtered,
                            lastLoadedAt = Instant.now()
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun cancelReservation(resId: String) {
        val nic = currentOwnerNic ?: return
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = repo.cancelReservation(resId)
            if (result.isSuccess) {
                refresh(nic)
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun setFilter(filter: ReservationFilter) {
        _state.update { s ->
            s.copy(
                activeFilter = filter,
                filtered = applyFilter(s.all, filter)
            )
        }
    }

    private fun applyFilter(all: List<ReservationUi>, filter: ReservationFilter): List<ReservationUi> =
        when (filter) {
            ReservationFilter.All        -> all
            ReservationFilter.Confirmed  -> all.filter { it.status == ReservationStatus.Confirmed }
            ReservationFilter.Pending    -> all.filter { it.status == ReservationStatus.Pending }
            ReservationFilter.Completed  -> all.filter { it.status == ReservationStatus.Completed }
            ReservationFilter.Cancelled  -> all.filter { it.status == ReservationStatus.Cancelled }
        }
}
