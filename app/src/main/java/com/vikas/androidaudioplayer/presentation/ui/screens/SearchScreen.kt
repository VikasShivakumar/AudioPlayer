package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.vikas.androidaudioplayer.presentation.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onTrackClick: (AudioTrack) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val playlists by viewModel.playlists.collectAsState()

    var showAddToPlaylistDialog by remember { mutableStateOf<AudioTrack?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = viewModel::onQueryChange,
            onSearch = {},
            active = false,
            onActiveChange = {},
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.cancel))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {}

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(results) { track ->
                TrackItem(
                    track = track,
                    onClick = { 
                        viewModel.playTrack(track)
                        onTrackClick(track)
                    },
                    onAddToPlaylist = { showAddToPlaylistDialog = track },
                    onAddToQueue = { viewModel.addToQueue(track) },
                    onPlayNext = { viewModel.playNext(track) }
                )
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
