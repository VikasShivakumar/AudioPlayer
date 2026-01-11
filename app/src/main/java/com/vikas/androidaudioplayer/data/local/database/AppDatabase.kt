package com.vikas.androidaudioplayer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vikas.androidaudioplayer.data.local.database.dao.AudioDao
import com.vikas.androidaudioplayer.data.local.database.dao.PlaylistDao
import com.vikas.androidaudioplayer.data.local.database.entity.AudioTrackEntity
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistEntity
import com.vikas.androidaudioplayer.data.local.database.entity.PlaylistTrackCrossRef

@Database(
    entities = [
        AudioTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
    abstract fun playlistDao(): PlaylistDao
}
