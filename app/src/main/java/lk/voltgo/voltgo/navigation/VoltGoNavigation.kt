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

@Composable
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
        // Authentication Graph
        navigation(
            startDestination = Screen.Splash.route,
            route = NavigationGraph.Auth.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { token: String ->
                        navController.navigate(NavigationGraph.Main.route) {
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
            startDestination = Screen.Home.route,
            route = NavigationGraph.Main.route       // "main_graph"
        ) {
            composable(Screen.Home.route) {
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
                CreateReservationScreen(
                    onBackClick = { navController.popBackStack() },
                    onOpenMap = { navController.popBackStack() },
                    onSubmit = { _ ->
                        // TODO: Handle cancel flow or navigate to a confirmation dialog/screen
                    }
                )
            }
            composable(Screen.Stations.route) {
                StationsScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }
            composable(Screen.Profile.route) {
                EditProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}