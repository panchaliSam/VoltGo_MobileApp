package lk.voltgo.voltgo.navigation

sealed class Screen(val route: String) {
    // Authentication Flow
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")

    // Main Flow - EV Owner
    data object Home : Screen("home")
    data object MyReservations : Screen("my_reservations")

    // Operator Flow

}
// Navigation graphs
sealed class NavigationGraph(val route: String) {
    data object Auth : NavigationGraph("auth_graph")
    data object Main : NavigationGraph("main_graph")
    data object Operator : NavigationGraph("operator_graph")
}