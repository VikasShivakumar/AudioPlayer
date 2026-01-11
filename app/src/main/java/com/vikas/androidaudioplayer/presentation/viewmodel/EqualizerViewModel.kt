package com.vikas.androidaudioplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.vikas.androidaudioplayer.service.playback.EqualizerController
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EqualizerState(
    val bands: List<EqualizerBand> = emptyList(),
    val minLevel: Short = -1500,
    val maxLevel: Short = 1500
)

data class EqualizerBand(
    val index: Short,
    val centerFreq: Int,
    val level: Short
)

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val equalizerController: EqualizerController
) : ViewModel() {

    private val _uiState = MutableStateFlow(EqualizerState())
    val uiState: StateFlow<EqualizerState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            equalizerController.equalizerState.collect { equalizer ->
                if (equalizer != null) {
                    loadEqualizerSettings()
                } else {
                    _uiState.value = EqualizerState()
                }
            }
        }
    }

    private fun loadEqualizerSettings() {
        val bandCount = equalizerController.getBandCount()
        if (bandCount > 0) {
            val range = equalizerController.getBandLevelRange()
            val bands = (0 until bandCount).map { i ->
                val band = i.toShort()
                EqualizerBand(
                    index = band,
                    centerFreq = equalizerController.getCenterFreq(band),
                    level = equalizerController.getBandLevel(band)
                )
            }
            _uiState.value = EqualizerState(
                bands = bands,
                minLevel = range[0],
                maxLevel = range[1]
            )
        }
    }

    fun setBandLevel(band: Short, level: Short) {
        equalizerController.setBandLevel(band, level)
        _uiState.value = _uiState.value.copy(
            bands = _uiState.value.bands.map {
                if (it.index == band) it.copy(level = level) else it
            }
        )
    }
}
