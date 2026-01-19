package com.vikas.androidaudioplayer.service.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.session.SessionToken
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.vikas.androidaudioplayer.R
import com.vikas.androidaudioplayer.domain.repository.MediaRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.future
import timber.log.Timber
import javax.inject.Inject

/**
 * A MediaLibraryService that manages audio playback and provides system-integrated
 * media notifications and Android Auto content browsing.
 */
@AndroidEntryPoint
class AudioPlaybackService : MediaLibraryService() {

    @Inject
    lateinit var playbackController: PlaybackController

    @Inject
    lateinit var mediaRepository: MediaRepository

    private var mediaLibrarySession: MediaLibrarySession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        Timber.d("AudioPlaybackService onCreate")
        
        // Create the notification channel FIRST
        createNotificationChannel()
        
        // Build the MediaLibrarySession with the player from PlaybackController
        val session = MediaLibrarySession.Builder(this, playbackController.player, LibrarySessionCallback())
            .setSessionActivity(playbackController.sessionActivityPendingIntent)
            .build()
        mediaLibrarySession = session

        // Use DefaultMediaNotificationProvider for system-level notification features
        val notificationProvider = DefaultMediaNotificationProvider.Builder(this)
            .setChannelId(CHANNEL_ID)
            .setChannelName(R.string.notification_channel_name)
            .build()
        
        setMediaNotificationProvider(notificationProvider)
        
        // Add listener to track playback state changes and update notification
        playbackController.player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Timber.d("Player isPlaying changed to: $isPlaying")
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                Timber.d("Player playbackState changed to: ${stateToString(playbackState)}")
            }
        })
    }
    
    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {
        @OptIn(UnstableApi::class)
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            // Check if the caller is allowed to access the library
            // For now, allow everyone (or specifically Auto)
            val rootExtras = params?.extras ?: android.os.Bundle()
            val rootMediaItem = MediaItem.Builder()
                .setMediaId("root")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .setTitle("Root")
                        .build()
                )
                .build()
            return Futures.immediateFuture(LibraryResult.ofItem(rootMediaItem, params))
        }

        @OptIn(UnstableApi::class)
        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            Timber.d("onGetChildren for parentId: $parentId")
            
            return serviceScope.future(Dispatchers.IO) {
                val mediaItems = when (parentId) {
                    "root" -> {
                        listOf(
                            createBrowsableItem("tracks", "All Tracks", "All your songs"),
                            createBrowsableItem("albums", "Albums", "Browse by album"),
                            createBrowsableItem("artists", "Artists", "Browse by artist")
                        )
                    }
                    "tracks" -> {
                        mediaRepository.getAllTracks().first().map { track ->
                            MediaItem.Builder()
                                .setMediaId(track.id)
                                .setUri(track.path)
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setIsBrowsable(false)
                                        .setIsPlayable(true)
                                        .setTitle(track.title)
                                        .setArtist(track.artist)
                                        .setAlbumTitle(track.album)
                                        .setArtworkUri(android.net.Uri.parse(track.albumArtUri ?: ""))
                                        .build()
                                )
                                .build()
                        }
                    }
                    "albums" -> {
                        mediaRepository.getAlbums().first().map { album ->
                             createBrowsableItem("album_${album.id}", album.title, album.artist)
                        }
                    }
                    "artists" -> {
                        mediaRepository.getArtists().first().map { artist ->
                            createBrowsableItem("artist_${artist.id}", artist.name, "")
                        }
                    }
                    else -> {
                        // Handle getting tracks for specific album/artist if needed
                        // For simplicity, just return empty for unknown parents or TODO implement deep browsing
                        emptyList()
                    }
                }
                LibraryResult.ofItemList(ImmutableList.copyOf(mediaItems), params)
            }
        }
    }

    private fun createBrowsableItem(id: String, title: String, subtitle: String): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .build()
            )
            .build()
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
        val session = mediaLibrarySession
        if (session != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = buildInitialNotification(session)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        }
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
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaLibrarySession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaLibrarySession?.run {
            // We DON'T release the player here because it's managed by the Singleton PlaybackController.
            // Releasing it here would break play buttons if the service is destroyed but the app is still alive.
            release()
            mediaLibrarySession = null
        }
        serviceScope.coroutineContext.cancelChildren() // Cancel coroutines
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
