package lk.voltgo.voltgo.ui.viewmodel.main

/**
 * ------------------------------------------------------------
 * File: ReservationViewModel.kt
 * Author: Panchali Samarasinghe
 * Created: October 20, 2025
 * Version: 1.0
 *
 * Description:
 *  ViewModel that loads reservations from the API via the repository,
 *  maps them to UI models, and filters by status category.
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.mapper.toUi
import lk.voltgo.voltgo.data.repository.ReservationRepository
import lk.voltgo.voltgo.ui.screens.main.ReservationStatus
import lk.voltgo.voltgo.ui.screens.main.ReservationUi
import java.time.Instant

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
    private val repo: ReservationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationsUiState(isLoading = true))
    val state: StateFlow<ReservationsUiState> = _state

    init {
        refresh()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refresh() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = repo.getMyReservations()
            result
                .onSuccess { list ->
                    val uiList = list.map { it.toUi() }
                        .sortedBy { it.date + it.timeRange } // simple stable ordering
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
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to load reservations") }
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