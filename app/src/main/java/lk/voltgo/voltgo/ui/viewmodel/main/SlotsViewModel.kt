//package lk.voltgo.voltgo.ui.viewmodel.main
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import lk.voltgo.voltgo.data.repository.SlotsRepository
//import lk.voltgo.voltgo.data.local.entities.SlotEntity
//import javax.inject.Inject
//
//data class SlotsUiState(
//    val slots: List<SlotEntity> = emptyList(),
//    val isLoading: Boolean = false
//)
//
//@HiltViewModel
//class SlotsViewModel @Inject constructor(
//    private val repository: SlotsRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(SlotsUiState())
//    val uiState: StateFlow<SlotsUiState> = _uiState
//
//    fun loadSlots(stationId: String) {
//        viewModelScope.launch {
//            _uiState.value = SlotsUiState(isLoading = true)
//            val slots = repository.getSlotsByStationId(stationId)
//            _uiState.value = SlotsUiState(slots = slots, isLoading = false)
//        }
//    }
//}