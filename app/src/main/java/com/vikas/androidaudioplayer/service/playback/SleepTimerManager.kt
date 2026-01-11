package com.vikas.androidaudioplayer.service.playback

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepTimerManager @Inject constructor(
    private val playbackController: PlaybackController
) {
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _remainingTime = MutableStateFlow<Long?>(null)
    val remainingTime: StateFlow<Long?> = _remainingTime.asStateFlow()

    fun startTimer(minutes: Int) {
        stopTimer()
        val durationMillis = minutes * 60 * 1000L
        val endTime = System.currentTimeMillis() + durationMillis

        timerJob = scope.launch {
            while (System.currentTimeMillis() < endTime) {
                _remainingTime.value = endTime - System.currentTimeMillis()
                delay(1000)
            }
            playbackController.player.pause()
            stopTimer()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _remainingTime.value = null
    }
}
