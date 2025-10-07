package lk.voltgo.voltgo.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.auth.AuthManager
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigationEvent: LoginNavigationEvent? = null
)

sealed class LoginNavigationEvent {
    data class NavigateToRegister(
        val token: String,
        val email: String,
        val displayName: String
    ) : LoginNavigationEvent()
    data class NavigateToMain(
        val token: String
    ) : LoginNavigationEvent()
    data object NavigateToOperator : LoginNavigationEvent()
}


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = authManager.loginUser(username, password)

                when {
                    result.isSuccess -> {
                        val response = result.getOrNull()
                        when (response?.role?.lowercase()) {
                            "evowner" -> _uiState.value = LoginUiState(navigationEvent = LoginNavigationEvent.NavigateToMain(
                                token = response.token
                            ))
                            "operator" -> _uiState.value = LoginUiState(navigationEvent = LoginNavigationEvent.NavigateToOperator)
                            else -> _uiState.value = LoginUiState(errorMessage = "Invalid role")
                        }
                    }
                    result.isFailure -> {
                        _uiState.value = LoginUiState(errorMessage = "Login failed. Please try again.")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(errorMessage = e.message ?: "Unknown error occurred")
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigationEvent = null)
    }
}