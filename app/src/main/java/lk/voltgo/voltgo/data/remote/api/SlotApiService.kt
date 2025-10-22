package lk.voltgo.voltgo.data.remote.api

import lk.voltgo.voltgo.data.remote.dto.SlotDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SlotApiService {

    @GET("api/Slot/Station/{stationId}")
    suspend fun getSlotsByStation(
        @Path("stationId") stationId: String
    ): Response<List<SlotDto>>
}