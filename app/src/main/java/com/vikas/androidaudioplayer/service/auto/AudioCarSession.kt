package com.vikas.androidaudioplayer.service.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class AudioCarSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return MainCarScreen(carContext)
    }
}
