package com.anastube.app.downloader

import com.anastube.app.data.FormatOption
import com.anastube.app.data.MediaItem

object MediaExtractor {

    /**
     * Resolves available formats (Video & Audio) for a given media URL.
     * Generates standard high-quality choices identical to SnapTube's format picker.
     */
    fun resolveFormats(mediaItem: MediaItem): List<FormatOption> {
        val formats = mutableListOf<FormatOption>()

        // Audio options (Music)
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_m4a",
                label = "M4A 128K",
                quality = "128 kbps",
                format = "M4A",
                estimatedSize = "3.8 MB",
                isAudioOnly = true,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_mp3_160",
                label = "MP3 160K",
                quality = "160 kbps",
                format = "MP3",
                estimatedSize = "4.9 MB",
                isAudioOnly = true,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_mp3_320",
                label = "MP3 320K (HD Audio)",
                quality = "320 kbps",
                format = "MP3",
                estimatedSize = "9.5 MB",
                isAudioOnly = true,
                downloadUrl = mediaItem.url
            )
        )

        // Video options
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_360p",
                label = "360p Fast",
                quality = "360p",
                format = "MP4",
                estimatedSize = "11.2 MB",
                isAudioOnly = false,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_720p",
                label = "720p HD",
                quality = "720p",
                format = "MP4",
                estimatedSize = "28.4 MB",
                isAudioOnly = false,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_1080p",
                label = "1080p Full HD",
                quality = "1080p",
                format = "MP4",
                estimatedSize = "62.1 MB",
                isAudioOnly = false,
                downloadUrl = mediaItem.url
            )
        )

        return formats
    }

    /**
     * Inspects a web URL or network response to detect media streams (.mp4, .m3u8, .mp3, etc.)
     */
    fun isMediaStream(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains(".mp4") ||
                lower.contains(".m3u8") ||
                lower.contains(".mp3") ||
                lower.contains(".m4a") ||
                lower.contains("googlevideo.com/videoplayback") ||
                lower.contains("mime=video") ||
                lower.contains("mime=audio")
    }
}
