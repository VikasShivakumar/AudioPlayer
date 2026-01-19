package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vikas.androidaudioplayer.R
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import com.vikas.androidaudioplayer.presentation.ui.components.AddToPlaylistDialog
import com.vikas.androidaudioplayer.presentation.ui.components.TrackItem
import com.vikas.androidaudioplayer.presentation.viewmodel.PlaylistDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    onBackClick: () -> Unit,
    onTrackClick: (AudioTrack) -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
    val playlist by viewModel.playlist.collectAsState()
    val tracks by viewModel.tracks.collectAsState()
    val allPlaylists by viewModel.allPlaylists.collectAsState()
    val allTracks by viewModel.allTracks.collectAsState()

    var showAddToPlaylistDialog by remember { mutableStateOf<AudioTrack?>(null) }
    var showAddTracksToPlaylistDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlist?.name ?: stringResource(R.string.playlists)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.collapse))
                    }
                },
                actions = {
                    IconButton(onClick = { showAddTracksToPlaylistDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_to_playlist))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(tracks) { track ->
                TrackItem(
                    track = track,
                    onClick = { 
                        viewModel.playTrack(track)
                        onTrackClick(track)
                    },
                    onAddToPlaylist = {
                        showAddToPlaylistDialog = track
                    },
                    onAddToQueue = { viewModel.addToQueue(track) },
                    onPlayNext = { viewModel.playNext(track) }
                )
            }
        }
    }

    if (showAddToPlaylistDialog != null) {
        AddToPlaylistDialog(
            playlists = allPlaylists,
            onDismiss = { showAddToPlaylistDialog = null },
            onPlaylistSelected = { selectedPlaylist ->
                viewModel.addTrackToPlaylist(selectedPlaylist.id, showAddToPlaylistDialog!!)
                showAddToPlaylistDialog = null
            }
        )
    }

    if (showAddTracksToPlaylistDialog) {
        AddTracksToPlaylistDialog(
            allTracks = allTracks,
            onDismiss = { showAddTracksToPlaylistDialog = false },
            onTracksSelected = { selectedTracks ->
                viewModel.addTracksToCurrentPlaylist(selectedTracks)
                showAddTracksToPlaylistDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTracksToPlaylistDialog(
    allTracks: List<AudioTrack>,
    onDismiss: () -> Unit,
    onTracksSelected: (List<AudioTrack>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedTracks = remember { mutableStateListOf<AudioTrack>() }

    val filteredTracks = remember(searchQuery, allTracks) {
        if (searchQuery.isBlank()) {
            allTracks
        } else {
            allTracks.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.artist.contains(searchQuery, ignoreCase = true) ||
                        it.album.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_to_playlist)) },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    placeholder = { Text(stringResource(R.string.search)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.cancel))
                            }
                        }
                    },
                    singleLine = true
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredTracks) { track ->
                        val isSelected = selectedTracks.contains(track)
                        ListItem(
                            headlineContent = { Text(track.title) },
                            supportingContent = { Text(track.artist) },
                            leadingContent = {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedTracks.add(track)
                                        else selectedTracks.remove(track)
                                    }
                                )
                            },
                            modifier = Modifier.clickable {
                                if (isSelected) selectedTracks.remove(track)
                                else selectedTracks.add(track)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onTracksSelected(selectedTracks.toList()) },
                enabled = selectedTracks.isNotEmpty()
            ) {
                Text("Add (${selectedTracks.size})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
