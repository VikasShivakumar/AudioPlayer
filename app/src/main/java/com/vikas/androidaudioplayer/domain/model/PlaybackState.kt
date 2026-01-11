package com.vikas.androidaudioplayer.domain.model

data class PlaybackState(
    val currentTrack: AudioTrack?,
    val isPlaying: Boolean,
    val position: Long,
    val duration: Long,
    val playbackSpeed: Float,
    val repeatMode: RepeatMode,
    val shuffleEnabled: Boolean,
    val queue: List<AudioTrack>,
    val queueIndex: Int
)

enum class RepeatMode {
    OFF, ONE, ALL
}
