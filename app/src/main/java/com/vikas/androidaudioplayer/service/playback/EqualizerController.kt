package com.vikas.androidaudioplayer.service.playback

import android.media.audiofx.Equalizer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EqualizerController @Inject constructor() {
    private var equalizer: Equalizer? = null
    
    fun initEqualizer(audioSessionId: Int) {
        try {
            if (equalizer != null) {
                equalizer?.release()
            }
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize equalizer")
        }
    }

    fun getBandCount(): Int = equalizer?.numberOfBands?.toInt() ?: 0

    fun getBandLevelRange(): ShortArray = equalizer?.bandLevelRange ?: shortArrayOf(-1500, 1500)

    fun getCenterFreq(band: Short): Int = equalizer?.getCenterFreq(band) ?: 0

    fun getBandLevel(band: Short): Short = equalizer?.getBandLevel(band) ?: 0

    fun setBandLevel(band: Short, level: Short) {
        equalizer?.setBandLevel(band, level)
    }

    fun release() {
        equalizer?.release()
        equalizer = null
    }
}
