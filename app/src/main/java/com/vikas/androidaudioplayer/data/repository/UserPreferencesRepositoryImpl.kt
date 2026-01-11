package com.vikas.androidaudioplayer.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.vikas.androidaudioplayer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")

    override val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_THEME] ?: false // Default to system (handled in UI usually, but here defaulting to false if not set or we can use a tri-state)
            // For now, let's say false. In real app, we might want to return null if not set to follow system.
        }

    override suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = enabled
        }
    }
}
