package com.vikas.androidaudioplayer.di

import android.content.Context
import androidx.room.Room
import com.vikas.androidaudioplayer.data.local.database.AppDatabase
import com.vikas.androidaudioplayer.data.local.database.dao.AudioDao
import com.vikas.androidaudioplayer.data.local.database.dao.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "audio_player_db"
        ).build()
    }

    @Provides
    fun provideAudioDao(database: AppDatabase): AudioDao = database.audioDao()

    @Provides
    fun providePlaylistDao(database: AppDatabase): PlaylistDao = database.playlistDao()
}
