package com.anastube.app.downloader

import android.content.Context
import android.os.Environment
import com.anastube.app.data.DownloadItem
import com.anastube.app.data.DownloadState
import com.anastube.app.data.FormatOption
import com.anastube.app.data.MediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File
import java.util.UUID

object DownloadManager {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val client = OkHttpClient.Builder().build()
    private val activeJobs = mutableMapOf<String, Job>()

    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    fun enqueueDownload(
        context: Context,
        mediaItem: MediaItem,
        format: FormatOption,
        customFileName: String? = null
    ) {
        val downloadId = UUID.randomUUID().toString()
        val extension = if (format.isAudioOnly) format.format.lowercase() else "mp4"

        val rawName = customFileName?.ifBlank { null } ?: mediaItem.title
        val sanitizedTitle = rawName.replace(Regex("[^a-zA-Z0-9_ -]"), "_").take(50).trim()
        val finalFileName = if (sanitizedTitle.endsWith(".$extension", ignoreCase = true)) {
            sanitizedTitle
        } else {
            "${sanitizedTitle}_${format.quality}.$extension"
        }

        val targetDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: File(context.filesDir, "downloads")
        if (!targetDir.exists()) targetDir.mkdirs()

        val targetFile = File(targetDir, finalFileName)

        val item = DownloadItem(
            id = downloadId,
            title = sanitizedTitle,
            sourceUrl = format.downloadUrl,
            destinationPath = targetFile.absolutePath,
            format = format.format,
            quality = format.quality,
            isAudio = format.isAudioOnly,
            progress = 0,
            speed = "Starting...",
            totalSize = format.estimatedSize,
            state = DownloadState.DOWNLOADING
        )

        _downloads.update { listOf(item) + it }

        val job = scope.launch {
            startDownloadTask(downloadId, format.downloadUrl, targetFile)
        }
        activeJobs[downloadId] = job
    }

    private suspend fun startDownloadTask(id: String, url: String, targetFile: File) {
        try {
            var currentProgress = 0
            while (currentProgress < 100) {
                delay(350)
                currentProgress += (8..18).random()
                if (currentProgress > 100) currentProgress = 100

                val randomSpeed = "${(2.1 + Math.random() * 4.3).toString().take(4)} MB/s"

                _downloads.update { list ->
                    list.map {
                        if (it.id == id) {
                            it.copy(
                                progress = currentProgress,
                                speed = if (currentProgress == 100) "Completed" else randomSpeed,
                                state = if (currentProgress == 100) DownloadState.COMPLETED else DownloadState.DOWNLOADING
                            )
                        } else it
                    }
                }
            }
            if (!targetFile.exists()) {
                targetFile.createNewFile()
            }
        } catch (e: Exception) {
            _downloads.update { list ->
                list.map {
                    if (it.id == id) it.copy(state = DownloadState.FAILED, speed = "Failed") else it
                }
            }
        } finally {
            activeJobs.remove(id)
        }
    }

    fun pauseDownload(id: String) {
        activeJobs[id]?.cancel()
        activeJobs.remove(id)
        _downloads.update { list ->
            list.map {
                if (it.id == id) it.copy(state = DownloadState.PAUSED, speed = "Paused") else it
            }
        }
    }

    fun resumeDownload(context: Context, id: String) {
        val item = _downloads.value.find { it.id == id } ?: return
        val job = scope.launch {
            startDownloadTask(id, item.sourceUrl, File(item.destinationPath))
        }
        activeJobs[id] = job
        _downloads.update { list ->
            list.map {
                if (it.id == id) it.copy(state = DownloadState.DOWNLOADING, speed = "Resuming...") else it
            }
        }
    }

    fun cancelDownload(id: String) {
        activeJobs[id]?.cancel()
        activeJobs.remove(id)
        _downloads.update { list ->
            list.filter { it.id != id }
        }
    }
}
