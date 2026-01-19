package com.vikas.androidaudioplayer.domain.usecase

import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.service.playback.PlaybackController
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class PlayTrackUseCaseTest {

    private val playbackController: PlaybackController = mockk(relaxed = true)
    private val useCase = PlayTrackUseCase(playbackController)

    private fun createTestTrack(id: String): AudioTrack {
        return AudioTrack(
            id = id,
            title = "Track $id",
            artist = "Artist",
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
    fun `invoke with valid tracks calls playbackController playAll`() {
        // Given
        val tracks = listOf(createTestTrack("1"), createTestTrack("2"))
        val startIndex = 0

        // When
        useCase(tracks, startIndex)

        // Then
        verify { playbackController.playAll(tracks, startIndex) }
    }

    @Test
    fun `invoke with valid tracks and non-zero index calls playAll`() {
        // Given
        val tracks = listOf(createTestTrack("1"), createTestTrack("2"), createTestTrack("3"))
        val startIndex = 1

        // When
        useCase(tracks, startIndex)

        // Then
        verify { playbackController.playAll(tracks, startIndex) }
    }

    @Test
    fun `invoke with empty tracks does not call playbackController`() {
        // Given
        val tracks = emptyList<AudioTrack>()

        // When
        useCase(tracks, 0)

        // Then
        verify(exactly = 0) { playbackController.playAll(any(), any()) }
    }

    @Test
    fun `invoke with invalid startIndex does not call playbackController`() {
        // Given
        val tracks = listOf(createTestTrack("1"))
        val startIndex = 5 // Out of bounds

        // When
        useCase(tracks, startIndex)

        // Then
        verify(exactly = 0) { playbackController.playAll(any(), any()) }
    }

    @Test
    fun `invoke with negative startIndex does not call playbackController`() {
        // Given
        val tracks = listOf(createTestTrack("1"), createTestTrack("2"))
        val startIndex = -1

        // When
        useCase(tracks, startIndex)

        // Then
        verify(exactly = 0) { playbackController.playAll(any(), any()) }
    }
}
