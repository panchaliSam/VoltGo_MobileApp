/**
 * ------------------------------------------------------------
 * File: TokenManager.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * Manages authentication token storage and retrieval using Android DataStore.
 * Provides functions to save, fetch, and clear the token for authenticated sessions.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

@Singleton
class TokenManager @Inject constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        val AUTH_TOKEN = stringPreferencesKey("token")
    }

    // Saves the provided authentication token into DataStore preferences.
    suspend fun saveToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }

    // Retrieves the saved authentication token from DataStore; returns null if absent.
    suspend fun getToken(): String? {
        return dataStore.data.map { it[AUTH_TOKEN] }.firstOrNull()
    }

    // Clears the stored authentication token from DataStore preferences.
    suspend fun clearToken() {
        dataStore.edit { it.remove(AUTH_TOKEN) }
    }
}