package com.vikas.androidaudioplayer.presentation.viewmodel

import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.domain.repository.PlaylistRepository
import com.vikas.androidaudioplayer.domain.usecase.ManagePlaylistUseCase
import com.vikas.androidaudioplayer.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PlaylistsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val playlistRepository: PlaylistRepository = mockk()
    private val managePlaylistUseCase: ManagePlaylistUseCase = mockk(relaxed = true)

    @Test
    fun `playlists StateFlow emits repository data`() = runTest {
        // Given
        val expectedPlaylists = listOf(
            Playlist(
                id = "1",
                name = "Playlist 1",
                description = "Description 1",
                trackCount = 5,
                createdAt = 1000L,
                updatedAt = 2000L,
                artworkUri = null,
                trackIds = emptyList()
            ),
            Playlist(
                id = "2",
                name = "Playlist 2",
                description = null,
                trackCount = 10,
                createdAt = 3000L,
                updatedAt = 4000L,
                artworkUri = null,
                trackIds = emptyList()
            )
        )
        every { playlistRepository.getAllPlaylists() } returns flowOf(expectedPlaylists)

        // When
        val viewModel = PlaylistsViewModel(playlistRepository, managePlaylistUseCase)

        // Collect flows to trigger WhileSubscribed
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.playlists.collect {}
        }

        // Then
        assertEquals(expectedPlaylists, viewModel.playlists.value)
    }

    @Test
    fun `playlists StateFlow initially emits empty list`() = runTest {
        // Given
        every { playlistRepository.getAllPlaylists() } returns flowOf(emptyList())

        // When
        val viewModel = PlaylistsViewModel(playlistRepository, managePlaylistUseCase)

        // Then
        assertEquals(emptyList<Playlist>(), viewModel.playlists.value)
    }

    @Test
    fun `createPlaylist calls ManagePlaylistUseCase`() = runTest {
        // Given
        every { playlistRepository.getAllPlaylists() } returns flowOf(emptyList())
        coEvery { managePlaylistUseCase.createPlaylist("New Playlist", null) } returns Result.success(
            Playlist(
                id = "1",
                name = "New Playlist",
                description = null,
                trackCount = 0,
                createdAt = 0,
                updatedAt = 0,
                artworkUri = null,
                trackIds = emptyList()
            )
        )
        val viewModel = PlaylistsViewModel(playlistRepository, managePlaylistUseCase)

        // When
        viewModel.createPlaylist("New Playlist")

        // Then
        coVerify { managePlaylistUseCase.createPlaylist("New Playlist", null) }
    }
}
