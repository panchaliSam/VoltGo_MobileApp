// File: SlotDetailViewModel.kt
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
import lk.voltgo.voltgo.data.remote.api.StationApiService
import lk.voltgo.voltgo.station.StationManager

// --- UI models for slots ---
data class PhysicalSlotUi(
    val number: Int,
    val isActive: Boolean,
    val label: String?,
    val connectorType: String?,
    val maxKw: Int?
)

data class StationDetailUiState(
    val isLoading: Boolean = true,
    val station: ChargingStationEntity? = null,
    val physicalSlots: List<PhysicalSlotUi> = emptyList(),
    val totalSlots: Int = 0,        // convenience for "Example (number of slots)"
    val error: String? = null
)

@HiltViewModel
class SlotDetailViewModel @Inject constructor(
    private val stationManager: StationManager,
    private val stationApi: StationApiService,                 // <-- inject API to get detail
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationDetailUiState())
    val uiState: StateFlow<StationDetailUiState> = _uiState

    private val stationId: String = savedStateHandle.get<String>("stationId")
        ?: savedStateHandle.get<String>("id")
        ?: throw IllegalStateException("stationId is required in nav args")

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = StationDetailUiState(isLoading = true)

            try {
                // 1) Get the locally-cached/basic entity (no slots)
                val base = stationManager.getStationById(stationId)

                // 2) Fetch rich detail (with physicalSlots) from API
                val detailResp = stationApi.getStationById(stationId)
                val slots = if (detailResp.isSuccessful) {
                    detailResp.body()?.physicalSlots.orEmpty().map {
                        PhysicalSlotUi(
                            number = it.number,
                            isActive = it.isActive,
                            label = it.label,
                            connectorType = it.connectorType,
                            maxKw = it.maxKw
                        )
                    }
                } else emptyList()

                val totalSlots = when {
                    slots.isNotEmpty() -> slots.size
                    base?.availableSlots != null -> base.availableSlots!!
                    else -> 0
                }

                _uiState.value = StationDetailUiState(
                    isLoading = false,
                    station = base,
                    physicalSlots = slots.sortedBy { it.number },
                    totalSlots = totalSlots,
                    error = if (base == null) "Station not found" else null
                )
            } catch (e: Exception) {
                _uiState.value = StationDetailUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load station"
                )
            }
        }
    }
}