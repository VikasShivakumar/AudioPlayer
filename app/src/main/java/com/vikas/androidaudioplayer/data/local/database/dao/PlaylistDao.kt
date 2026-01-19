package com.vikas.androidaudioplayer.data.local.database.dao

import androidx.room.*
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistEntity
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistTrackCrossRef
import com.vikas.androidaudioplayer.data.local.database.entity.AudioTrackEntity
import kotlinx.coroutines.flow.Flow

import com.vikas.androidaudioplayer.data.local.database.model.PlaylistWithCount

@Dao
interface PlaylistDao {
    @Query("""
        SELECT *, (SELECT COUNT(*) FROM playlist_tracks WHERE playlist_id = playlists.id) as track_count 
        FROM playlists 
        ORDER BY name ASC
    """)
    fun getAllPlaylists(): Flow<List<PlaylistWithCount>>

    @Query("""
        SELECT *, (SELECT COUNT(*) FROM playlist_tracks WHERE playlist_id = :id) as track_count 
        FROM playlists 
        WHERE id = :id
    """)
    fun getPlaylist(id: String): Flow<PlaylistWithCount?>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrackCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM audio_tracks INNER JOIN playlist_tracks ON audio_tracks.id = playlist_tracks.track_id WHERE playlist_tracks.playlist_id = :playlistId ORDER BY playlist_tracks.position ASC")
    fun getTracksForPlaylist(playlistId: String): Flow<List<AudioTrackEntity>>
    
    @Query("SELECT MAX(position) FROM playlist_tracks WHERE playlist_id = :playlistId")
    suspend fun getMaxPosition(playlistId: String): Int?
}
