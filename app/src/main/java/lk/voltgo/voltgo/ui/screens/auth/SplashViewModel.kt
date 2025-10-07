package lk.voltgo.voltgo.ui.screens.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import lk.voltgo.voltgo.auth.AuthManager
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel(){
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