package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.vikas.androidaudioplayer.service.playback.PlaybackController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NowPlayingState(
    val mediaItem: MediaItem? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val queue: List<MediaItem> = emptyList(),
    val currentMediaItemIndex: Int = -1
)

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playbackController: PlaybackController
) : ViewModel() {

    private val _uiState = MutableStateFlow(NowPlayingState())
    val uiState: StateFlow<NowPlayingState> = _uiState.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateState()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateState()
        }

        override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
            updateState()
        }
    }

    init {
        playbackController.player.addListener(playerListener)
        updateState()
        startProgressUpdater()
    }

    private fun updateState() {
        val player = playbackController.player
        val queue = mutableListOf<MediaItem>()
        for (i in 0 until player.mediaItemCount) {
            queue.add(player.getMediaItemAt(i))
        }

        _uiState.value = _uiState.value.copy(
            mediaItem = player.currentMediaItem,
            isPlaying = player.isPlaying,
            duration = player.duration.coerceAtLeast(0L),
            queue = queue,
            currentMediaItemIndex = player.currentMediaItemIndex
        )
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                if (playbackController.player.isPlaying) {
                    _uiState.value = _uiState.value.copy(
                        currentPosition = playbackController.player.currentPosition
                    )
                }
                delay(1000)
            }
        }
    }

    fun togglePlayPause() {
        if (playbackController.player.isPlaying) {
            playbackController.player.pause()
        } else {
            playbackController.player.play()
        }
    }

    fun skipToNext() {
        if (playbackController.player.hasNextMediaItem()) {
            playbackController.player.seekToNext()
        }
    }

    fun skipToPrevious() {
        if (playbackController.player.hasPreviousMediaItem()) {
            playbackController.player.seekToPrevious()
        } else {
             playbackController.player.seekTo(0)
        }
    }
    
    fun seekTo(position: Long) {
        playbackController.player.seekTo(position)
        _uiState.value = _uiState.value.copy(currentPosition = position)
    }

    fun playQueueItem(index: Int) {
        playbackController.player.seekTo(index, 0L)
        playbackController.player.play()
    }

    fun removeFromQueue(index: Int) {
        playbackController.removeFromQueue(index)
    }

    fun moveInQueue(from: Int, to: Int) {
        playbackController.moveInQueue(from, to)
    }

    override fun onCleared() {
        playbackController.player.removeListener(playerListener)
        super.onCleared()
    }
}
