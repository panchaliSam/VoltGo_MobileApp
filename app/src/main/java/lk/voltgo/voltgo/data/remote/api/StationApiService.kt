/**
 * ------------------------------------------------------------
 * File: StationApiService.kt
 * Author: Ishini Aposo
 * Date: 10 oct 2025
 *
 * Description:
 * This interface defines the Retrofit API endpoints used to interact
 * with the backend service for retrieving charging station data.
 * It includes methods for fetching all stations and fetching details
 * of a specific station by ID.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import lk.voltgo.voltgo.data.remote.dto.StationDto

interface StationApiService {

    // Fetches the complete list of all charging stations from the backend.
    @GET("/api/stations")
    suspend fun getAllStations(): Response<List<StationDto>>

    // Fetches the details of a specific charging station by its unique ID.
    @GET("/api/stations/{id}")
    suspend fun getStationById(@Path("id") id: String): Response<StationDto>
}