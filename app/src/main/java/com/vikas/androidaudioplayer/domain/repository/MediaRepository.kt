package com.vikas.androidaudioplayer.domain.repository

import com.vikas.androidaudioplayer.domain.model.*
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAllTracks(): Flow<List<AudioTrack>>
    fun getAlbums(): Flow<List<Album>>
    fun getArtists(): Flow<List<Artist>>
    fun getTrackById(id: String): Flow<AudioTrack?>
    suspend fun scanMedia(): Result<Unit>
}
