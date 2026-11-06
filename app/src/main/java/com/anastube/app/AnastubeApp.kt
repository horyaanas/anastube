package com.anastube.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AnastubeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Playback Channel
            val playbackChannel = NotificationChannel(
                CHANNEL_PLAYBACK,
                "anastube Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows media playback controls in notification"
            }

            // Downloads Channel
            val downloadChannel = NotificationChannel(
                CHANNEL_DOWNLOADS,
                "anastube Downloads",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows progress and completion of media downloads"
            }

            manager?.createNotificationChannel(playbackChannel)
            manager?.createNotificationChannel(downloadChannel)
        }
    }

    companion object {
        const val CHANNEL_PLAYBACK = "anastube_playback_channel"
        const val CHANNEL_DOWNLOADS = "anastube_downloads_channel"
    }
}
