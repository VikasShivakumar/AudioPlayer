package com.vikas.androidaudioplayer.domain.model

data class Playlist(
    val id: String,
    val name: String,
    val description: String? = null,
    val trackCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val artworkUri: String? = null,
    val trackIds: List<String> = emptyList()
)
