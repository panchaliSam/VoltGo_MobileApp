package lk.voltgo.voltgo.data.local.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

object DateTimeUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun nowIso(): String = Instant.now().toString() // e.g., 2025-10-10T04:15:30Z
}
