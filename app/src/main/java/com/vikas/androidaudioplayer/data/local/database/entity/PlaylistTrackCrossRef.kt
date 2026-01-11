package com.vikas.androidaudioplayer.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlist_id", "position"], // Composite PK to allow same track multiple times if needed, or better, use an auto-gen ID for the join table row. But PRD suggests position. 
    // Actually, primaryKeys = ["playlist_id", "position"] allows duplicate tracks in a playlist at different positions, which is correct.
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AudioTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["track_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistTrackCrossRef(
    @ColumnInfo(name = "playlist_id") val playlistId: String,
    @ColumnInfo(name = "track_id") val trackId: String,
    val position: Int,
    @ColumnInfo(name = "added_at") val addedAt: Long
)
