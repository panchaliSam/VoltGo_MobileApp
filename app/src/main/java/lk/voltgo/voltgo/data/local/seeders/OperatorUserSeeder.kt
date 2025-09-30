package lk.voltgo.voltgo.data.local.seeders

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.dao.OperatorUserDao
import lk.voltgo.voltgo.data.local.entities.OperatorUserEntity

object OperatorUserSeeder {
    fun seed(operatorUserDao: OperatorUserDao) {
        CoroutineScope(Dispatchers.IO).launch {
            val demoOperators = listOf(
                OperatorUserEntity(
                    userId = "U002", // FK to UserEntity
                    stationId = "ST001",
                    employeeCode = "EMP-001"
                )
            )

            demoOperators.forEach {
                try {
                    operatorUserDao.insertOperator(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
