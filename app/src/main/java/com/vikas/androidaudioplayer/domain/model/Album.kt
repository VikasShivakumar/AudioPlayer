package com.vikas.androidaudioplayer.domain.model

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val year: Int?,
    val trackCount: Int,
    val artworkUri: String?,
    val duration: Long,
    val genre: String?
)
