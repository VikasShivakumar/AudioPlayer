package com.vikas.androidaudioplayer.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    data object Library : Screen("library", "Library", Icons.Default.Home)
    data object Search : Screen("search", "Search", Icons.Default.Search)
    data object Playlists : Screen("playlists", "Playlists", Icons.Default.LibraryMusic)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    // Screens without bottom bar entry
    data object NowPlaying : Screen("now_playing")
    data object PlaylistDetail : Screen("playlist_detail/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist_detail/$playlistId"
    }
    data object Equalizer : Screen("equalizer")
}
