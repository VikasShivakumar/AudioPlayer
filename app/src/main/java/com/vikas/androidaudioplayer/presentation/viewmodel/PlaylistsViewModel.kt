package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.domain.repository.PlaylistRepository
import com.vikas.androidaudioplayer.domain.usecase.ManagePlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val managePlaylistUseCase: ManagePlaylistUseCase
) : ViewModel() {

    val playlists: StateFlow<List<Playlist>> = playlistRepository.getAllPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            managePlaylistUseCase.createPlaylist(name)
        }
    }
}
