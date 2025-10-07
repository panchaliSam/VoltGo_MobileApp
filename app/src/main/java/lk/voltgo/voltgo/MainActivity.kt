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