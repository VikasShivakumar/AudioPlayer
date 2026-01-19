package com.vikas.androidaudioplayer.domain.usecase

import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.domain.repository.PlaylistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ManagePlaylistUseCaseTest {

    private val playlistRepository: PlaylistRepository = mockk(relaxed = true)
    private val useCase = ManagePlaylistUseCase(playlistRepository)

    @Test
    fun `createPlaylist with valid name returns success`() = runTest {
        // Given
        val name = "My Playlist"
        val description = "A description"
        val expectedPlaylist = Playlist(
            id = "1",
            name = name,
            description = description,
            trackCount = 0,
            createdAt = 0L,
            updatedAt = 0L,
            artworkUri = null,
            trackIds = emptyList()
        )
        coEvery { playlistRepository.createPlaylist(name, description) } returns Result.success(expectedPlaylist)

        // When
        val result = useCase.createPlaylist(name, description)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedPlaylist, result.getOrNull())
        coVerify { playlistRepository.createPlaylist(name, description) }
    }

    @Test
    fun `createPlaylist with blank name returns failure`() = runTest {
        // When
        val result = useCase.createPlaylist("   ")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { playlistRepository.createPlaylist(any(), any()) }
    }

    @Test
    fun `createPlaylist with empty name returns failure`() = runTest {
        // When
        val result = useCase.createPlaylist("")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `addTracks delegates to repository`() = runTest {
        // Given
        val playlistId = "playlist_1"
        val trackIds = listOf("track_1", "track_2", "track_3")

        // When
        useCase.addTracks(playlistId, trackIds)

        // Then
        coVerify { playlistRepository.addTracksToPlaylist(playlistId, trackIds) }
    }

    @Test
    fun `addTracks with empty list still delegates to repository`() = runTest {
        // Given
        val playlistId = "playlist_1"
        val trackIds = emptyList<String>()

        // When
        useCase.addTracks(playlistId, trackIds)

        // Then
        coVerify { playlistRepository.addTracksToPlaylist(playlistId, trackIds) }
    }
}
