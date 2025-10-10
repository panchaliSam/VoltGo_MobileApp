/****
 * ---------------------------------------------------------
 * File: SplashViewModel.kt
 * Project: VoltGo ⚡ Mobile App
 * Description:
 *   ViewModel responsible for checking user authentication status
 *   and navigating to the appropriate screen (onboarding or main).
 *
 * Author: Panchali Samarasinghe
 * Created: October 10, 2025
 * Version: 1.0
 * ---------------------------------------------------------
 */
package lk.voltgo.voltgo.ui.screens.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import lk.voltgo.voltgo.auth.AuthManager
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel(){
    // Checks if the user is logged in and navigates accordingly
    suspend fun checkAuthStatus(
        onNavigateToOnboarding: () -> Unit,
    ) {
        if (authManager.isLoggedIn()) {
            // User is logged in, navigate to main
            onNavigateToOnboarding()
        } else {
            // User is not logged in, show onboarding
            onNavigateToOnboarding()
        }
    }
}