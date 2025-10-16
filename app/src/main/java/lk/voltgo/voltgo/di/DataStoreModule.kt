/**
 * ------------------------------------------------------------
 * File: DataStoreModule.kt
 * Authors: Ishini Aposo & Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This Dagger Hilt module provides the singleton instance of the Android Jetpack DataStore
 * used for managing and persisting user preferences across the VoltGo app. It replaces
 * SharedPreferences and ensures type-safe, asynchronous data storage.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // Provides a singleton instance of the DataStore for storing user preferences.
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES_NAME) }
        )
    }
}
