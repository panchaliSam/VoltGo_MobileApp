package lk.voltgo.voltgo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import lk.voltgo.voltgo.data.local.dao.*
import lk.voltgo.voltgo.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        EVOwnerEntity::class,
        OperatorUserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VoltGoDatabase : RoomDatabase() {

    // --- DAOs ---
    abstract fun userDao(): UserDao
    abstract fun evOwnerDao(): EVOwnerDao
    abstract fun operatorUserDao(): OperatorUserDao

    companion object {
        const val DATABASE_NAME = "voltgo_database"

        @Volatile
        private var INSTANCE: VoltGoDatabase? = null

        fun getDatabase(context: Context): VoltGoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VoltGoDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
