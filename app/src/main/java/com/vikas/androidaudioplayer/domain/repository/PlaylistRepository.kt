package com.vikas.androidaudioplayer.domain.repository

import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylist(id: String): Flow<Playlist?>
    fun getTracksForPlaylist(playlistId: String): Flow<List<AudioTrack>>
    suspend fun createPlaylist(name: String, description: String?): Result<Playlist>
    suspend fun addTracksToPlaylist(playlistId: String, trackIds: List<String>)
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)
    suspend fun reorderPlaylistTracks(playlistId: String, fromIndex: Int, toIndex: Int)
    suspend fun deletePlaylist(playlistId: String)
}
