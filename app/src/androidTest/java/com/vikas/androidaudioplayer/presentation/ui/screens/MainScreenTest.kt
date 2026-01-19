package com.vikas.androidaudioplayer.presentation.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.vikas.androidaudioplayer.presentation.ui.theme.AndroidAudioPlayerTheme
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainScreen_rendersBottomNavigation() {
        // Given
        composeTestRule.setContent {
            AndroidAudioPlayerTheme {
                MainScreen()
            }
        }

        // Then
        composeTestRule.onNodeWithText("Library").assertExists()
        composeTestRule.onNodeWithText("Playlists").assertExists()
        composeTestRule.onNodeWithText("Search").assertExists()
        composeTestRule.onNodeWithText("Settings").assertExists()
    }
}
