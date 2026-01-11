package com.vikas.androidaudioplayer.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vikas.androidaudioplayer.presentation.viewmodel.NowPlayingViewModel

@Composable
fun MiniPlayer(
    onClick: () -> Unit,
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val mediaItem = state.mediaItem

    if (mediaItem != null) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable(onClick = onClick),
            tonalElevation = 8.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mediaItem.mediaMetadata.title?.toString() ?: "Unknown Title",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mediaItem.mediaMetadata.artist?.toString() ?: "Unknown Artist",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                IconButton(onClick = { viewModel.togglePlayPause() }) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play"
                    )
                }
            }
            
            // Progress indicator at the bottom?
            // LinearProgressIndicator(progress = ...)
        }
    }
}
