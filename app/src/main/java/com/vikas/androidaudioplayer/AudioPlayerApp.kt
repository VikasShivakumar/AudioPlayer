package com.vikas.androidaudioplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AudioPlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val fileLoggingTree = com.vikas.androidaudioplayer.util.FileLoggingTree(this)
        Timber.plant(fileLoggingTree)
        
        // Clean old logs on startup (keep last 3 days for now)
        fileLoggingTree.cleanOldLogs(3)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
