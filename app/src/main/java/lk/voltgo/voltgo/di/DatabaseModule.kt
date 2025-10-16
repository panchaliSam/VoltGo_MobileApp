/**
 * ------------------------------------------------------------
 * File: DatabaseModule.kt
 * Authors: Ishini Aposo & Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This Dagger Hilt module provides the dependencies for the local Room database
 * and its DAO instances used across the VoltGo app. It ensures singleton
 * instances of the database and data access objects are available for injection.
 * ------------------------------------------------------------
 */

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

    // Provides the singleton instance of the VoltGo Room database.
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VoltGoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            VoltGoDatabase::class.java,
            VoltGoDatabase.DATABASE_NAME
        ).build()
    }

    // Provides the DAO for accessing user-related data from the database.
    @Provides
    fun provideUserDao(database: VoltGoDatabase): UserDao {
        return database.userDao()
    }

    // Provides the DAO for accessing charging station data from the database.
    @Provides
    fun provideChargingStationDao(db: VoltGoDatabase): ChargingStationDao {
        return db.chargingStationDao()
    }

}