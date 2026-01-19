package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.vikas.androidaudioplayer.domain.model.Artist
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ArtistDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mediaRepository: MediaRepository = mockk()
    private val playlistRepository: PlaylistRepository = mockk(relaxed = true)
    private val managePlaylistUseCase: ManagePlaylistUseCase = mockk(relaxed = true)
    private val playTrackUseCase: PlayTrackUseCase = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk()

    private fun createTestTrack(id: String, artist: String): AudioTrack {
        return AudioTrack(
            id = id,
            title = "Track $id",
            artist = artist,
            album = "Album",
            albumArtist = null,
            duration = 180000L,
            path = "/path/to/$id.mp3",
            albumArtUri = null,
            trackNumber = 1,
            year = 2023,
            genre = "Pop",
            bitrate = 320,
            sampleRate = 44100,
            dateAdded = 0,
            dateModified = 0,
            size = 1024,
            mimeType = "audio/mpeg"
        )
    }

    @Test
    fun `initialization loads artist and tracks`() = runTest {
        // Given
        val artistId = "artist_1"
        val expectedArtist = Artist(
            id = artistId,
            name = "Test Artist",
            albumCount = 2,
            trackCount = 5,
            artworkUri = null
        )
        val tracks = listOf(
            createTestTrack("1", "Test Artist"),
            createTestTrack("2", "Other Artist"),
            createTestTrack("3", "Test Artist")
        )

        every { savedStateHandle.get<String>("artistId") } returns artistId
        coEvery { mediaRepository.getArtists() } returns flowOf(listOf(expectedArtist))
        coEvery { mediaRepository.getAllTracks() } returns flowOf(tracks)
        coEvery { playlistRepository.getAllPlaylists() } returns flowOf(emptyList())

        // When
        val viewModel = ArtistDetailViewModel(
            mediaRepository,
            playlistRepository,
            managePlaylistUseCase,
            playTrackUseCase,
            savedStateHandle
        )

        // Collect flows to trigger WhileSubscribed
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.artist.collect {}
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tracks.collect {}
        }

        // Then
        val artistState = viewModel.artist.value
        assertEquals(expectedArtist, artistState)

        val tracksState = viewModel.tracks.value
        assertEquals(2, tracksState.size)
        assertEquals("Test Artist", tracksState[0].artist)
        assertEquals("Test Artist", tracksState[1].artist)
    }

    @Test
    fun `playTrack calls PlayTrackUseCase with correct parameters`() = runTest {
        // Given
        val artistId = "artist_1"
        val expectedArtist = Artist(
            id = artistId,
            name = "Test Artist",
            albumCount = 1,
            trackCount = 2,
            artworkUri = null
        )
        val track1 = createTestTrack("1", "Test Artist")
        val track2 = createTestTrack("2", "Test Artist")
        val tracks = listOf(track1, track2)

        every { savedStateHandle.get<String>("artistId") } returns artistId
        coEvery { mediaRepository.getArtists() } returns flowOf(listOf(expectedArtist))
        coEvery { mediaRepository.getAllTracks() } returns flowOf(tracks)
        coEvery { playlistRepository.getAllPlaylists() } returns flowOf(emptyList())

        val viewModel = ArtistDetailViewModel(
            mediaRepository,
            playlistRepository,
            managePlaylistUseCase,
            playTrackUseCase,
            savedStateHandle
        )

        // Collect flows to populate tracks
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.artist.collect {}
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tracks.collect {}
        }

        assertEquals(2, viewModel.tracks.value.size)

        // When
        viewModel.playTrack(track2)

        // Then
        coVerify { playTrackUseCase(tracks, 1) }
    }

    @Test
    fun `addTrackToPlaylist calls ManagePlaylistUseCase`() = runTest {
        // Given
        val artistId = "artist_1"
        val expectedArtist = Artist(
            id = artistId,
            name = "Test Artist",
            albumCount = 1,
            trackCount = 1,
            artworkUri = null
        )
        val track = createTestTrack("1", "Test Artist")

        every { savedStateHandle.get<String>("artistId") } returns artistId
        coEvery { mediaRepository.getArtists() } returns flowOf(listOf(expectedArtist))
        coEvery { mediaRepository.getAllTracks() } returns flowOf(listOf(track))
        coEvery { playlistRepository.getAllPlaylists() } returns flowOf(emptyList())

        val viewModel = ArtistDetailViewModel(
            mediaRepository,
            playlistRepository,
            managePlaylistUseCase,
            playTrackUseCase,
            savedStateHandle
        )

        // When
        viewModel.addTrackToPlaylist("playlist_1", track)

        // Then
        coVerify { managePlaylistUseCase.addTracks("playlist_1", listOf("1")) }
    }
}
