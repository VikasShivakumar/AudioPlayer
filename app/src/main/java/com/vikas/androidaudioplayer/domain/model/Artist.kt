package com.vikas.androidaudioplayer.domain.model

data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int,
    val trackCount: Int,
    val artworkUri: String?
)
