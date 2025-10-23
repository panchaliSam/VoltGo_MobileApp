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
                        id = dto.stationId,
                        name = dto.name,
                        type = dto.type,
                        location = dto.location,
                        latitude = dto.latitude,
                        longitude = dto.longitude,
                        availableSlots = dto.availableSlots,
                        isActive = dto.isActive
                    )
                }

                // Persist to SQLite (transactional on IO)
                withContext(Dispatchers.IO) {
                    stationDao.insertStations(entityList) // REPLACE on conflict

                    // Optional strict mirror: remove rows not in latest payload
                    val ids = entityList.map { it.id }
                    if (ids.isNotEmpty()) {
                        stationDao.deleteStationsNotIn(ids)
                    }
                }

                // Emit what we saved
                emit(entityList)
            } else {
                // On error, emit cached data (active stations) as fallback
                emitCachedActive()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // On exception, emit cached data (active stations) as fallback
            emitCachedActive()
        }
    }

    private suspend fun emitCachedActive(): List<ChargingStationEntity> =
        withContext(Dispatchers.IO) {
            // Query DB directly (one-shot). If you prefer a reactive stream, expose dao.observeActiveStations().
            stationDao.searchStations("%") // or create a dao.getAllActive() if you prefer
                .ifEmpty {
                    // If you want strictly active only:
                    // stationDao.getAllActive()
                    emptyList()
                }
        }

    // If you want a reactive stream of DB changes elsewhere:
    fun observeActiveStations(): Flow<List<ChargingStationEntity>> =
        stationDao.observeActiveStations()

    // Fetch a single station by ID (prefer local first; fallback to network)
    suspend fun getStationById(stationId: String): ChargingStationEntity? {
        return try {
            // Try local first
            stationDao.getStationById(stationId) ?: run {
                // Fallback to network, persist, return
                val response = stationApiService.getStationById(stationId)
                if (response.isSuccessful) {
                    response.body()?.let { dto ->
                        val entity = ChargingStationEntity(
                            id = dto.stationId,
                            name = dto.name,
                            type = dto.type,
                            location = dto.location,
                            latitude = dto.latitude,
                            longitude = dto.longitude,
                            availableSlots = dto.availableSlots,
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

    // Search stations from API response
    suspend fun searchStations(query: String): List<ChargingStationEntity> {
        return try {
            val response = stationApiService.getAllStations()
            if (response.isSuccessful) {
                response.body()
                    ?.filter { it.name.contains(query, ignoreCase = true) }
                    ?.map { dto ->
                        ChargingStationEntity(
                            id = dto.stationId,
                            name = dto.name,
                            type = dto.type,
                            location = dto.location,
                            latitude = dto.latitude,
                            longitude = dto.longitude,
                            availableSlots = dto.availableSlots,
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