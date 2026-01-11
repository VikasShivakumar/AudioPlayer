package com.vikas.androidaudioplayer.domain.usecase

import com.vikas.androidaudioplayer.domain.repository.MediaRepository
import javax.inject.Inject

class ScanMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return mediaRepository.scanMedia()
    }
}
