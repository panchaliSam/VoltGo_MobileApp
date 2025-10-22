package lk.voltgo.voltgo.ui.viewmodel.auth

/**
 * ------------------------------------------------------------
 * File: HomeViewModel.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-16
 *
 * Description:
 * ViewModel for the VoltGo Home screen.
 * Handles logout and any future home-level logic such as session checks.
 * ------------------------------------------------------------
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.auth.AuthManager
import lk.voltgo.voltgo.data.repository.UserRepository

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val userRepository: UserRepository
) : ViewModel() {

    /** Logs out the current user by clearing token from AuthManager and DataStore */
    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authManager.logout()
            onLoggedOut() // navigate to login screen
        }
    }
    fun deactivateAndLogout(
        onLoggedOut: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = userRepository.deactivateMe()
                if (result.isSuccess) {
                    authManager.logout()
                    onLoggedOut()
                } else {
                    val message = result.exceptionOrNull()?.message ?: "Failed to deactivate"
                    onError(message)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unexpected error during deactivation")
            }
        }
    }
}
