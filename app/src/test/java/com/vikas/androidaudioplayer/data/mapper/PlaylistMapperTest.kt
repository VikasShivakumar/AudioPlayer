package com.vikas.androidaudioplayer.data.mapper

import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistEntity
import com.vikas.androidaudioplayer.data.local.database.model.PlaylistWithCount
import com.vikas.androidaudioplayer.domain.model.Playlist
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaylistMapperTest {

    @Test
    fun `PlaylistEntity toDomain maps correctly with defaults`() {
        // Given
        val entity = PlaylistEntity(
            id = "playlist_1",
            name = "Test Playlist",
            description = "Test Description",
            createdAt = 1000L,
            updatedAt = 2000L,
            artworkUri = "content://artwork/1"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.createdAt, domain.createdAt)
        assertEquals(entity.updatedAt, domain.updatedAt)
        assertEquals(entity.artworkUri, domain.artworkUri)
        assertEquals(0, domain.trackCount)
        assertEquals(emptyList<String>(), domain.trackIds)
    }

    @Test
    fun `PlaylistEntity toDomain maps correctly with trackIds and trackCount`() {
        // Given
        val entity = PlaylistEntity(
            id = "playlist_1",
            name = "Test Playlist",
            description = null,
            createdAt = 1000L,
            updatedAt = 2000L,
            artworkUri = null
        )
        val trackIds = listOf("track_1", "track_2", "track_3")

        // When
        val domain = entity.toDomain(trackIds = trackIds, trackCount = 3)

        // Then
        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(3, domain.trackCount)
        assertEquals(trackIds, domain.trackIds)
    }

    @Test
    fun `PlaylistWithCount toDomain maps correctly`() {
        // Given
        val entity = PlaylistEntity(
            id = "playlist_1",
            name = "My Playlist",
            description = "A great playlist",
            createdAt = 5000L,
            updatedAt = 6000L,
            artworkUri = null
        )
        val playlistWithCount = PlaylistWithCount(
            playlist = entity,
            trackCount = 10
        )

        // When
        val domain = playlistWithCount.toDomain()

        // Then
        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(10, domain.trackCount)
    }

    @Test
    fun `PlaylistWithCount toDomain with trackIds maps correctly`() {
        // Given
        val entity = PlaylistEntity(
            id = "playlist_2",
            name = "Another Playlist",
            description = null,
            createdAt = 0L,
            updatedAt = 0L,
            artworkUri = null
        )
        val playlistWithCount = PlaylistWithCount(
            playlist = entity,
            trackCount = 2
        )
        val trackIds = listOf("track_a", "track_b")

        // When
        val domain = playlistWithCount.toDomain(trackIds = trackIds)

        // Then
        assertEquals(trackIds, domain.trackIds)
        assertEquals(2, domain.trackCount)
    }

    @Test
    fun `Playlist toEntity maps correctly`() {
        // Given
        val domain = Playlist(
            id = "playlist_1",
            name = "Test Playlist",
            description = "A description",
            trackCount = 5,
            createdAt = 1000L,
            updatedAt = 2000L,
            artworkUri = "content://artwork/1",
            trackIds = listOf("track_1", "track_2")
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals(domain.id, entity.id)
        assertEquals(domain.name, entity.name)
        assertEquals(domain.description, entity.description)
        assertEquals(domain.createdAt, entity.createdAt)
        assertEquals(domain.updatedAt, entity.updatedAt)
        assertEquals(domain.artworkUri, entity.artworkUri)
    }
}
