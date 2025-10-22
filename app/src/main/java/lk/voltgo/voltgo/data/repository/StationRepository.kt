package lk.voltgo.voltgo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import lk.voltgo.voltgo.data.local.entities.ChargingStationEntity
import lk.voltgo.voltgo.data.remote.api.StationApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(
    private val stationApiService: StationApiService
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
                emit(entityList)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    // Fetch a single station by ID
    suspend fun getStationById(stationId: String): ChargingStationEntity? {
        return try {
            val response = stationApiService.getStationById(stationId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
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
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
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