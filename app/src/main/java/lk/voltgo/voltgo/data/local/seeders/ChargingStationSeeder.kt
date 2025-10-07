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
                    name = "Colombo Main Station",
                    type = "AC",
                    location = "Colombo - Main Street",
                    latitude = 6.9271,
                    longitude = 79.8612,
                    availableSlots = 4,
                    isActive = true
                ),
                ChargingStationEntity(
                    id = "ST002",
                    name = "Kandy City Station",
                    type = "DC",
                    location = "Kandy - City Center",
                    latitude = 7.2906,
                    longitude = 80.6337,
                    availableSlots = 2,
                    isActive = true
                ),
                ChargingStationEntity(
                    id = "ST003",
                    name = "Galle Marine Station",
                    type = "AC",
                    location = "Galle - Marine Drive",
                    latitude = 6.0535,
                    longitude = 80.2210,
                    availableSlots = 3,
                    isActive = false
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
