package lk.voltgo.voltgo.data.local.seeders

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.dao.ChargingStationDao
import lk.voltgo.voltgo.data.local.entities.ChargingStationEntity

object ChargingStationSeeder {
    fun seed(chargingStationDao: ChargingStationDao) {
        CoroutineScope(Dispatchers.IO).launch {
            val demoStations = listOf(
                ChargingStationEntity(
                    id = "ST001",
                    location = "Colombo - Main Street",
                    type = "AC",
                    availableSlots = 4,
                    isActive = true
                ),
                ChargingStationEntity(
                    id = "ST002",
                    location = "Kandy - City Center",
                    type = "DC",
                    availableSlots = 2,
                    isActive = true
                ),
                ChargingStationEntity(
                    id = "ST003",
                    location = "Galle - Marine Drive",
                    type = "AC",
                    availableSlots = 3,
                    isActive = false // inactive for testing
                )
            )

            try {
                chargingStationDao.insertAll(demoStations)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
