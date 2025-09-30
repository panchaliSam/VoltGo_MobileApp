package lk.voltgo.voltgo

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.VoltGoDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔑 This actually opens the DB (creating tables on first run + triggering the seeder)
        val db = VoltGoDatabase.getDatabase(applicationContext)

        // Optional: touch a DAO to force open + verify
        CoroutineScope(Dispatchers.IO).launch {
            val count = db.userDao().countUsers() // add a simple @Query("SELECT COUNT(*) FROM UserEntity") in UserDao
            Log.d("VoltGo", "User rows: $count")
        }
    }
}
