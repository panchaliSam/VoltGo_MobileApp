package lk.voltgo.voltgo.station

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lk.voltgo.voltgo.data.local.entities.ChargingStationEntity
import lk.voltgo.voltgo.data.repository.StationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationManager @Inject constructor(
    private val stationRepository: StationRepository
) {

    // Returns all stations from repository, mapped correctly
    fun getAllStations(): Flow<List<ChargingStationEntity>> {
        return stationRepository.getAllStations()
            .map { list ->
                list.filter { it.isActive } // optional: only show active
            }
    }

    suspend fun getStationById(stationId: String): ChargingStationEntity? {
        return stationRepository.getStationById(stationId)
    }

    // Remove search from repository; do it in ViewModel instead
    suspend fun searchStations(query: String, stations: List<ChargingStationEntity>): List<ChargingStationEntity> {
        return if (query.isBlank()) stations
        else stations.filter { it.name.contains(query, ignoreCase = true) }
    }
}
