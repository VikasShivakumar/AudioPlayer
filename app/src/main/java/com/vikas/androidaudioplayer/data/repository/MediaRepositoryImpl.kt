package com.vikas.androidaudioplayer.data.repository

import com.vikas.androidaudioplayer.data.local.database.dao.AudioDao
import com.vikas.androidaudioplayer.data.mapper.toDomain
import com.vikas.androidaudioplayer.data.mapper.toEntity
import com.vikas.androidaudioplayer.data.source.LocalMediaSource
import com.vikas.androidaudioplayer.domain.model.Album
import com.vikas.androidaudioplayer.domain.model.Artist
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val localMediaSource: LocalMediaSource,
    private val audioDao: AudioDao
) : MediaRepository {

    override fun getAllTracks(): Flow<List<AudioTrack>> {
        return audioDao.getAllTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAlbums(): Flow<List<Album>> {
        return audioDao.getAllTracks().map { entities ->
            entities.groupBy { it.album }.map { (albumName, tracks) ->
                val firstTrack = tracks.first()
                Album(
                    id = firstTrack.album.hashCode().toString(), // Simple ID generation
                    title = albumName,
                    artist = firstTrack.artist,
                    year = firstTrack.year,
                    trackCount = tracks.size,
                    artworkUri = firstTrack.albumArtUri,
                    duration = tracks.sumOf { it.duration },
                    genre = firstTrack.genre
                )
            }.sortedBy { it.title }
        }
    }

    override fun getArtists(): Flow<List<Artist>> {
        return audioDao.getAllTracks().map { entities ->
            entities.groupBy { it.artist }.map { (artistName, tracks) ->
                Artist(
                    id = artistName.hashCode().toString(),
                    name = artistName,
                    albumCount = tracks.distinctBy { it.album }.size,
                    trackCount = tracks.size,
                    artworkUri = tracks.firstOrNull()?.albumArtUri
                )
            }.sortedBy { it.name }
        }
    }

    override fun getTrackById(id: String): Flow<AudioTrack?> {
        return audioDao.getTrackById(id).map { it?.toDomain() }
    }

    override suspend fun scanMedia(): Result<Unit> {
        return try {
            val deviceTracks = localMediaSource.getAudioTracks()
            // Sync with DB: Insert new/updated, delete removed
            audioDao.insertTracks(deviceTracks.map { it.toEntity() })
            
            val deviceTrackIds = deviceTracks.map { it.id }
            audioDao.deleteTracksNotIn(deviceTrackIds)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
