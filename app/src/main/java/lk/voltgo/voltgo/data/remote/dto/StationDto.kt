package lk.voltgo.voltgo.data.remote.dto

import com.google.gson.annotations.SerializedName


data class StationDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("availableSlots") val availableSlots: Int?,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("physicalSlots") val physicalSlots: List<StationPhysicalSlotDto> = emptyList()
)

data class StationPhysicalSlotDto(
    @SerializedName("number") val number: Int,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("label") val label: String?,
    @SerializedName("connectorType") val connectorType: String?,
    @SerializedName("maxKw") val maxKw: Int?
)