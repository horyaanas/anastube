package com.anastube.app.data

data class MediaItem(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val duration: String,
    val author: String,
    val views: String,
    val date: String = "",
    val isLive: Boolean = false
)

enum class DownloadState {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class FormatOption(
    val id: String,
    val label: String,
    val quality: String,
    val format: String, // "MP4", "MP3", "M4A", "WEBM"
    val estimatedSize: String,
    val bitrate: String,
    val isAudioOnly: Boolean,
    val downloadUrl: String
)

data class DownloadItem(
    val id: String,
    val title: String,
    val sourceUrl: String,
    val destinationPath: String,
    val format: String,
    val quality: String,
    val isAudio: Boolean,
    val progress: Int = 0,
    val speed: String = "0 KB/s",
    val totalSize: String = "",
    val downloadedBytes: Long = 0,
    val totalBytes: Long = 0,
    val state: DownloadState = DownloadState.DOWNLOADING
)

data class BookmarkItem(
    val id: String,
    val name: String,
    val url: String,
    val iconColor: Long,
    val isCustom: Boolean = false
)

data class QuickToolItem(
    val title: String,
    val subtitle: String,
    val iconType: String
)
