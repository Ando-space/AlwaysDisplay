package com.example.alwaysdisplay.media

import android.app.Notification
import android.content.Intent
import android.media.session.MediaSession
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * 通知监听服务
 * 监听系统中所有媒体通知，提取MediaSession信息
 */
class MediaListenerService : NotificationListenerService() {

    companion object {
        @Volatile
        private var instance: MediaListenerService? = null

        val bridge: MediaSessionBridge?
            get() = instance?.mediaSessionBridge
    }

    private val mediaSessionBridge = MediaSessionBridge()

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this

        // 从已有通知中恢复媒体会话
        activeNotifications?.forEach { sbn ->
            extractMediaSession(sbn)
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        mediaSessionBridge.release()
        instance = null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        sbn?.let { extractMediaSession(it) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        sbn?.let {
            mediaSessionBridge.removeSession(it.packageName)
        }
    }

    private fun extractMediaSession(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras ?: return
        val token = extras.getParcelable(
            Notification.EXTRA_MEDIA_SESSION,
            MediaSession.Token::class.java
        ) ?: return

        mediaSessionBridge.connect(this, sbn.packageName, token)
    }
}
