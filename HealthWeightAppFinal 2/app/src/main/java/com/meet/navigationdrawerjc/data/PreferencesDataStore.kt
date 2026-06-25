package com.meet.navigationdrawerjc.data

import android.content.Intent
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.meet.navigationdrawerjc.worker.MyBackgroundService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import java.io.IOException
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// Extension function to create DataStore instance
private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesDataStore(private val context: Context) {

    // Reference to the DataStore
    private val dataStore = context.dataStore

    companion object {
        // Keys used to store/retrieve values in the DataStore
        val MOTIVATION_KEY = intPreferencesKey("motivation")
        val ABILITY_KEY = intPreferencesKey("ability")
        val TRIGGERS_KEY = booleanPreferencesKey("triggers")
    }

    // Function to get the motivation level
    val motivation: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences()) // Handle exceptions and emit default values
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[MOTIVATION_KEY] ?: 0 // Default to 0 (Low) if not set
        }

    // Function to get the ability level
    val ability: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences()) // Handle exceptions and emit default values
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ABILITY_KEY] ?: 0 // Default to 0 (Low) if not set
        }

    // Function to check if triggers are enabled
    val triggers: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences()) // Handle exceptions and emit default values
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[TRIGGERS_KEY] ?: false // Default to false if not set
        }

    init {
        // Observe the triggers Flow and start/stop the background service based on its value
        triggers
            .onEach { isEnabled ->
                if (isEnabled) {
                    startBackgroundService()
                } else {
                    stopBackgroundService()
                }
            }
            .launchIn(CoroutineScope(Dispatchers.IO)) // Use the appropriate scope
    }

    // Function to update motivation level
    suspend fun updateMotivation(value: Int) {
        dataStore.edit { preferences ->
            preferences[MOTIVATION_KEY] = value
        }
    }

    // Function to update ability level
    suspend fun updateAbility(value: Int) {
        dataStore.edit { preferences ->
            preferences[ABILITY_KEY] = value
        }
    }

    // Function to update triggers setting
    suspend fun updateTriggers(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[TRIGGERS_KEY] = enabled
        }
    }

    private fun startBackgroundService() {
        val intent = Intent(context, MyBackgroundService::class.java)
        context.startService(intent)
    }

    private fun stopBackgroundService() {
        val intent = Intent(context, MyBackgroundService::class.java)
        context.stopService(intent)
    }
}