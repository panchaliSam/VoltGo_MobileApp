package lk.voltgo.voltgo.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
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

    // Fetch all stations directly from the API
    fun getAllStations(): Flow<List<ChargingStationEntity>> = flow {
        try {
            val response = stationApiService.getAllStations()
            if (response.isSuccessful) {
                val dtoList = response.body().orEmpty()

                val entityList = dtoList.map { dto ->
                    ChargingStationEntity(
                        id = dto.id,                                   // <-- changed from stationId
                        name = dto.name,
                        type = dto.type,                                // nullable ok if entity allows null
                        location = dto.location,                        // nullable ok if entity allows null
                        latitude = dto.latitude,                        // nullable ok if entity allows null
                        longitude = dto.longitude,                      // nullable ok if entity allows null
                        availableSlots = dto.availableSlots ?: 0,       // safe fallback
                        isActive = dto.isActive
                    )
                }

                withContext(Dispatchers.IO) {
                    stationDao.insertStations(entityList)
                    val ids = entityList.map { it.id }
                    if (ids.isNotEmpty()) {
                        stationDao.deleteStationsNotIn(ids)
                    }
                }

                emit(entityList)
            } else {
                // On error, emit cached data
                emit(emitCachedActive())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // On exception, emit cached data
            emit(emitCachedActive())
        }
    }

    private suspend fun emitCachedActive(): List<ChargingStationEntity> =
        withContext(Dispatchers.IO) {
            // Use whatever local fallback you prefer
            stationDao.searchStations("%")
                .ifEmpty { emptyList() }
        }

    // DB-observed active list (if you already have this DAO query)
    fun observeActiveStations(): Flow<List<ChargingStationEntity>> =
        stationDao.observeActiveStations()

    // Fetch a single station by ID (local first; fallback to network)
    suspend fun getStationById(stationId: String): ChargingStationEntity? {
        return try {
            stationDao.getStationById(stationId) ?: run {
                val response = stationApiService.getStationById(stationId)
                if (response.isSuccessful) {
                    response.body()?.let { dto ->
                        val entity = ChargingStationEntity(
                            id = dto.id,                                 // <-- changed from stationId
                            name = dto.name,
                            type = dto.type,
                            location = dto.location,
                            latitude = dto.latitude,
                            longitude = dto.longitude,
                            availableSlots = dto.availableSlots ?: 0,
                            isActive = dto.isActive
                        )
                        withContext(Dispatchers.IO) { stationDao.insertStation(entity) }
                        entity
                    }
                } else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Last fallback: whatever is in DB
            stationDao.getStationById(stationId)
        }
    }

    // Search stations from API response (simple client-side filter)
    suspend fun searchStations(query: String): List<ChargingStationEntity> {
        return try {
            val response = stationApiService.getAllStations()
            if (response.isSuccessful) {
                response.body()
                    ?.filter { it.name.contains(query, ignoreCase = true) }
                    ?.map { dto ->
                        ChargingStationEntity(
                            id = dto.id,                                 // <-- changed from stationId
                            name = dto.name,
                            type = dto.type,
                            location = dto.location,
                            latitude = dto.latitude,
                            longitude = dto.longitude,
                            availableSlots = dto.availableSlots ?: 0,
                            isActive = dto.isActive
                        )
                    } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}