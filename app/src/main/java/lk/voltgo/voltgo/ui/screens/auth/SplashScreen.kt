/**
 * ---------------------------------------------------------
 * File: SplashScreen.kt
 * Project: VoltGo ⚡ Mobile App
 * Description:
 *   Displays the splash screen shown at app launch.
 *   Shows animated gradient background, app logo, and tagline
 *   while checking authentication status before navigating to onboarding.
 *
 * Author: Panchali Samarasinghe
 * Created: October 10, 2025
 * Version: 1.0
 * ---------------------------------------------------------
 */
package lk.voltgo.voltgo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.auth.SplashViewModel

// Displays the splash screen and navigates to onboarding after authentication check
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(2000)
        viewModel.checkAuthStatus(
            onNavigateToOnboarding = onNavigateToOnboarding
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(  brush = Brush.verticalGradient(
                colors = AppColors.splashGradient
            )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo placeholder (⚡ emoji inside circle)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = AppColors.logoBorderGradient
                        ),
                        shape = CircleShape
                    )
                    .background(
                        AppColors.TranslucentWhite05,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡",
                    style = MaterialTheme.typography.displayLarge,
                    color = AppColors.EnergyAmber
                )
            }

            // App Name
            Text(
                text = "VoltGo",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Tagline
            Text(
                text = "Find, Reserve, Charge",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TranslucentWhite90,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading indicator
            CircularProgressIndicator(
                color = Color.White,
                trackColor = AppColors.TranslucentWhite20,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Preparing your experience…",
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.TranslucentWhite85,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Displays a preview of the SplashScreen in Android Studio design view
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(
        onNavigateToOnboarding = { /* No-op for preview */ }
    )
}
