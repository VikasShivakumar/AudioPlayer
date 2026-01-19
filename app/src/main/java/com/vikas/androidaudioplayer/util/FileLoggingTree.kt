package com.vikas.androidaudioplayer.util

import android.content.Context
import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLoggingTree(private val context: Context) : Timber.Tree() {

    private val logDir: File by lazy {
        val dir = File(context.filesDir, "logs")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            val fileName = "log_${SimpleDateFormat("yyyy_MM_dd", Locale.US).format(Date())}.txt"
            val file = File(logDir, fileName)
            
            val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
            val priorityStr = when (priority) {
                android.util.Log.VERBOSE -> "V"
                android.util.Log.DEBUG -> "D"
                android.util.Log.INFO -> "I"
                android.util.Log.WARN -> "W"
                android.util.Log.ERROR -> "E"
                android.util.Log.ASSERT -> "A"
                else -> "?"
            }
            
            val logMessage = "$timestamp $priorityStr/$tag: $message\n"
            
            FileWriter(file, true).use { writer ->
                writer.append(logMessage)
                t?.printStackTrace(java.io.PrintWriter(writer))
            }
        } catch (e: Exception) {
            // Failed to log to file
        }
    }
    
    fun cleanOldLogs(maxDays: Int = 7) {
        try {
            val files = logDir.listFiles() ?: return
            val currentTime = System.currentTimeMillis()
            val maxAge = maxDays * 24 * 60 * 60 * 1000L
            
            files.forEach { file ->
                if (file.lastModified() < currentTime - maxAge) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
             // Failed to clean logs
        }
    }
}
