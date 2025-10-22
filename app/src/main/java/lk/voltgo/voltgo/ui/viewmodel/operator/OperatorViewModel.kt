package lk.voltgo.voltgo.ui.viewmodel.operator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.remote.dto.BookingDto
import lk.voltgo.voltgo.data.repository.OperatorRepository

data class OperatorUiState(
    val isVerifying: Boolean = false,
    val isCompleting: Boolean = false,
    val error: String? = null,
    val info: String? = null,
    val booking: BookingDto? = null
)

@HiltViewModel
class OperatorViewModel @Inject constructor(
    private val repo: OperatorRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(OperatorUiState())
    val ui: StateFlow<OperatorUiState> = _ui

    fun reset() = _ui.update { OperatorUiState() }

    fun verifyBase64(base64: String) {
        _ui.update { it.copy(isVerifying = true, error = null, info = null) }
        viewModelScope.launch {
            val result = repo.scanQr(base64)
            _ui.update {
                result.fold(
                    onSuccess = { res ->
                        it.copy(
                            isVerifying = false,
                            booking = res.booking,
                            info = res.message,
                            error = null
                        )
                    },
                    onFailure = { e ->
                        it.copy(isVerifying = false, error = e.message ?: "Verification failed")
                    }
                )
            }
        }
    }

    fun complete() {
        _ui.update { it.copy(isCompleting = true, error = null, info = null) }
        viewModelScope.launch {
            val result = repo.complete()
            _ui.update {
                result.fold(
                    onSuccess = { msg ->
                        // mark completed in UI
                        val updated = it.booking?.copy(status = "Completed")
                        it.copy(
                            isCompleting = false,
                            booking = updated,
                            info = msg.message
                        )
                    },
                    onFailure = { e ->
                        it.copy(isCompleting = false, error = e.message ?: "Complete failed")
                    }
                )
            }
        }
    }
}