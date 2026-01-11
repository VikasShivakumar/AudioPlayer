package com.vikas.androidaudioplayer.data.local.database.dao

import androidx.room.*
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistEntity
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistTrackCrossRef
import com.vikas.androidaudioplayer.data.local.database.entity.AudioTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getPlaylist(id: String): Flow<PlaylistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrackCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)

    @Transaction
    @Query("SELECT * FROM audio_tracks INNER JOIN playlist_tracks ON audio_tracks.id = playlist_tracks.track_id WHERE playlist_tracks.playlist_id = :playlistId ORDER BY playlist_tracks.position ASC")
    fun getTracksForPlaylist(playlistId: String): Flow<List<AudioTrackEntity>>
    
    @Query("SELECT MAX(position) FROM playlist_tracks WHERE playlist_id = :playlistId")
    suspend fun getMaxPosition(playlistId: String): Int?
}
