package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vikas.androidaudioplayer.presentation.viewmodel.SettingsViewModel
import com.vikas.androidaudioplayer.util.Formatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onEqualizerClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val remainingSleepTime by viewModel.remainingSleepTime.collectAsState()
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Theme Setting
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dark Theme",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.toggleTheme(it) }
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Sleep Timer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSleepTimerDialog = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Sleep Timer", style = MaterialTheme.typography.bodyLarge)
                    remainingSleepTime?.let {
                        Text(
                            text = "Stopping in ${Formatter.formatDuration(it)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Equalizer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEqualizerClick() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Equalizer", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    if (showSleepTimerDialog) {
        SleepTimerDialog(
            onDismiss = { showSleepTimerDialog = false },
            onSetTimer = { minutes ->
                viewModel.setSleepTimer(minutes)
                showSleepTimerDialog = false
            },
            onCancelTimer = {
                viewModel.cancelSleepTimer()
                showSleepTimerDialog = false
            }
        )
    }
}

@Composable
fun SleepTimerDialog(
    onDismiss: () -> Unit,
    onSetTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Sleep Timer") },
        text = {
            Column {
                listOf(15, 30, 45, 60).forEach { minutes ->
                    TextButton(
                        onClick = { onSetTimer(minutes) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("$minutes minutes")
                    }
                }
                TextButton(
                    onClick = onCancelTimer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Turn Off Timer", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
