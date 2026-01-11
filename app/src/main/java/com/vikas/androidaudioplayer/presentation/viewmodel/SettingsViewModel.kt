package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikas.androidaudioplayer.domain.repository.UserPreferencesRepository
import com.vikas.androidaudioplayer.service.playback.SleepTimerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val sleepTimerManager: SleepTimerManager
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = userPreferencesRepository.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
        
    val remainingSleepTime: StateFlow<Long?> = sleepTimerManager.remainingTime
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkTheme(enabled)
        }
    }
    
    fun setSleepTimer(minutes: Int) {
        sleepTimerManager.startTimer(minutes)
    }
    
    fun cancelSleepTimer() {
        sleepTimerManager.stopTimer()
    }
}
