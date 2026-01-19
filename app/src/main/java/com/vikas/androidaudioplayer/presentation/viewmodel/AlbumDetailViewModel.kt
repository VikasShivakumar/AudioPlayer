package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val playlistRepository: PlaylistRepository,
    private val managePlaylistUseCase: ManagePlaylistUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val albumId: String = checkNotNull(savedStateHandle["albumId"])
    
    // Ideally we would have getAlbum(id) in repository, but we can filter from existing lists
    val album: StateFlow<Album?> = mediaRepository.getAlbums()
        .map { albums -> albums.find { it.id == albumId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val tracks: StateFlow<List<AudioTrack>> = combine(
        mediaRepository.getAllTracks(),
        album
    ) { tracks, album ->
        if (album == null) emptyList()
        else tracks.filter { it.album == album.title }
    }.stateIn(
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
