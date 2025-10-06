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
                    isActive = true,
                    latitude = 6.9271,
                    longitude = 79.8612
                ),
                ChargingStationEntity(
                    id = "ST002",
                    location = "Kandy - City Center",
                    type = "DC",
                    availableSlots = 2,
                    isActive = true,
                    latitude = 7.2906,
                    longitude = 80.6337
                ),
                ChargingStationEntity(
                    id = "ST003",
                    location = "Galle - Marine Drive",
                    type = "AC",
                    availableSlots = 3,
                    isActive = false,
                    latitude = 6.0535,
                    longitude = 80.2210
                )
            )

            try {
                chargingStationDao.insertStations(demoStations)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
