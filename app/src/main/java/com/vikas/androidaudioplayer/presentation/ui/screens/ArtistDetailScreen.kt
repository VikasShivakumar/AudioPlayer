package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vikas.androidaudioplayer.R
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.presentation.ui.components.AddToPlaylistDialog
import com.vikas.androidaudioplayer.presentation.ui.components.TrackItem
import com.vikas.androidaudioplayer.presentation.viewmodel.ArtistDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    onBackClick: () -> Unit,
    onTrackClick: (AudioTrack) -> Unit,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val artist by viewModel.artist.collectAsState()
    val tracks by viewModel.tracks.collectAsState()
    val playlists by viewModel.playlists.collectAsState()

    var showAddToPlaylistDialog by remember { mutableStateOf<AudioTrack?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(artist?.name ?: stringResource(R.string.artists)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.collapse))
                    }
                },
                 colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        if (artist != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                 item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                         AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(null) // Artist image URI if available
                                .crossfade(true)
                                .error(R.drawable.ic_gramophone) 
                                .placeholder(R.drawable.ic_gramophone)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = artist?.name ?: "",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${artist?.albumCount} ${stringResource(R.string.albums)} • ${artist?.trackCount} ${stringResource(R.string.tracks)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                items(tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = { 
                            viewModel.playTrack(track)
                            onTrackClick(track)
                        },
                        onAddToPlaylist = {
                            showAddToPlaylistDialog = track
                        }
                    )
                }
            }
        } else {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

     if (showAddToPlaylistDialog != null) {
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = { showAddToPlaylistDialog = null },
            onPlaylistSelected = { selectedPlaylist ->
                viewModel.addTrackToPlaylist(selectedPlaylist.id, showAddToPlaylistDialog!!)
                showAddToPlaylistDialog = null
            }
        )
    }
}
