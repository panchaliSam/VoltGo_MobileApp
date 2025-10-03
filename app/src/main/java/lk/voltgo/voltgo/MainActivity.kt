package lk.voltgo.voltgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import lk.chargehere.app.navigation.NavigationGraph
import lk.voltgo.voltgo.navigation.VoltGoNavigation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ChargeHereApp()
        }
    }
}

@Composable
fun ChargeHereApp() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        VoltGoNavigation(
            navController = navController,
            startDestination = NavigationGraph.Auth.route,
            modifier = Modifier.padding(innerPadding)
        )
    }
}