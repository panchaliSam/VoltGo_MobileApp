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

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigationEvent = null)
    }

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
