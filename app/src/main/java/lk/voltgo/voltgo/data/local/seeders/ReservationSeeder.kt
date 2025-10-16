package lk.voltgo.voltgo.data.local.seeders

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.dao.ReservationDao
import lk.voltgo.voltgo.data.local.entities.ReservationEntity
import lk.voltgo.voltgo.data.local.utils.DateTimeUtils

object ReservationSeeder {

    /**
     * Seeds demo reservations.
     * Assumes the following already exist due to FK constraints:
     *  - Users:      U001
     *  - Stations:   ST001
     *  - Slots:      SLOT-01, SLOT-02
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun seed(reservationDao: ReservationDao) {
        CoroutineScope(Dispatchers.IO).launch {
            val now = DateTimeUtils.nowIso()

            val demo = listOf(
                ReservationEntity(
                    reservationId = "R001",
                    ownerId = "U001",                 // FK -> user.user_id
                    ownerNic = "993210123V",          // mirrors backend OwnerNIC (optional)
                    stationId = "ST001",              // FK -> charging_station.station_id
                    slotId = "SLOT-01",               // FK -> slot.slot_id
                    startTime = "2025-10-01T03:30:00Z",
                    endTime   = "2025-10-01T04:30:00Z",
                    status = "Pending",               // backend-aligned: Pending/Confirmed/Cancelled/Completed
                    confirmedAt = null,
                    completedAt = null,
                    cancelledAt = null,
                    serverSynced = false,
                    createdAt = now,
                    updatedAt = now
                ),
                ReservationEntity(
                    reservationId = "R002",
                    ownerId = "U001",
                    ownerNic = "993210123V",
                    stationId = "ST001",
                    slotId = "SLOT-02",
                    startTime = "2025-10-02T03:30:00Z",
                    endTime   = "2025-10-02T04:00:00Z",
                    status = "Confirmed",
                    confirmedAt = "2025-09-30T10:00:00Z",
                    completedAt = null,
                    cancelledAt = null,
                    serverSynced = false,
                    createdAt = now,
                    updatedAt = now
                )
            )

            try {
                reservationDao.insertAll(demo)
            } catch (e: Exception) {
                // Ignore if already seeded / FK issues during iterative dev
                e.printStackTrace()
            }
        }
    }
}
