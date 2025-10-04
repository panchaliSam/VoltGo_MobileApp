package lk.voltgo.voltgo.data.local.seeders

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.dao.UserDao
import lk.voltgo.voltgo.data.local.entities.UserEntity

object UserSeeder {
    fun seed(userDao: UserDao) {
        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis().toString()

            val demoUsers = listOf(
                UserEntity(
                    userId = "U001",
                    displayName = "John",
                    phone = "0771234567",
                    email = "johnDeoe@gmail.com",
                    role = "EV_OWNER",
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                ),
                UserEntity(
                    userId = "U002",
                    displayName = "Operator User",
                    phone = "0779876543",
                    email = "operator@example.com",
                    role = "OPERATOR",
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )
            )

            demoUsers.forEach {
                try {
                    userDao.insertUser(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
