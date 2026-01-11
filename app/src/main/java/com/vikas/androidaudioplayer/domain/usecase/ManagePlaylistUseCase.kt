package com.vikas.androidaudioplayer.domain.usecase

import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.domain.repository.PlaylistRepository
import javax.inject.Inject

class ManagePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend fun createPlaylist(name: String, description: String? = null): Result<Playlist> {
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Playlist name cannot be empty"))
        return playlistRepository.createPlaylist(name, description)
    }

    suspend fun addTracks(playlistId: String, trackIds: List<String>) {
        playlistRepository.addTracksToPlaylist(playlistId, trackIds)
    }
}
