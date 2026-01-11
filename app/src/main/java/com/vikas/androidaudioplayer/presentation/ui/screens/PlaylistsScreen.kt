package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vikas.androidaudioplayer.domain.model.Playlist
import com.vikas.androidaudioplayer.presentation.viewmodel.PlaylistsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    onPlaylistClick: (String) -> Unit, // Pass ID
    viewModel: PlaylistsViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Playlists") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create Playlist")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(playlists) { playlist ->
                PlaylistItem(playlist = playlist, onClick = { onPlaylistClick(playlist.id) })
            }
        }

        if (showCreateDialog) {
            CreatePlaylistDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name ->
                    viewModel.createPlaylist(name)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun PlaylistItem(playlist: Playlist, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(playlist.name) },
        supportingContent = { Text("${playlist.trackCount} tracks") },
        leadingContent = {
            Icon(Icons.Default.MusicNote, contentDescription = null)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun CreatePlaylistDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Playlist") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onConfirm(text) }
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
