//package lk.voltgo.voltgo.data.repository
//
//import lk.voltgo.voltgo.data.remote.api.SlotApiService
//import lk.voltgo.voltgo.data.local.entities.SlotEntity
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class SlotsRepository @Inject constructor(
//    private val slotApiService: SlotApiService
//) {
//    suspend fun getSlotsByStationId(stationId: String): List<SlotEntity> {
//        return try {
//            val response = slotApiService.getSlotsByStation(stationId)
//            if (response.isSuccessful) {
//                response.body()?.map {
//                    SlotEntity(
//                        id = it.id,
//                        stationId = it.stationId,
//                        reservationDate = it.reservationDate,
//                        startTime = it.startTime,
//                        endTime = it.endTime,
//                        description = it.description,
//                        isAvailable = it.isAvailable
//                    )
//                } ?: emptyList()
//            } else emptyList()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emptyList()
//        }
//    }
//}