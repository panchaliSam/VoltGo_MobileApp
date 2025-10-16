package lk.voltgo.voltgo.data.local.seeders

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.dao.UserDao
import lk.voltgo.voltgo.data.local.entities.UserEntity
import lk.voltgo.voltgo.data.remote.types.RoleType
import java.time.Instant

object UserSeeder {
    @RequiresApi(Build.VERSION_CODES.O)
    fun seed(userDao: UserDao) {
        CoroutineScope(Dispatchers.IO).launch {
            if (userDao.countUsers() > 0) return@launch

            val nowIso = Instant.now().toString()

            val demoUsers = listOf(
                UserEntity(
                    userId = "672c2f2f0000000000000001",
                    email = "john.doe@gmail.com",
                    phone = "0771234567",
                    role = RoleType.STATION_OPERATOR,          // matches backend default
                    isActive = true,
                    nic = "901234567V",
                    fullName = "John Doe",
                    address = "123 Main Street, Colombo",
                    createdAt = nowIso,
                    lastLoginAt = null
                ),
                UserEntity(
                    userId = "672c2f2f0000000000000002",
                    email = "operator@example.com",
                    phone = "0779876543",
                    role = RoleType.STATION_OPERATOR,
                    isActive = true,
                    nic = null,
                    fullName = "Operator User",
                    address = "Station Road, Kandy",
                    createdAt = nowIso,
                    lastLoginAt = null
                )
            )

            demoUsers.forEach {
                try { userDao.insertUser(it) } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }
}
