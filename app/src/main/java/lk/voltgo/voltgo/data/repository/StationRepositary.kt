package lk.voltgo.voltgo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import lk.voltgo.voltgo.data.local.dao.ChargingStationDao
import lk.voltgo.voltgo.data.local.entities.ChargingStationEntity
import lk.voltgo.voltgo.data.remote.api.StationApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(
    private val stationApiService: StationApiService,
    private val stationDao: ChargingStationDao
) {

    /**
     * Fetch all stations from API and update local DB.
     * Returns local cached data as Flow for observing in UI.
     */
    fun getAllStations(): Flow<List<ChargingStationEntity>> = flow {
        try {
            // 1. Emit local data first (cached)
            emit(stationDao.observeActiveStations().first())

            // 2. Fetch fresh data from API
            val response = stationApiService.getAllStations()
            if (response.isSuccessful) {
                response.body()?.let { stations ->
                    // Convert API DTOs to entities if needed
                    val entities = stations.map {
                        ChargingStationEntity(
                            id = it.stationId,
                            name = it.name,
                            type = it.type,
                            location = it.location,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            availableSlots = it.availableSlots,
                            isActive = it.isActive
                        )
                    }
                    // Save to Room
                    stationDao.insertAll(entities)
                    emit(entities)
                }
            } else {
                // Fallback: emit cached data
                emit(stationDao.observeActiveStations().first())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(stationDao.observeActiveStations().first())
        }
    }

    /**
     * Get single station by ID from API or local DB.
     */
    suspend fun getStationById(stationId: String): ChargingStationEntity? {
        return try {
            val response = stationApiService.getStationById(stationId)
            if (response.isSuccessful) {
                response.body()?.let {
                    val entity = ChargingStationEntity(
                        id = it.stationId,
                        name = it.name,
                        type = it.type,
                        location = it.location,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        availableSlots = it.availableSlots,
                        isActive = it.isActive
                    )
                    stationDao.insertStation(entity)
                    entity
                }
            } else {
                stationDao.getStationById(stationId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stationDao.getStationById(stationId)
        }
    }

    /**
     * Local-only search by name or type.
     */
    suspend fun searchStations(query: String): List<ChargingStationEntity> {
        return stationDao.searchStations("%$query%")
    }
}