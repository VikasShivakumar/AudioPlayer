package com.vikas.androidaudioplayer.domain.usecase

import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.service.playback.PlaybackController
import javax.inject.Inject

class PlayTrackUseCase @Inject constructor(
    private val playbackController: PlaybackController
) {
    operator fun invoke(tracks: List<AudioTrack>, startIndex: Int = 0) {
        if (tracks.isNotEmpty() && startIndex in tracks.indices) {
            playbackController.playAll(tracks, startIndex)
        }
    }
}
