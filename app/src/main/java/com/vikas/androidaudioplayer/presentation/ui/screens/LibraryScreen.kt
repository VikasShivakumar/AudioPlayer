package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vikas.androidaudioplayer.domain.model.Album
import com.vikas.androidaudioplayer.domain.model.Artist
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.presentation.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onTrackClick: (AudioTrack) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val tracks by viewModel.tracks.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tracks", "Albums", "Artists")

    var showAddToPlaylistDialog by remember { mutableStateOf<AudioTrack?>(null) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Library") },
                    actions = {
                        IconButton(
                            onClick = { viewModel.scanMedia() },
                            enabled = !isScanning
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Scan Media")
                        }
                    }
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
                if (isScanning) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (tracks.isEmpty() && !isScanning) {
                EmptyLibraryContent(onScanClick = { viewModel.scanMedia() })
            } else {
                when (selectedTabIndex) {
                    0 -> TracksList(
                        tracks = tracks, 
                        onTrackClick = onTrackClick,
                        onAddToPlaylist = { track -> showAddToPlaylistDialog = track }
                    )
                    1 -> AlbumsGrid(albums)
                    2 -> ArtistsList(artists)
                }
            }
        }
    }

    if (showAddToPlaylistDialog != null) {
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = { showAddToPlaylistDialog = null },
            onPlaylistSelected = { playlist ->
                viewModel.addTrackToPlaylist(playlist.id, showAddToPlaylistDialog!!)
                showAddToPlaylistDialog = null
            }
        )
    }
}

@Composable
fun EmptyLibraryContent(onScanClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No music found on your device.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onScanClick) {
            Text("Scan for Media")
        }
    }
}

@Composable
fun AddToPlaylistDialog(
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (Playlist) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Playlist") },
        text = {
            LazyColumn {
                items(playlists) { playlist ->
                    ListItem(
                        headlineContent = { Text(playlist.name) },
                        modifier = Modifier.clickable { onPlaylistSelected(playlist) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TracksList(
    tracks: List<AudioTrack>, 
    onTrackClick: (AudioTrack) -> Unit,
    onAddToPlaylist: (AudioTrack) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks) { track ->
            TrackItem(
                track = track, 
                onClick = { onTrackClick(track) },
                onAddToPlaylist = { onAddToPlaylist(track) }
            )
        }
    }
}

@Composable
fun AlbumsGrid(albums: List<Album>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(albums) { album ->
            AlbumItem(album)
        }
    }
}

@Composable
fun ArtistsList(artists: List<Artist>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(artists) { artist ->
            ArtistItem(artist)
        }
    }
}

@Composable
fun TrackItem(
    track: AudioTrack, 
    onClick: () -> Unit,
    onAddToPlaylist: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(track.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text("${track.artist} • ${track.album}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Add to Playlist") },
                        onClick = {
                            showMenu = false
                            onAddToPlaylist()
                        }
                    )
                }
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun AlbumItem(album: Album) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(0.8f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                 Box(contentAlignment = Alignment.Center) {
                     Text("Art")
                 }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(album.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(album.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ArtistItem(artist: Artist) {
    ListItem(
        headlineContent = { Text(artist.name) },
        supportingContent = { Text("${artist.albumCount} albums • ${artist.trackCount} tracks") },
        leadingContent = {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(artist.name.take(1))
                }
            }
        }
    )
}
