package com.vikas.androidaudioplayer.service.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import dagger.hilt.android.qualifiers.ApplicationContext
import android.app.PendingIntent
import android.content.Intent
import com.vikas.androidaudioplayer.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val equalizerController: EqualizerController
) {

    val sessionActivityPendingIntent: PendingIntent
        get() = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private val _player: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true
        )
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(C.WAKE_MODE_NETWORK)
        .build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        equalizerController.initEqualizer(audioSessionId)
                    }
                }
            })
        }

    val player: Player get() = _player

    fun play(track: AudioTrack) {
        val mediaItem = MediaItem.Builder()
            .setUri(track.path)
            .setMediaId(track.id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .setAlbumTitle(track.album)
                    .setArtworkUri(android.net.Uri.parse(track.albumArtUri ?: ""))
                    .setDurationMs(track.duration)
                    .build()
            )
            .build()

        // Start service FIRST to ensure MediaSession is ready
        startService()
        
        _player.setMediaItem(mediaItem)
        _player.prepare()
        _player.play()
    }
    
    fun playAll(tracks: List<AudioTrack>, startIndex: Int = 0) {
         val mediaItems = tracks.map { track ->
            MediaItem.Builder()
                .setUri(track.path)
                .setMediaId(track.id)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .setArtworkUri(android.net.Uri.parse(track.albumArtUri ?: ""))
                        .setDurationMs(track.duration)
                        .build()
                )
                .build()
        }
        
        // Start service FIRST to ensure MediaSession is ready
        startService()
        
        _player.setMediaItems(mediaItems, startIndex, 0L)
        _player.prepare()
        _player.play()
    }

    private fun startService() {
        val intent = Intent(context, AudioPlaybackService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun release() {
        _player.release()
    }
}
