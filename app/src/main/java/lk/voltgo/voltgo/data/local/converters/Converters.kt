/**
 * ------------------------------------------------------------
 * File: Converters.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-24
 * Description: Room type converters for enums and (optionally) Instant.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import lk.voltgo.voltgo.data.remote.types.StatusType
import java.time.Instant
import java.time.format.DateTimeFormatter

class Converters {

    // ---- StatusType <-> String ----
    @TypeConverter
    fun fromStatus(value: StatusType?): String? = value?.name

    @TypeConverter
    fun toStatus(value: String?): StatusType? = value?.let { StatusType.valueOf(it) }

    // ---- Optional: Instant <-> ISO-8601 String (Z) ----
    @TypeConverter
    fun fromInstant(value: Instant?): String? = value?.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toInstant(value: String?): Instant? = value?.let { Instant.parse(it) }
}