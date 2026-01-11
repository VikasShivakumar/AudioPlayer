package com.vikas.androidaudioplayer.data.local.database.dao

import androidx.room.*
import com.vikas.androidaudioplayer.data.local.database.entity.AudioTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {
    @Query("SELECT * FROM audio_tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<AudioTrackEntity>>

    @Query("SELECT * FROM audio_tracks WHERE id = :id")
    fun getTrackById(id: String): Flow<AudioTrackEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<AudioTrackEntity>)

    @Update
    suspend fun updateTrack(track: AudioTrackEntity)

    @Delete
    suspend fun deleteTrack(track: AudioTrackEntity)
    
    @Query("DELETE FROM audio_tracks WHERE id NOT IN (:ids)")
    suspend fun deleteTracksNotIn(ids: List<String>)
}
