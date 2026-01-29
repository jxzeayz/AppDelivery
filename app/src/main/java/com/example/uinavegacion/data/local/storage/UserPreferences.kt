package com.example.uinavegacion.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//extension o elemento para obtener y manipular el Data Store
val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences (private val context: Context){
    private val isLoggedInKey = booleanPreferencesKey("is_logged_key")
    private val userIdKey = longPreferencesKey("user_id_key")
    private val userRoleKey = stringPreferencesKey("user_role_key")

    suspend fun setLoggedIn(value: Boolean){
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }
    
    suspend fun setUserId(userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[userIdKey] = userId
        }
    }
    
    suspend fun setUserRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[userRoleKey] = role
        }
    }
    
    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(isLoggedInKey)
            prefs.remove(userIdKey)
            prefs.remove(userRoleKey)
        }
    }
    
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[isLoggedInKey] ?: false
        }
    
    val userId: Flow<Long?> = context.dataStore.data
        .map { prefs ->
            prefs[userIdKey]
        }
    
    val userRole: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[userRoleKey]
        }
}