package lk.voltgo.voltgo.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import lk.voltgo.voltgo.data.remote.dto.StationDto

interface StationApiService {

    @GET("/api/stations")
    suspend fun getAllStations(): Response<List<StationDto>>

    @GET("/api/stations/{id}")
    suspend fun getStationById(@Path("id") id: String): Response<StationDto>
}