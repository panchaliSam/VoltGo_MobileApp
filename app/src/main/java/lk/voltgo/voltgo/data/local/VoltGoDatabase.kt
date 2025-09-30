package lk.voltgo.voltgo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import lk.voltgo.voltgo.data.local.dao.*
import lk.voltgo.voltgo.data.local.entities.*
import lk.voltgo.voltgo.data.local.seeders.EVOwnerSeeder
import lk.voltgo.voltgo.data.local.seeders.OperatorUserSeeder
import lk.voltgo.voltgo.data.local.seeders.UserSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.local.seeders.ReservationSeeder

@Database(
    entities = [
        UserEntity::class,
        EVOwnerEntity::class,
        OperatorUserEntity::class,
        ReservationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VoltGoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun evOwnerDao(): EVOwnerDao
    abstract fun operatorUserDao(): OperatorUserDao
    abstract fun reservationDao(): ReservationDao


    companion object {
        const val DATABASE_NAME = "voltgo_database"

        @Volatile
        private var INSTANCE: VoltGoDatabase? = null

        fun getDatabase(context: Context): VoltGoDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create the DB instance variable first so the callback can capture it.
                lateinit var createdInstance: VoltGoDatabase

                createdInstance = Room.databaseBuilder(
                    context.applicationContext,
                    VoltGoDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Use the already-built instance captured above
                            CoroutineScope(Dispatchers.IO).launch {
                                // Seed in a defined order so FKs are satisfied
                                UserSeeder.seed(createdInstance.userDao())
                                EVOwnerSeeder.seed(createdInstance.evOwnerDao())
                                OperatorUserSeeder.seed(createdInstance.operatorUserDao())
                                ReservationSeeder.seed(createdInstance.reservationDao())
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
