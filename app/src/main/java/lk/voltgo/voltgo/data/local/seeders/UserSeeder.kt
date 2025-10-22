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
                    userId = "68ef2fd4f26d7d9cd2d49221",
                    email = "panchali@email.com",
                    phone = "+94707789098",
                    role = RoleType.EV_OWNER,          // matches backend default
                    isActive = true,
                    nic = "123456788V",
                    fullName = "Panchali Samarasinghe",
                    address = "123 Main Street, Colombo 07",
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
