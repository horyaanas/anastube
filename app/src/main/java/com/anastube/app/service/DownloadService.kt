package com.anastube.app.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.anastube.app.AnastubeApp
import com.anastube.app.R

class DownloadService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("anastube Downloader is running in background")
        startForeground(NOTIFICATION_ID, notification)
        return START_NOT_STICKY
    }

    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, AnastubeApp.CHANNEL_DOWNLOADS)
            .setSmallIcon(R.drawable.ic_anastube_logo)
            .setContentTitle("anastube Downloads")
            .setContentText(text)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 2001
    }
}
