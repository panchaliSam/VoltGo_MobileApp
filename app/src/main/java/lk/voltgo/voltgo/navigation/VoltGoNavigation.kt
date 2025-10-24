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

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import lk.voltgo.voltgo.ui.screens.auth.LoginScreen
import lk.voltgo.voltgo.ui.screens.auth.OnboardingScreen
import lk.voltgo.voltgo.ui.screens.auth.RegisterScreen
import lk.voltgo.voltgo.ui.screens.auth.SplashScreen
import lk.voltgo.voltgo.ui.screens.auth.EditProfileScreen
import lk.voltgo.voltgo.ui.screens.main.CreateReservationScreen
import lk.voltgo.voltgo.ui.screens.main.StationsScreen
import lk.voltgo.voltgo.ui.screens.main.HomeScreen
import lk.voltgo.voltgo.ui.screens.main.MyReservationsScreen
import lk.voltgo.voltgo.ui.screens.main.ReservationDetailsScreen
import lk.voltgo.voltgo.ui.screens.main.SlotDetailScreen
import lk.voltgo.voltgo.ui.screens.main.SlotPickerScreen
import lk.voltgo.voltgo.ui.screens.main.UpcomingReservationsScreen
import lk.voltgo.voltgo.ui.screens.operator.EVOperatorScreen

@RequiresApi(Build.VERSION_CODES.O)
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
                val stationName = "VoltGo ⚡"

                EVOperatorScreen(
                    stationName = stationName,
                    onLoggedOut = { navController.navigate(Screen.Login.route)}
                )
            }
        }
        // Main navigation graph for EV owner features (Home, Reservations, Stations, Profile)
        navigation(
            startDestination = Screen.Home.route,
            route = NavigationGraph.Main.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onMyReservationsClick = {
                        navController.navigate(Screen.MyReservations.route)
                    },
                    onUpcomingReservationsClick = {
                        navController.navigate(Screen.UpcomingReservations.route)
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
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
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
                    }
                )
            }
            composable(Screen.UpcomingReservations.route) {
                UpcomingReservationsScreen(
                    onBackClick = { navController.popBackStack() },
                    onViewDetails = { res ->
                        val encoded = Uri.encode(res.id)
                        navController.navigate(Screen.reservationDetailsRoute(encoded))
                    }
                )
            }
            composable(
                route = "${Screen.ReservationDetails.route}/{reservationId}",
                arguments = listOf(navArgument("reservationId") { type = NavType.StringType })
            ) {
                ReservationDetailsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            // Add this inside your main navigation graph
            composable(
                route = "${Screen.NewReservation.route}/{stationId}/{physicalSlotNumber}/{reservationDateIso}/{startTimeIso}/{endTimeIso}",
                arguments = listOf(
                    navArgument("stationId") { type = NavType.StringType },
                    navArgument("physicalSlotNumber") { type = NavType.StringType },
                    navArgument("reservationDateIso") { type = NavType.StringType },
                    navArgument("startTimeIso") { type = NavType.StringType },
                    navArgument("endTimeIso") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                CreateReservationScreen(
                    onBackClick = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() },
                    navBackStackEntry = backStackEntry   // <-- pass args here
                )
            }

            composable(Screen.Stations.route) {
                // Stations Screen - displays available charging stations on map
                StationsScreen(
                    onBackClick = { navController.popBackStack() },
                    onStationClick = { stationId ->
                        val encoded = Uri.encode(stationId)
                        navController.navigate(Screen.slotDetailsRoute(encoded))
                    }
                )
            }
            composable(
                route = "${Screen.SlotDetails.route}/{stationId}",
                arguments = listOf(navArgument("stationId") { type = NavType.StringType })
            ) {
                SlotDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    onReserve = { stationId ->
                        navController.navigate(Screen.slotPickerRoute(Uri.encode(stationId)))
                    }
                )
            }
            composable(
                route = "${Screen.SlotPicker.route}/{stationId}",
                arguments = listOf(navArgument("stationId") { type = NavType.StringType })
            ) {
                SlotPickerScreen(
                    onBackClick = { navController.popBackStack() },
                    onConfirm = { stationId, slotNumber, reservationDate, startTime, endTime ->
                        val encodedStationId = Uri.encode(stationId)
                        val encodedDate = Uri.encode(reservationDate)
                        val encodedStartTime = Uri.encode(startTime)
                        val encodedEndTime = Uri.encode(endTime)
                        val encodedSlot = Uri.encode(slotNumber.toString())

                        navController.navigate(
                            "${Screen.NewReservation.route}/$encodedStationId/$encodedSlot/$encodedDate/$encodedStartTime/$encodedEndTime"
                        )
                    }
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