package com.vikas.androidaudioplayer.data.mapper

import com.vikas.androidaudioplayer.data.local.database.entity.AudioTrackEntity
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import org.junit.Assert.assertEquals
import org.junit.Test

class AudioMapperTest {

    @Test
    fun `AudioTrackEntity toDomain maps correctly`() {
        // Given
        val entity = AudioTrackEntity(
            id = 1,
            title = "Test Title",
            artist = "Test Artist",
            album = "Test Album",
            albumArtist = "Test Album Artist",
            duration = 1000L,
            path = "/path/to/file",
            albumArtUri = "content://media/external/audio/albumart/1",
            trackNumber = 1,
            year = 2023,
            genre = "Rock",
            bitrate = 320,
            sampleRate = 44100,
            dateAdded = 123456789L,
            dateModified = 987654321L,
            size = 1024L,
            mimeType = "audio/mpeg"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(entity.id, domain.id)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.artist, domain.artist)
        assertEquals(entity.album, domain.album)
        assertEquals(entity.path, domain.path)
        assertEquals(entity.duration, domain.duration)
    }

    @Test
    fun `AudioTrack toEntity maps correctly`() {
        // Given
        val domain = AudioTrack(
            id = 1,
            title = "Test Title",
            artist = "Test Artist",
            album = "Test Album",
            albumArtist = "Test Album Artist",
            duration = 1000L,
            path = "/path/to/file",
            albumArtUri = "content://media/external/audio/albumart/1",
            trackNumber = 1,
            year = 2023,
            genre = "Rock",
            bitrate = 320,
            sampleRate = 44100,
            dateAdded = 123456789L,
            dateModified = 987654321L,
            size = 1024L,
            mimeType = "audio/mpeg"
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals(domain.id, entity.id)
        assertEquals(domain.title, entity.title)
        assertEquals(domain.artist, entity.artist)
        assertEquals(domain.album, entity.album)
        assertEquals(domain.path, entity.path)
        assertEquals(domain.duration, entity.duration)
    }
}
