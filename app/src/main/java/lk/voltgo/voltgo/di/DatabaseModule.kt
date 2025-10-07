package lk.voltgo.voltgo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import lk.voltgo.voltgo.data.local.VoltGoDatabase
import lk.voltgo.voltgo.data.local.dao.ChargingStationDao
import lk.voltgo.voltgo.data.local.dao.UserDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VoltGoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            VoltGoDatabase::class.java,
            VoltGoDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideUserDao(database: VoltGoDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideChargingStationDao(db: VoltGoDatabase): ChargingStationDao {
        return db.chargingStationDao()
    }

}