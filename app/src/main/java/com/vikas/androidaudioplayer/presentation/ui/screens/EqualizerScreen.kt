package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vikas.androidaudioplayer.presentation.viewmodel.EqualizerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    onBackClick: () -> Unit,
    viewModel: EqualizerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equalizer") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.bands.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Equalizer not available")
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(state.bands) { band ->
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(64.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${band.centerFreq / 1000} Hz",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Slider(
                                value = band.level.toFloat(),
                                onValueChange = { viewModel.setBandLevel(band.index, it.toInt().toShort()) },
                                valueRange = state.minLevel.toFloat()..state.maxLevel.toFloat(),
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer {
                                        rotationZ = -90f
                                    }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "${band.level / 100} dB",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
