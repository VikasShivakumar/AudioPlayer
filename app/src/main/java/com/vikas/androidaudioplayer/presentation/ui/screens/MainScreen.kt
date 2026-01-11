package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.vikas.androidaudioplayer.presentation.navigation.NavigationGraph
import com.vikas.androidaudioplayer.presentation.navigation.Screen
import com.vikas.androidaudioplayer.presentation.ui.components.BottomNavigationBar
import com.vikas.androidaudioplayer.presentation.ui.components.MiniPlayer

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            Column {
                MiniPlayer(
                    onClick = {
                        navController.navigate(Screen.NowPlaying.route)
                    }
                )
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
