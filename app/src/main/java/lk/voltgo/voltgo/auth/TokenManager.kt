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

    suspend fun saveToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { it[AUTH_TOKEN] }.firstOrNull()
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(AUTH_TOKEN) }
    }
}