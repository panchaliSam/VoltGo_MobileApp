package lk.voltgo.voltgo.data.local.seeders

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.dao.ReservationDao
import lk.voltgo.voltgo.data.local.entities.ReservationEntity

object ReservationSeeder {
    fun seed(reservationDao: ReservationDao) {
        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis().toString()

            val demo = listOf(
                ReservationEntity(
                    reservationId = "R001",
                    ownerId = "U001",                // must exist in user table
                    stationId = "ST001",
                    slotId = "SLOT-01",
                    startTime = "2025-10-01T03:30:00Z",
                    endTime   = "2025-10-01T04:30:00Z",
                    status = "PENDING",
                    serverSynced = false,
                    createdAt = now,
                    updatedAt = now
                ),
                ReservationEntity(
                    reservationId = "R002",
                    ownerId = "U001",
                    stationId = "ST001",
                    slotId = "SLOT-02",
                    startTime = "2025-10-02T03:30:00Z",
                    endTime   = "2025-10-02T04:00:00Z",
                    status = "APPROVED",
                    serverSynced = false,
                    createdAt = now,
                    updatedAt = now
                )
            )

            try {
                reservationDao.insertAll(demo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
