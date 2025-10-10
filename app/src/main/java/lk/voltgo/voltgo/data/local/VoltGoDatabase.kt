/**
 * ------------------------------------------------------------
 * File: VoltGoDatabase.kt
 * Authors: Panchali Samarasinghe, Ishini Aponso
 * Date: 2025-10-10
 *
 * Description:
 * This class defines the Room database configuration for the VoltGo app. It serves as the
 * main access point for the app’s persisted data, linking entity classes with their
 * respective DAOs. It also includes logic to build the database instance and pre-seed
 * initial data when the database is first created.
 * ------------------------------------------------------------
 */
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

    // Provides access to the User DAO for user-related operations.
    abstract fun userDao(): UserDao

    // Provides access to the EV Owner DAO for managing EV owner data.
    abstract fun evOwnerDao(): EVOwnerDao

    // Provides access to the Reservation DAO for reservation management.
    abstract fun reservationDao(): ReservationDao

    // Provides access to the Charging Station DAO for station data operations.
    abstract fun chargingStationDao(): ChargingStationDao

    // Provides access to the Slot DAO for slot availability management.
    abstract fun slotDao(): SlotDao


    companion object {
        const val DATABASE_NAME = "voltgo_database"

        @Volatile
        private var INSTANCE: VoltGoDatabase? = null

        // Returns a singleton instance of the VoltGo database, creating it if not already initialized.
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
