package lk.voltgo.voltgo.data.local

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import lk.voltgo.voltgo.data.local.dao.*
import lk.voltgo.voltgo.data.local.entities.*
import lk.voltgo.voltgo.data.local.seeders.EVOwnerSeeder
import lk.voltgo.voltgo.data.local.seeders.UserSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.seeders.ChargingStationSeeder
import lk.voltgo.voltgo.data.local.seeders.ReservationSeeder
import lk.voltgo.voltgo.data.local.seeders.SlotSeeder

@Database(
    entities = [
        UserEntity::class,
        EVOwnerEntity::class,
        ReservationEntity::class,
        ChargingStationEntity::class,
        SlotEntity::class
    ],
    version = 1,                 // keep as 1 since fallbackToDestructiveMigration is enabled
    exportSchema = false
)
abstract class VoltGoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun evOwnerDao(): EVOwnerDao
    abstract fun reservationDao(): ReservationDao
    abstract fun chargingStationDao(): ChargingStationDao
    abstract fun slotDao(): SlotDao


    companion object {
        const val DATABASE_NAME = "voltgo_database"

        @Volatile
        private var INSTANCE: VoltGoDatabase? = null

        fun getDatabase(context: Context): VoltGoDatabase {
            return INSTANCE ?: synchronized(this) {
                lateinit var createdInstance: VoltGoDatabase

                createdInstance = Room.databaseBuilder(
                    context.applicationContext,
                    VoltGoDatabase::class.java,
                    DATABASE_NAME
                )
                    // NOTE: destructive migration wipes data on schema change; fine for dev/demo.
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                // Seed in order (FK safety)
                                UserSeeder.seed(createdInstance.userDao())
                                EVOwnerSeeder.seed(createdInstance.evOwnerDao())
                                ReservationSeeder.seed(createdInstance.reservationDao())
                                ChargingStationSeeder.seed(createdInstance.chargingStationDao())
                                SlotSeeder.seed( createdInstance.slotDao())
                            }
                        }
                    })
                    .build()

                INSTANCE = createdInstance
                createdInstance
            }
        }
    }
}
