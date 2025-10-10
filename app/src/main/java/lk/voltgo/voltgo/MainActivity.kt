/**
 * ------------------------------------------------------------
 * File: MainActivity.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This is the main entry point of the VoltGo app. It initializes the local database,
 * launches the UI using Jetpack Compose, and sets up the main navigation graph.
 * Dagger Hilt is used for dependency injection.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.VoltGoDatabase
import lk.voltgo.voltgo.navigation.NavigationGraph
import lk.voltgo.voltgo.navigation.VoltGoNavigation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Called when the activity is first created; initializes the Room database and sets up UI.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = VoltGoDatabase.getDatabase(applicationContext)

        // 2) Touch a DAO so Room opens the connection (use any simple query)
        lifecycleScope.launch(Dispatchers.IO) {
            val count = db.userDao().countUsers()   // @Query("SELECT COUNT(*) FROM UserEntity")
            Log.d("VoltGo", "User rows: $count")
        }


        enableEdgeToEdge()
        setContent {
            ChargeHereApp()
        }
    }
}

// Composable function that defines the main app layout and navigation graph.
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