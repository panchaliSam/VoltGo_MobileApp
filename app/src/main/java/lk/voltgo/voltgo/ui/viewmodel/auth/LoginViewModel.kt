/**
 * ---------------------------------------------------------
 * File: LoginViewModel.kt
 * Project: VoltGo ⚡ Mobile App
 * Description:
 *   ViewModel responsible for managing user login functionality.
 *   Handles login requests, authentication via AuthManager, UI state management,
 *   and navigation events based on user roles (EV Owner or Operator).
 *
 * Author: Panchali Samarasinghe
 * Created: October 10, 2025
 * Version: 1.0
 * ---------------------------------------------------------
 */
package lk.voltgo.voltgo.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.auth.AuthManager
import lk.voltgo.voltgo.data.remote.types.RoleType
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
        val token: String,
        val role: RoleType
    ) : LoginNavigationEvent()
    data class NavigateToOperator(
        val token: String,
        val role: RoleType
    ) : LoginNavigationEvent()
}


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Handles user login by calling AuthManager and updates UI state and navigation events
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = authManager.loginUser(username, password)

                when {
                    result.isSuccess -> {
                        val response = result.getOrNull()
                        when (response?.role) {
                            RoleType.EV_OWNER  -> _uiState.value = LoginUiState(navigationEvent = LoginNavigationEvent.NavigateToMain(
                                token = response.token,
                                role = response.role
                            ))
                            RoleType.STATION_OPERATOR  -> _uiState.value = LoginUiState(navigationEvent = LoginNavigationEvent.NavigateToOperator(
                                token = response.token,
                                role = response.role
                            ))
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

    // Clears navigation events after they are processed to prevent re-triggering
    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigationEvent = null)
    }
}