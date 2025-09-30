package lk.voltgo.voltgo.data.local.seeders

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.dao.EVOwnerDao
import lk.voltgo.voltgo.data.local.entities.EVOwnerEntity

object EVOwnerSeeder {
    fun seed(evOwnerDao: EVOwnerDao) {
        CoroutineScope(Dispatchers.IO).launch {
            val demoOwners = listOf(
                EVOwnerEntity(
                    userId = "U001", // FK to UserEntity
                    address = "123, Colombo Road, Kandy"
                )
            )

            demoOwners.forEach {
                try {
                    evOwnerDao.insertEVOwner(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
