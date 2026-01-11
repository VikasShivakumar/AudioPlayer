package com.vikas.androidaudioplayer.service.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.session.SessionToken
import com.vikas.androidaudioplayer.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * A MediaSessionService that manages audio playback and provides system-integrated
 * media notifications with VLC/Spotify-like behavior.
 */
@AndroidEntryPoint
class AudioPlaybackService : MediaSessionService() {

    @Inject
    lateinit var playbackController: PlaybackController

    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        Timber.d("AudioPlaybackService onCreate - player hash: ${playbackController.player.hashCode()}")
        
        // Create the notification channel FIRST
        createNotificationChannel()
        
        // Build the MediaSession with the player from PlaybackController
        val session = MediaSession.Builder(this, playbackController.player)
            .setSessionActivity(playbackController.sessionActivityPendingIntent)
            .build()
        mediaSession = session

        // Use DefaultMediaNotificationProvider for system-level notification features
        val notificationProvider = DefaultMediaNotificationProvider.Builder(this)
            .setChannelId(CHANNEL_ID)
            .setChannelName(R.string.notification_channel_name)
            .build()
        
        setMediaNotificationProvider(notificationProvider)
        
        // Add listener to track playback state changes and update notification
        playbackController.player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Timber.d("Player isPlaying changed to: $isPlaying, mediaItemCount: ${playbackController.player.mediaItemCount}")
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                Timber.d("Player playbackState changed to: ${stateToString(playbackState)}")
            }
            
            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                Timber.d("Media item transitioned: ${mediaItem?.mediaMetadata?.title}")
            }
            
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                Timber.d("PlayWhenReady changed to: $playWhenReady")
            }
        })
    }
    
    private fun stateToString(state: Int): String = when (state) {
        Player.STATE_IDLE -> "IDLE"
        Player.STATE_BUFFERING -> "BUFFERING"
        Player.STATE_READY -> "READY"
        Player.STATE_ENDED -> "ENDED"
        else -> "UNKNOWN($state)"
    }
    
    @OptIn(UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("AudioPlaybackService onStartCommand - mediaItemCount: ${playbackController.player.mediaItemCount}, playWhenReady: ${playbackController.player.playWhenReady}")
        
        // CRITICAL: When startForegroundService() is called, we MUST call startForeground() 
        // within 5 seconds. Build an initial notification immediately.
        val session = mediaSession
        if (session != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = buildInitialNotification(session)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            Timber.d("Started foreground with notification")
        }
        
        // Call super to let MediaSessionService handle normal operations
        return super.onStartCommand(intent, flags, startId)
    }
    
    @OptIn(UnstableApi::class)
    private fun buildInitialNotification(session: MediaSession): android.app.Notification {
        val metadata = session.player.currentMediaItem?.mediaMetadata
        val isPlaying = session.player.isPlaying
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_gramophone)
            .setContentTitle(metadata?.title ?: getString(R.string.app_name))
            .setContentText(metadata?.artist ?: "")
            .setContentIntent(session.sessionActivity)
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification for audio playback controls"
                setShowBadge(false)
            }
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            Timber.d("Notification channel created: $CHANNEL_ID")
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Timber.d("onGetSession called for: ${controllerInfo.packageName}")
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.d("onTaskRemoved called")
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            Timber.d("Stopping service - player not active")
            stopSelf()
        }
    }

    override fun onDestroy() {
        Timber.d("AudioPlaybackService onDestroy")
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "audio_player_channel"
        const val NOTIFICATION_ID = 1
        
        fun getSessionToken(context: Context): SessionToken {
            return SessionToken(context, ComponentName(context, AudioPlaybackService::class.java))
        }
    }
}
