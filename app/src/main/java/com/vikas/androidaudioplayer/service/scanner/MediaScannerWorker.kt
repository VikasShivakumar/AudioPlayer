package com.vikas.androidaudioplayer.service.scanner

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vikas.androidaudioplayer.domain.usecase.ScanMediaUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class MediaScannerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scanMediaUseCase: ScanMediaUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting media scan")
            scanMediaUseCase().getOrThrow()
            Timber.d("Media scan completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Media scan failed")
            Result.retry()
        }
    }
}
