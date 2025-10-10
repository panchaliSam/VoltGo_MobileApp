/**
 * ---------------------------------------------------------
 * File: RegistrationViewModel.kt
 * Project: VoltGo ⚡ Mobile App
 * Description:
 *   ViewModel responsible for handling user registration logic.
 *   Manages UI state, input validation, API communication via AuthManager,
 *   and navigation events after registration.
 *
 * Author: Panchali Samarasinghe
 * Created: October 10, 2025
 * Version: 1.0
 * ---------------------------------------------------------
 */
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

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val successMessage: String? = null,
    val navigationEvent: RegistrationNavigationEvent? = null
)

sealed class RegistrationNavigationEvent {
    data object NavigateToMain : RegistrationNavigationEvent()
    data object NavigateBackToLogin : RegistrationNavigationEvent()
}

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    // Handles user registration logic including validation and API call
    fun registerUser(
        email: String,
        phone: String,
        password: String,
        nic: String,
        fullName: String,
        address: String
    ) {
        // basic client-side validation
        val errors = validateInputs(email, phone, password, nic, fullName, address)
        if (errors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                fieldErrors = errors,
                errorMessage = "Please fix the highlighted fields."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, fieldErrors = emptyMap())

            try {
                val result = authManager.registerUser(
                    email = email,
                    phone = phone,
                    password = password,
                    nic = nic,
                    fullName = fullName,
                    address = address
                )

                if (result.isSuccess) {
                    // Registration successful. You can choose to navigate to Main or back to Login.
                    _uiState.value = RegistrationUiState(
                        successMessage = "Registration successful!",
                        navigationEvent = RegistrationNavigationEvent.NavigateToMain
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Registration failed. Please try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    // Resets navigation event once handled to avoid repeated navigation
    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigationEvent = null)
    }

    // Performs basic client-side validation on registration input fields
    private fun validateInputs(
        email: String,
        phone: String,
        password: String,
        nic: String,
        fullName: String,
        address: String
    ): Map<String, String> {
        val errs = mutableMapOf<String, String>()

        if (!email.contains("@") || email.isBlank()) errs["email"] = "Enter a valid email."
        if (phone.isBlank()) errs["phone"] = "Phone is required."
        if (password.length < 8) errs["password"] = "Password must be at least 8 characters."
        if (nic.isBlank()) errs["nic"] = "NIC is required."
        if (fullName.isBlank()) errs["fullName"] = "Full name is required."
        if (address.isBlank()) errs["address"] = "Address is required."

        return errs
    }
}
