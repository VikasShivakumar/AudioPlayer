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
import androidx.core.net.toUri

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

    fun playAll(tracks: List<AudioTrack>, startIndex: Int = 0) {
        val mediaItems = tracks.map { it.toMediaItem() }
        
        // Start service FIRST to ensure MediaSession is ready
        startService()
        
        _player.setMediaItems(mediaItems, startIndex, 0L)
        _player.prepare()
        _player.play()
    }

    fun addTrackToQueue(track: AudioTrack) {
        _player.addMediaItem(track.toMediaItem())
        if (_player.playbackState == Player.STATE_IDLE || _player.playbackState == Player.STATE_ENDED) {
            _player.prepare()
        }
    }

    fun addTracksToQueue(tracks: List<AudioTrack>) {
        _player.addMediaItems(tracks.map { it.toMediaItem() })
        if (_player.playbackState == Player.STATE_IDLE || _player.playbackState == Player.STATE_ENDED) {
            _player.prepare()
        }
    }

    fun playNext(track: AudioTrack) {
        val nextIndex = if (_player.mediaItemCount > 0) _player.currentMediaItemIndex + 1 else 0
        _player.addMediaItem(nextIndex, track.toMediaItem())
        if (_player.playbackState == Player.STATE_IDLE || _player.playbackState == Player.STATE_ENDED) {
            _player.prepare()
        }
    }

    fun removeFromQueue(index: Int) {
        if (index in 0 until _player.mediaItemCount) {
            _player.removeMediaItem(index)
        }
    }

    fun moveInQueue(from: Int, to: Int) {
        if (from in 0 until _player.mediaItemCount && to in 0 until _player.mediaItemCount) {
            _player.moveMediaItem(from, to)
        }
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

    private fun AudioTrack.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri(path)
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri((albumArtUri ?: "").toUri())
                    .setDurationMs(duration)
                    .build()
            )
            .build()
    }
}
