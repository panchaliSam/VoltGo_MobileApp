package lk.voltgo.voltgo.navigation

sealed class Screen(val route: String) {
    // Authentication Flow
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Profile : Screen("profile")

    // Main Flow - EV Owner
    data object Home : Screen("home")
    data object MyReservations : Screen("my_reservations")
    data object NewReservation : Screen("new_reservation")
    data object Stations : Screen("stations")

    // Operator Flow
    data object OperatorHome : Screen("operator_home")

}
// Navigation graphs
sealed class NavigationGraph(val route: String) {
    data object Auth : NavigationGraph("auth_graph")
    data object Main : NavigationGraph("main_graph")
    data object Operator : NavigationGraph("operator_graph")
}