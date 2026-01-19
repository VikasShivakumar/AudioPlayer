package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.vikas.androidaudioplayer.domain.model.Album
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.domain.repository.MediaRepository
import com.vikas.androidaudioplayer.domain.repository.PlaylistRepository
import com.vikas.androidaudioplayer.domain.usecase.ManagePlaylistUseCase
import com.vikas.androidaudioplayer.domain.usecase.PlayTrackUseCase
import com.vikas.androidaudioplayer.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AlbumDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mediaRepository: MediaRepository = mockk()
    private val playlistRepository: PlaylistRepository = mockk(relaxed = true)
    private val managePlaylistUseCase: ManagePlaylistUseCase = mockk(relaxed = true)
    private val playTrackUseCase: PlayTrackUseCase = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk()

    @Test
    fun `initialization loads album and tracks`() = runTest {
        // Given
        val albumId = "album_1"
        val expectedAlbum = Album(id = albumId, title = "Test Album", artist = "Test Artist")
        val tracks = listOf(
            AudioTrack(id = 1, title = "Track 1", album = "Test Album", artist = "Test Artist", path = "", duration = 0, albumArtUri = null),
            AudioTrack(id = 2, title = "Track 2", album = "Other Album", artist = "Other Artist", path = "", duration = 0, albumArtUri = null)
        )

        every { savedStateHandle.get<String>("albumId") } returns albumId
        coEvery { mediaRepository.getAlbums() } returns flowOf(listOf(expectedAlbum))
        coEvery { mediaRepository.getAllTracks() } returns flowOf(tracks)

        // When
        val viewModel = AlbumDetailViewModel(
            mediaRepository,
            playlistRepository,
            managePlaylistUseCase,
            playTrackUseCase,
            savedStateHandle
        )

        // Then
        // Allow state flow to emit
        val albumState = viewModel.album.value
        assertEquals(expectedAlbum, albumState)

        val tracksState = viewModel.tracks.value
        assertEquals(1, tracksState.size)
        assertEquals("Track 1", tracksState[0].title)
    }

    @Test
    fun `playTrack calls use case`() = runTest {
        // Given
        val albumId = "album_1"
        val expectedAlbum = Album(id = albumId, title = "Test Album", artist = "Test Artist")
        val track = AudioTrack(id = 1, title = "Track 1", album = "Test Album", artist = "Test Artist", path = "", duration = 0, albumArtUri = null)

        every { savedStateHandle.get<String>("albumId") } returns albumId
        coEvery { mediaRepository.getAlbums() } returns flowOf(listOf(expectedAlbum))
        coEvery { mediaRepository.getAllTracks() } returns flowOf(listOf(track))

        val viewModel = AlbumDetailViewModel(
            mediaRepository,
            playlistRepository,
            managePlaylistUseCase,
            playTrackUseCase,
            savedStateHandle
        )

        // Ensure tracks are loaded
        assertEquals(1, viewModel.tracks.value.size)

        // When
        viewModel.playTrack(track)

        // Then
        coVerify { playTrackUseCase(listOf(track), 0) }
    }
}
