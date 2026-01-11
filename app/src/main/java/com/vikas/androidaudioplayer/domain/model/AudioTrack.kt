package com.vikas.androidaudioplayer.domain.model

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String?,
    val duration: Long, // milliseconds
    val path: String,
    val albumArtUri: String?,
    val trackNumber: Int?,
    val year: Int?,
    val genre: String?,
    val bitrate: Int?,
    val sampleRate: Int?,
    val dateAdded: Long,
    val dateModified: Long,
    val size: Long,
    val mimeType: String
)
