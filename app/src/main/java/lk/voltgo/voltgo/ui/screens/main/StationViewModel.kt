package lk.voltgo.voltgo.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    val uiState: StateFlow<StationUiState> = _uiState.asStateFlow()

    init {
        loadStations()
    }

    fun loadStations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                stationManager.getAllStations().collect { stations ->
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
                val results = stationManager.searchStations(query)
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