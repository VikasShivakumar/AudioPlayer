package com.vikas.androidaudioplayer.data.repository

import com.vikas.androidaudioplayer.data.local.database.dao.PlaylistDao
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistEntity
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistTrackCrossRef
import com.vikas.androidaudioplayer.data.local.database.model.PlaylistWithCount
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaylistRepositoryImplTest {

    private val playlistDao: PlaylistDao = mockk(relaxed = true)
    private val repository = PlaylistRepositoryImpl(playlistDao)

    @Test
    fun `getAllPlaylists maps entities to domain`() = runTest {
        // Given
        val entity = PlaylistWithCount(
            id = "1",
            name = "Test Playlist",
            description = "Description",
            createdAt = 0L,
            updatedAt = 0L,
            trackCount = 5,
            artworkUri = null
        )
        coEvery { playlistDao.getAllPlaylists() } returns flowOf(listOf(entity))

        // When
        val result = repository.getAllPlaylists().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test Playlist", result[0].name)
        assertEquals(5, result[0].trackCount)
    }

    @Test
    fun `createPlaylist inserts entity and returns success`() = runTest {
        // Given
        val name = "New Playlist"
        val description = "New Description"
        val slot = slot<PlaylistEntity>()

        coEvery { playlistDao.insertPlaylist(capture(slot)) } returns Unit

        // When
        val result = repository.createPlaylist(name, description)

        // Then
        assertTrue(result.isSuccess)
        val playlist = result.getOrNull()
        assertEquals(name, playlist?.name)
        assertEquals(description, playlist?.description)
        
        coVerify { playlistDao.insertPlaylist(any()) }
        assertEquals(name, slot.captured.name)
    }

    @Test
    fun `addTracksToPlaylist inserts cross refs`() = runTest {
        // Given
        val playlistId = "playlist_1"
        val trackIds = listOf("track_1", "track_2")
        
        coEvery { playlistDao.getMaxPosition(playlistId) } returns 0

        // When
        repository.addTracksToPlaylist(playlistId, trackIds)

        // Then
        coVerify(exactly = 2) { playlistDao.insertPlaylistTrackCrossRef(any()) }
    }
}
