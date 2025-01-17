package com.irlink.meritz.util.notification

import android.app.Notification
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import com.irlink.meritz.R

data class NotificationData(
    val groupId: String = NotificationGroups.SERVICE,
    val channelId: String = NotificationChannels.SERVICES,
    val defaults: Int = NotificationCompat.DEFAULT_ALL,
    val priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    val smallIcon: Int,
    val contentTitle: String,
    val contentText: String = "",
    val setTicker: String = contentText,
    val autoCancel: Boolean = true,
    val category: String = Notification.CATEGORY_MESSAGE,
    val visibility: Int = Notification.VISIBILITY_PUBLIC,
    val colorRes: Int = R.color.colorPrimary,
    val `when`: Long = System.currentTimeMillis(),
    val setShowWhen: Boolean = true,
    val setOngoing: Boolean = false,
    val isFullScreenIntent: Boolean = false,
    val pendingIntent: PendingIntent? = null,
    val fullScreenPendingIntent: PendingIntent? = null,
) {
    var vibrate: LongArray = longArrayOf()
}