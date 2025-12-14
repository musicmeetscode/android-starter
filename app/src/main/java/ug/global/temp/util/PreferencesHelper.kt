package ug.global.temp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Extension property to create DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * PreferencesHelper - Manages DataStore preferences
 * 
 * Replaces old SharedPreferences. Uses Coroutines and Flow.
 */
@Singleton
class PreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    // Keys
    companion object {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_LAST_SYNC = longPreferencesKey("last_sync_timestamp")
    }
    
    // ==================== SAVE OPERATIONS ====================
    
    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = isLoggedIn
        }
    }
    
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
        }
    }
    
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
        }
    }
    
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    
    // ==================== READ OPERATIONS (FLOW) ====================
    
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_IS_LOGGED_IN] ?: false
        }
        
    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }
        
    val userId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USER_ID]
        }
        
    
    // ==================== SYNCHRONOUS READ (Use sparingly) ====================
    
    suspend fun getAuthTokenSync(): String? {
        return authToken.first()
    }
}
