//package lk.voltgo.voltgo.station
//
//import kotlinx.coroutines.flow.Flow
//import lk.voltgo.voltgo.data.local.entities.ChargingStationEntity
//import lk.voltgo.voltgo.data.repository.StationRepository
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class StationManager @Inject constructor(
//    private val stationRepository: StationRepository
//) {
//
//    fun getAllStations(): Flow<List<ChargingStationEntity>> {
//        return stationRepository.getAllStations()
//    }
//
//    suspend fun getStationById(stationId: String): ChargingStationEntity? {
//        return stationRepository.getStationById(stationId)
//    }
//
//    suspend fun searchStations(query: String): List<ChargingStationEntity> {
//        return stationRepository.searchStations(query)
//    }
//}