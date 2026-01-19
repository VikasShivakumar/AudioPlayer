package com.vikas.androidaudioplayer.data.local.database.model

import androidx.room.Embedded
import androidx.room.ColumnInfo
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistEntity

data class PlaylistWithCount(
    @Embedded val playlist: PlaylistEntity,
    @ColumnInfo(name = "track_count") val trackCount: Int
)
