package lk.voltgo.voltgo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StationDto(
    @SerializedName("stationId")
    val stationId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("locationLat")
    val locationLat: Double,

    @SerializedName("locationLng")
    val locationLng: Double,

    @SerializedName("type")
    val type: String, // "AC" or "DC"

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("updatedAt")
    val updatedAt: String
)