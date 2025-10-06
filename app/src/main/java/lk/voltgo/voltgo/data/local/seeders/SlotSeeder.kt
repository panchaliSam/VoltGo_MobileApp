package lk.voltgo.voltgo.data.local.seeders

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.dao.SlotDao
import lk.voltgo.voltgo.data.local.entities.SlotEntity
import java.sql.Date

object SlotSeeder {
    fun seed(slotDao: SlotDao) {
        CoroutineScope(Dispatchers.IO).launch {
            val demoSlots = listOf(
                SlotEntity(
                    slotId = "SLOT-001",
                    startTime = "2025-10-01T08:00:00Z",
                    endTime = "2025-10-01T09:00:00Z",
                    reservationDate = "2025-10-01"
                ),
                SlotEntity(
                    slotId = "SLOT-002",
                    startTime = "2025-10-01T09:30:00Z",
                    endTime = "2025-10-01T10:30:00Z",
                    reservationDate = "2025-10-01"
                ),
                SlotEntity(
                    slotId = "SLOT-003",
                    startTime = "2025-10-02T11:00:00Z",
                    endTime = "2025-10-02T12:00:00Z",
                    reservationDate = "2025-10-02"
                ),
                SlotEntity(
                    slotId = "SLOT-004",
                    startTime = "2025-10-03T14:00:00Z",
                    endTime = "2025-10-03T15:00:00Z",
                    reservationDate = "2025-10-03"
                )
            )

            try {
                slotDao.insertAll(demoSlots)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
