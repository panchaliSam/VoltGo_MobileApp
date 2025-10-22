package lk.voltgo.voltgo.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import lk.voltgo.voltgo.data.remote.api.SlotApiService
import lk.voltgo.voltgo.data.remote.dto.SlotDto

@Singleton
class SlotRepository @Inject constructor(
    private val api: SlotApiService
) {
    suspend fun getSlotsByStation(stationId: String): List<SlotDto> {
        val res = api.getSlotsByStation(stationId)
        if (res.isSuccessful) return res.body().orEmpty()
        return emptyList()
    }
}