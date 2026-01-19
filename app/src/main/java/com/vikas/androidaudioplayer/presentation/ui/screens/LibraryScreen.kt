package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vikas.androidaudioplayer.R
import com.vikas.androidaudioplayer.domain.model.Album
import com.vikas.androidaudioplayer.domain.model.Artist
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.presentation.ui.components.AddToPlaylistDialog
import com.vikas.androidaudioplayer.presentation.ui.components.AlphabetScroller
import com.vikas.androidaudioplayer.presentation.ui.components.TrackItem
import com.vikas.androidaudioplayer.presentation.viewmodel.LibraryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onTrackClick: (AudioTrack) -> Unit,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
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
                        onAddToPlaylist = { track -> showAddToPlaylistDialog = track },
                        onAddToQueue = { viewModel.addToQueue(it) },
                        onPlayNext = { viewModel.playNext(it) },
                        onPlayAll = { viewModel.playAllTracks() },
                        onShuffleAll = { viewModel.shuffleAll() }
                    )
                    1 -> AlbumsGrid(albums, onAlbumClick)
                    2 -> ArtistsList(artists, onArtistClick)
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
fun TracksList(
    tracks: List<AudioTrack>, 
    onTrackClick: (AudioTrack) -> Unit,
    onAddToPlaylist: (AudioTrack) -> Unit,
    onAddToQueue: (AudioTrack) -> Unit,
    onPlayNext: (AudioTrack) -> Unit,
    onPlayAll: () -> Unit,
    onShuffleAll: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onPlayAll,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Play All")
                }
                Button(
                    onClick = onShuffleAll,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Shuffle All")
                }
            }
        }
        items(tracks) { track ->
            TrackItem(
                track = track, 
                onClick = { onTrackClick(track) },
                onAddToPlaylist = { onAddToPlaylist(track) },
                onAddToQueue = { onAddToQueue(track) },
                onPlayNext = { onPlayNext(track) }
            )
        }
    }

    AlphabetScroller(
        onLetterClick = { letter ->
            val index = tracks.indexOfFirst {
                if (letter == '#') it.title.isNotEmpty() && !it.title[0].isLetter()
                else it.title.startsWith(letter, ignoreCase = true)
            }
            if (index != -1) {
                coroutineScope.launch {
                    listState.scrollToItem(index + 1) // +1 for header
                }
            }
        },
        modifier = Modifier.align(Alignment.CenterEnd)
    )
    }
}

@Composable
fun AlbumsGrid(
    albums: List<Album>,
    onAlbumClick: (String) -> Unit
) {
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(albums) { album ->
                AlbumItem(album, onClick = { onAlbumClick(album.id) })
            }
        }

        AlphabetScroller(
            onLetterClick = { letter ->
                val index = albums.indexOfFirst {
                    if (letter == '#') it.title.isNotEmpty() && !it.title[0].isLetter()
                    else it.title.startsWith(letter, ignoreCase = true)
                }
                if (index != -1) {
                    coroutineScope.launch {
                        gridState.scrollToItem(index)
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun ArtistsList(
    artists: List<Artist>,
    onArtistClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(artists) { artist ->
            ArtistItem(artist, onClick = { onArtistClick(artist.id) })
        }
    }
}

@Composable
fun AlbumItem(
    album: Album,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(album.artworkUri)
                    .crossfade(true)
                    .error(R.drawable.ic_gramophone)
                    .placeholder(R.drawable.ic_gramophone)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(album.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(album.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ArtistItem(
    artist: Artist,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
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
