package lk.voltgo.voltgo.ui.viewmodel.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import lk.voltgo.voltgo.data.local.entities.ChargingStationEntity
import lk.voltgo.voltgo.station.StationManager

data class StationDetailUiState(
    val isLoading: Boolean = true,
    val station: ChargingStationEntity? = null,
    val error: String? = null
)

@HiltViewModel
class SlotDetailViewModel @Inject constructor(
    private val stationManager: StationManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationDetailUiState())
    val uiState: StateFlow<StationDetailUiState> = _uiState

    // Read the same key you declared in the route ("stationId").
    // Also tolerate "id" in case you change the route later.
    private val stationId: String = savedStateHandle.get<String>("stationId")
        ?: savedStateHandle.get<String>("id")
        ?: throw IllegalStateException("stationId is required in nav args")

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = StationDetailUiState(isLoading = true)
            try {
                val st = stationManager.getStationById(stationId)
                _uiState.value = if (st == null) {
                    StationDetailUiState(isLoading = false, error = "Station not found")
                } else {
                    StationDetailUiState(isLoading = false, station = st)
                }
            } catch (e: Exception) {
                _uiState.value = StationDetailUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load station"
                )
            }
        }
    }
}