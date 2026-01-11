package com.vikas.androidaudioplayer.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val isDarkTheme: Flow<Boolean>
    suspend fun setDarkTheme(enabled: Boolean)
}
