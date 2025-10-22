package lk.voltgo.voltgo.ui.viewmodel.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.remote.dto.SlotDto
import lk.voltgo.voltgo.data.repository.SlotRepository

data class SlotPickerUiState(
    val isLoading: Boolean = true,
    val slots: List<SlotDto> = emptyList(),
    val error: String? = null,
    val stationId: String = ""
)

@HiltViewModel
class SlotPickerViewModel @Inject constructor(
    private val repo: SlotRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stationId: String = checkNotNull(savedStateHandle["stationId"]) {
        "stationId is required in nav args"
    }

    private val _uiState = MutableStateFlow(SlotPickerUiState(stationId = stationId))
    val uiState: StateFlow<SlotPickerUiState> = _uiState

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val data = repo.getSlotsByStation(stationId)
                _uiState.value = _uiState.value.copy(isLoading = false, slots = data)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load slots"
                )
            }
        }
    }
}