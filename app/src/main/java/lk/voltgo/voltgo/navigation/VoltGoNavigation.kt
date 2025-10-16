/**
 * ------------------------------------------------------------
 * File: VoltGoNavigation.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This file defines the main navigation setup for the VoltGo app using Jetpack Compose Navigation.
 * It manages the navigation graph for authentication, main app flow, and related screens.
 * Each route corresponds to a composable screen, allowing seamless user navigation.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import lk.voltgo.voltgo.ui.screens.auth.LoginScreen
import lk.voltgo.voltgo.ui.screens.auth.OnboardingScreen
import lk.voltgo.voltgo.ui.screens.auth.RegisterScreen
import lk.voltgo.voltgo.ui.screens.auth.SplashScreen
import lk.voltgo.voltgo.ui.screens.auth.EditProfileScreen
import lk.voltgo.voltgo.ui.screens.main.CreateReservationScreen
import lk.voltgo.voltgo.ui.screens.main.StationsScreen
import lk.voltgo.voltgo.ui.screens.main.HomeScreen
import lk.voltgo.voltgo.ui.screens.main.MyReservationsScreen
import lk.voltgo.voltgo.ui.screens.operator.EVOperatorScreen

@Composable
// Sets up the navigation graph for VoltGo app including authentication and main flows
fun VoltGoNavigation(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Authentication navigation graph (Splash, Onboarding, Login, Register)
        navigation(
            startDestination = Screen.Splash.route,
            route = NavigationGraph.Auth.route
        ) {
            composable(Screen.Splash.route) {
                // Splash Screen - navigates to Onboarding
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.Onboarding.route) {
                // Onboarding Screen - navigates to Login
                OnboardingScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.Login.route) {
                // Login Screen - handles login and navigation to Main graph
                LoginScreen(
                    onLoginSuccess = { token: String ->
                        navController.navigate(NavigationGraph.Main.route) {
                            popUpTo(NavigationGraph.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onOperatorLoginSuccess = { token: String ->
                        navController.navigate(NavigationGraph.Operator.route) {
                            popUpTo(NavigationGraph.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(Screen.Register.route) {
                // Register Screen - handles registration and navigation back to Auth flow
                RegisterScreen(
                    onRegister = {
                        navController.navigate(NavigationGraph.Auth.route) {
                            popUpTo(NavigationGraph.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
        }
        navigation(
            startDestination = Screen.OperatorHome.route,
            route = NavigationGraph.Operator.route
        ) {
            composable(Screen.OperatorHome.route) {
                // You can fetch this from a ViewModel; hardcoded here for clarity
                val stationName = "Downtown SuperCharge"

                EVOperatorScreen(
                    stationName = stationName,
                    onBackClick = { navController.popBackStack() },
                    onRefresh = {
                        // TODO: trigger refresh (e.g., viewModel.refreshOperatorData())
                    },
                    onViewReservation = { res ->
                        // Navigate to reservation details
                        // TODO: trigger refresh (e.g., viewModel.refreshOperatorData())
                        //navController.navigate("${Screen.ReservationDetails.route}/${res.id}")
                    },
                    onScanQrFor = { res ->
                        // Open scanner initialized for a specific reservation
                        // TODO: trigger refresh (e.g., viewModel.refreshOperatorData())
                        //navController.navigate("${Screen.QRScanner.route}?reservationId=${res.id}")
                    },
                    onQuickScan = {
                        // Open generic scanner
                        // TODO: trigger refresh (e.g., viewModel.refreshOperatorData())
                        //navController.navigate(Screen.QRScanner.route)
                    }
                )
            }
        }
        // Main navigation graph for EV owner features (Home, Reservations, Stations, Profile)
        navigation(
            startDestination = Screen.Home.route,
            route = NavigationGraph.Main.route
        ) {
            composable(Screen.Home.route) {
                // Home Screen - provides access to My Reservations, New Reservation, Stations, and Profile
                HomeScreen(
                    onMyReservationsClick = {
                        navController.navigate(Screen.MyReservations.route)
                    },
                    onNewReservationClick = {
                        navController.navigate(Screen.NewReservation.route)
                    },
                    onFindStationsClick = {
                        navController.navigate(Screen.Stations.route)
                    },
                    onEditProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onLogoutClick = {
                        navController.navigate(NavigationGraph.Auth.route) {
                            popUpTo(NavigationGraph.Main.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.MyReservations.route) {
                // My Reservations Screen - displays user's existing reservations
                MyReservationsScreen(
                    onBackClick = { navController.popBackStack() },
                    onViewDetails = { _ ->
                        // TODO: Navigate to reservation details when that screen exists
                        // navController.navigate(Screen.ReservationDetails.route)
                    },
                    onCancelReservation = { _ ->
                        // TODO: Handle cancel flow or navigate to a confirmation dialog/screen
                    }
                )
            }
            composable(Screen.NewReservation.route) {
                // Create Reservation Screen - allows user to create a new reservation
                CreateReservationScreen(
                    onBackClick = { navController.popBackStack() },
                    onOpenMap = { navController.popBackStack() },
                    onSubmit = { _ ->
                        // TODO: Handle cancel flow or navigate to a confirmation dialog/screen
                    }
                )
            }
            composable(Screen.Stations.route) {
                // Stations Screen - displays available charging stations on map
                StationsScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }
            composable(Screen.Profile.route) {
                // Edit Profile Screen - allows user to update profile details
                EditProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}