package com.vikas.androidaudioplayer.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vikas.androidaudioplayer.presentation.ui.screens.EqualizerScreen
import com.vikas.androidaudioplayer.presentation.ui.screens.LibraryScreen
import com.vikas.androidaudioplayer.presentation.ui.screens.NowPlayingScreen
import com.vikas.androidaudioplayer.presentation.ui.screens.PlayingQueueScreen
import com.vikas.androidaudioplayer.presentation.ui.screens.PlaylistDetailScreen
import com.vikas.androidaudioplayer.presentation.ui.screens.PlaylistsScreen
import com.vikas.androidaudioplayer.presentation.ui.screens.SearchScreen
import com.vikas.androidaudioplayer.presentation.ui.screens.SettingsScreen
import com.vikas.androidaudioplayer.presentation.viewmodel.LibraryViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Library.route,
        modifier = modifier
    ) {
        composable(Screen.Library.route) {
            val viewModel: LibraryViewModel = hiltViewModel()
            LibraryScreen(
                onTrackClick = { track ->
                    viewModel.playTrack(track)
                    navController.navigate(Screen.NowPlaying.route)
                },
                viewModel = viewModel
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onTrackClick = {
                    navController.navigate(Screen.NowPlaying.route)
                }
            )
        }
        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                onPlaylistClick = { playlistId ->
                    navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                }
            )
        }
        composable(
            route = Screen.PlaylistDetail.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
        ) {
            PlaylistDetailScreen(
                onBackClick = { navController.popBackStack() },
                onTrackClick = {
                    navController.navigate(Screen.NowPlaying.route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onEqualizerClick = { navController.navigate(Screen.Equalizer.route) }
            )
        }
        composable(Screen.Equalizer.route) {
            EqualizerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.NowPlaying.route) {
            NowPlayingScreen(
                onBackClick = { navController.popBackStack() },
                onQueueClick = { navController.navigate(Screen.PlayingQueue.route) }
            )
        }
        composable(Screen.PlayingQueue.route) {
            PlayingQueueScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = name)
    }
}
