/**
 * ------------------------------------------------------------
 * File: Screen.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This file defines all the navigation routes for the VoltGo app using a sealed class structure.
 * Each object represents a distinct screen or navigation graph used in the app’s navigation system.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.navigation

// Defines all individual screen routes for different app flows
sealed class Screen(val route: String) {
    // Authentication Flow Screens
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Profile : Screen("profile")

    // Main Flow - EV Owner Screens
    data object Home : Screen("home")
    data object MyReservations : Screen("my_reservations")
    data object NewReservation : Screen("new_reservation")
    data object Stations : Screen("stations")
    data object UpcomingReservations : Screen("upcoming_reservations")
    data object ReservationDetails : Screen("reservation_details")

    // Operator Flow Screens
    data object OperatorHome : Screen("operator_home")

    companion object {
        fun reservationDetailsRoute(id: String) =
            "${ReservationDetails.route}/$id"
    }
}

// Defines the main navigation graph routes for different app sections
sealed class NavigationGraph(val route: String) {
    // Graph for authentication-related screens
    data object Auth : NavigationGraph("auth_graph")
    // Graph for EV owner-related screens
    data object Main : NavigationGraph("main_graph")
    // Graph for operator-related screens
    data object Operator : NavigationGraph("operator_graph")
}