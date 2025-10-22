package lk.voltgo.voltgo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SlotDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("reservationDate")
    val reservationDate: String, // ISO 8601 date string

    @SerializedName("startTime")
    val startTime: String, // ISO 8601 datetime string

    @SerializedName("endTime")
    val endTime: String, // ISO 8601 datetime string

    @SerializedName("description")
    val description: String,

    @SerializedName("isAvailable")
    val isAvailable: Boolean,

    @SerializedName("stationId")
    val stationId: String
)