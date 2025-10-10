package lk.voltgo.voltgo.ui.screens.main

/**
 * ------------------------------------------------------------
 * File: StationViewModel.kt
 * Author: Ishini Aposo
 * Created: October 10, 2025
 * Version: 1.0
 *
 * Description:
 * This ViewModel manages the state and logic for the Stations screen in the VoltGo app.
 * It communicates with the StationManager to load, search, and manage charging station data.
 * The ViewModel exposes UI state using StateFlow and handles asynchronous operations with coroutines.
 * ------------------------------------------------------------
 */

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

    // Loads all charging stations from the StationManager.
    // Updates the UI state to show a loading indicator, and populates the station list when complete.
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

    // Searches charging stations based on the user’s query.
    // Updates the UI state with the filtered results or an error message if the search fails.
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

    // Clears any existing error message from the UI state.
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}