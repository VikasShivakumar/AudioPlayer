package com.vikas.androidaudioplayer.data.mapper

import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistEntity
import com.vikas.androidaudioplayer.domain.model.Playlist

fun PlaylistEntity.toDomain(trackIds: List<String> = emptyList(), trackCount: Int = 0): Playlist {
    return Playlist(
        id = id,
        name = name,
        description = description,
        trackCount = trackCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        artworkUri = artworkUri,
        trackIds = trackIds
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        artworkUri = artworkUri
    )
}
