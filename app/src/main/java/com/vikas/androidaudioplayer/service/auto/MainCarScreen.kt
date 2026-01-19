package com.vikas.androidaudioplayer.service.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template

class MainCarScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val row = Row.Builder()
            .setTitle("Welcome to Your Audio Player")
            .addText("Enjoy your premium music experience.")
            .build()

        val pane = Pane.Builder()
            .addRow(row)
            .addAction(
                Action.Builder()
                    .setTitle("Open Library")
                    .setOnClickListener {
                        // Handled by the native media browser
                    }
                    .build()
            )
            .build()

        return PaneTemplate.Builder(pane)
            .setHeaderAction(Action.APP_ICON)
            .setTitle("Audio Player")
            .build()
    }
}
