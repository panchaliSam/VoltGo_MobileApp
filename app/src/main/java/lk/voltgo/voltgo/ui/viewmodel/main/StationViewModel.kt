package lk.voltgo.voltgo.ui.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.entities.ChargingStationEntity
import lk.voltgo.voltgo.station.StationManager
import javax.inject.Inject

data class StationUiState(
    val isLoading: Boolean = false,
    val stations: List<ChargingStationEntity> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class StationViewModel @Inject constructor(
    private val stationManager: StationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationUiState())
    val uiState: StateFlow<StationUiState> get() = _uiState

    // Keep all stations locally for filtering
    private var allStations: List<ChargingStationEntity> = emptyList()

    init {
        loadStations()
    }

    fun loadStations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                stationManager.getAllStations().collect { stations ->
                    allStations = stations
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stations = stations
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load stations"
                )
            }
        }
    }

    fun searchStations(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, searchQuery = query)
            try {
                val results = if (query.isBlank()) allStations
                else allStations.filter { it.name.contains(query, ignoreCase = true) }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    stations = results
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Search failed"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
