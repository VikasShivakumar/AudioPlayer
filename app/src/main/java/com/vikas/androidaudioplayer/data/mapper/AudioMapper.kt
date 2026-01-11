package com.vikas.androidaudioplayer.data.mapper

import com.vikas.androidaudioplayer.data.local.database.entity.AudioTrackEntity
import com.vikas.androidaudioplayer.domain.model.AudioTrack

fun AudioTrackEntity.toDomain(): AudioTrack {
    return AudioTrack(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumArtist = albumArtist,
        duration = duration,
        path = path,
        albumArtUri = albumArtUri,
        trackNumber = trackNumber,
        year = year,
        genre = genre,
        bitrate = bitrate,
        sampleRate = sampleRate,
        dateAdded = dateAdded,
        dateModified = dateModified,
        size = size,
        mimeType = mimeType
    )
}

fun AudioTrack.toEntity(): AudioTrackEntity {
    return AudioTrackEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumArtist = albumArtist,
        duration = duration,
        path = path,
        albumArtUri = albumArtUri,
        trackNumber = trackNumber,
        year = year,
        genre = genre,
        bitrate = bitrate,
        sampleRate = sampleRate,
        dateAdded = dateAdded,
        dateModified = dateModified,
        size = size,
        mimeType = mimeType
    )
}
