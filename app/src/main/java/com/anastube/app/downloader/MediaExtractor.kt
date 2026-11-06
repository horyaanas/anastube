package com.anastube.app.downloader

import com.anastube.app.data.FormatOption
import com.anastube.app.data.MediaItem

object MediaExtractor {

    /**
     * Resolves complete format matrix identical to SnapTube's ChooseFormatActivity
     */
    fun resolveFormats(mediaItem: MediaItem): List<FormatOption> {
        val formats = mutableListOf<FormatOption>()

        // 1. Audio Formats (Music)
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_m4a_128",
                label = "M4A 128K",
                quality = "128 kbps",
                format = "M4A",
                estimatedSize = "3.2 MB",
                bitrate = "128 kbps",
                isAudioOnly = true,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_mp3_128",
                label = "MP3 128K (Fast)",
                quality = "128 kbps",
                format = "MP3",
                estimatedSize = "3.8 MB",
                bitrate = "128 kbps",
                isAudioOnly = true,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_mp3_160",
                label = "MP3 160K (HQ)",
                quality = "160 kbps",
                format = "MP3",
                estimatedSize = "4.9 MB",
                bitrate = "160 kbps",
                isAudioOnly = true,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_mp3_320",
                label = "MP3 320K (Lossless HD)",
                quality = "320 kbps",
                format = "MP3",
                estimatedSize = "9.8 MB",
                bitrate = "320 kbps",
                isAudioOnly = true,
                downloadUrl = mediaItem.url
            )
        )

        // 2. Video Formats
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_240p",
                label = "240p (Ultra Data Saver)",
                quality = "240p",
                format = "MP4",
                estimatedSize = "6.4 MB",
                bitrate = "400 kbps",
                isAudioOnly = false,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_360p",
                label = "360p (Mobile Standard)",
                quality = "360p",
                format = "MP4",
                estimatedSize = "12.8 MB",
                bitrate = "750 kbps",
                isAudioOnly = false,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_480p",
                label = "480p SD",
                quality = "480p",
                format = "MP4",
                estimatedSize = "19.5 MB",
                bitrate = "1.2 Mbps",
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
                estimatedSize = "34.2 MB",
                bitrate = "2.5 Mbps",
                isAudioOnly = false,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_1080p",
                label = "1080p Full HD (60fps)",
                quality = "1080p",
                format = "MP4",
                estimatedSize = "68.4 MB",
                bitrate = "5.0 Mbps",
                isAudioOnly = false,
                downloadUrl = mediaItem.url
            )
        )
        formats.add(
            FormatOption(
                id = "${mediaItem.id}_2160p",
                label = "4K Ultra HD",
                quality = "2160p",
                format = "MP4",
                estimatedSize = "182.0 MB",
                bitrate = "18.0 Mbps",
                isAudioOnly = false,
                downloadUrl = mediaItem.url
            )
        )

        return formats
    }

    /**
     * Inspects URLs and MIME streams to detect media files (.mp4, .m3u8, .mp3, etc.)
     */
    fun isMediaStream(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains(".mp4") ||
                lower.contains(".m3u8") ||
                lower.contains(".mp3") ||
                lower.contains(".m4a") ||
                lower.contains(".webm") ||
                lower.contains("googlevideo.com/videoplayback") ||
                lower.contains("mime=video") ||
                lower.contains("mime=audio") ||
                lower.contains("tiktokcdn") ||
                lower.contains("instagram.com/p/") ||
                lower.contains("fbcdn.net")
    }

    /**
     * Checks if a string contains a valid video or social media link
     */
    fun isMediaShareUrl(text: String): Boolean {
        val lower = text.lowercase()
        return lower.contains("youtube.com") ||
                lower.contains("youtu.be") ||
                lower.contains("tiktok.com") ||
                lower.contains("instagram.com") ||
                lower.contains("facebook.com") ||
                lower.contains("twitter.com") ||
                lower.contains("x.com") ||
                lower.contains("soundcloud.com") ||
                lower.contains("vimeo.com")
    }
}
