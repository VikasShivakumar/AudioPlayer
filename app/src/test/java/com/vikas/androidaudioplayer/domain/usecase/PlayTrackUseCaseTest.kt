package com.vikas.androidaudioplayer.domain.usecase

import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.service.playback.PlaybackController
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class PlayTrackUseCaseTest {

    private val playbackController: PlaybackController = mockk(relaxed = true)
    private val playTrackUseCase = PlayTrackUseCase(playbackController)

    @Test
    fun `invoke should call play on playbackController`() {
        val track = AudioTrack(
            id = "1",
            title = "Test Title",
            artist = "Test Artist",
            album = "Test Album",
            albumArtist = null,
            duration = 1000,
            path = "test/path",
            albumArtUri = null,
            trackNumber = null,
            year = null,
            genre = null,
            bitrate = null,
            sampleRate = null,
            dateAdded = 0,
            dateModified = 0,
            size = 0,
            mimeType = "audio/mpeg"
        )

        playTrackUseCase(track)

        verify { playbackController.play(track) }
    }
}
