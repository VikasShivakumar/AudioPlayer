package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.domain.repository.MediaRepository
import com.vikas.androidaudioplayer.domain.repository.PlaylistRepository
import com.vikas.androidaudioplayer.domain.usecase.PlayTrackUseCase
import com.vikas.androidaudioplayer.service.playback.PlaybackController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val mediaRepository: MediaRepository,
    private val playTrackUseCase: PlayTrackUseCase,
    private val playbackController: PlaybackController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playlistId: String = checkNotNull(savedStateHandle["playlistId"])

    val playlist: StateFlow<Playlist?> = playlistRepository.getPlaylist(playlistId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val tracks: StateFlow<List<AudioTrack>> = playlistRepository.getTracksForPlaylist(playlistId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allPlaylists: StateFlow<List<Playlist>> = playlistRepository.getAllPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTracks: StateFlow<List<AudioTrack>> = mediaRepository.getAllTracks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun playTrack(track: AudioTrack) {
        val currentTracks = tracks.value
        val index = currentTracks.indexOfFirst { it.id == track.id }
        if (index != -1) {
            playTrackUseCase(currentTracks, index)
        }
    }

    fun addTrackToPlaylist(playlistId: String, track: AudioTrack) {
        viewModelScope.launch {
            playlistRepository.addTracksToPlaylist(playlistId, listOf(track.id))
        }
    }

    fun addToQueue(track: AudioTrack) {
        playbackController.addTrackToQueue(track)
    }

    fun playNext(track: AudioTrack) {
        playbackController.playNext(track)
    }

    fun addTracksToCurrentPlaylist(tracks: List<AudioTrack>) {
        viewModelScope.launch {
            playlistRepository.addTracksToPlaylist(playlistId, tracks.map { it.id })
        }
    }
}
