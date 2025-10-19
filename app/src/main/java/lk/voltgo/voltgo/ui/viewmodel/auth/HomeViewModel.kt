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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    /** Logs out the current user by clearing token from AuthManager and DataStore */
    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authManager.logout()
            onLoggedOut() // navigate to login screen
        }
    }
}
