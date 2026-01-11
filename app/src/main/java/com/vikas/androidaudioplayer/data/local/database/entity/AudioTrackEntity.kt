package com.vikas.androidaudioplayer.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_tracks")
data class AudioTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    @ColumnInfo(name = "album_artist") val albumArtist: String?,
    val duration: Long,
    val path: String,
    @ColumnInfo(name = "album_art_uri") val albumArtUri: String?,
    @ColumnInfo(name = "track_number") val trackNumber: Int?,
    val year: Int?,
    val genre: String?,
    val bitrate: Int?,
    @ColumnInfo(name = "sample_rate") val sampleRate: Int?,
    @ColumnInfo(name = "date_added") val dateAdded: Long,
    @ColumnInfo(name = "date_modified") val dateModified: Long,
    val size: Long,
    @ColumnInfo(name = "mime_type") val mimeType: String,
    @ColumnInfo(name = "last_played") val lastPlayed: Long? = null,
    @ColumnInfo(name = "play_count") val playCount: Int = 0
)
