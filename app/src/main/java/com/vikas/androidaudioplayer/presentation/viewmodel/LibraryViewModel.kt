package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikas.androidaudioplayer.domain.model.Album
import com.vikas.androidaudioplayer.domain.model.Artist
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.domain.repository.MediaRepository
import com.vikas.androidaudioplayer.domain.repository.PlaylistRepository
import com.vikas.androidaudioplayer.domain.usecase.ManagePlaylistUseCase
import com.vikas.androidaudioplayer.domain.usecase.PlayTrackUseCase
import com.vikas.androidaudioplayer.domain.usecase.ScanMediaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val playlistRepository: PlaylistRepository,
    private val scanMediaUseCase: ScanMediaUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    private val managePlaylistUseCase: ManagePlaylistUseCase
) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    val tracks: StateFlow<List<AudioTrack>> = mediaRepository.getAllTracks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val albums: StateFlow<List<Album>> = mediaRepository.getAlbums()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val artists: StateFlow<List<Artist>> = mediaRepository.getArtists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val playlists: StateFlow<List<Playlist>> = playlistRepository.getAllPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        scanMedia()
    }

    fun scanMedia() {
        viewModelScope.launch {
            _isScanning.value = true
            try {
                scanMediaUseCase()
            } finally {
                _isScanning.value = false
            }
        }
    }
    
    fun playTrack(track: AudioTrack) {
        val currentTracks = tracks.value
        val index = currentTracks.indexOfFirst { it.id == track.id }
        if (index != -1) {
            playTrackUseCase(currentTracks, index)
        }
    }

    fun addTrackToPlaylist(playlistId: String, track: AudioTrack) {
        viewModelScope.launch {
            managePlaylistUseCase.addTracks(playlistId, listOf(track.id))
        }
    }
}
