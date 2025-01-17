package com.irlink.meritz.util.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.irlink.meritz.R
import com.irlink.meritz.util.LogUtil


class NotificationUtil(
    val applicationContext: Context
) {

    companion object {
        const val TAG: String = "NotificationUtil"
    }

    private val instance: NotificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val sound: Uri by lazy {
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    private val audioAttributes: AudioAttributes by lazy {
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    }

    private val vibrate: LongArray by lazy {
        longArrayOf(1000, 1000)
    }

    private val lightColor: Int by lazy {
        ContextCompat.getColor(applicationContext, R.color.colorAccent)
    }

    /**
     * 앱 기본 그룹과 채널을 하나씩 생성
     */
    fun init() {
        createChannelGroup(
            id = NotificationGroups.SERVICE,
            name = applicationContext.getString(R.string.notification_service_group_name)
        )
        createChannel(
            id = NotificationChannels.SERVICES,
            name = applicationContext.getString(R.string.notification_service_channel_name),
            groupId = NotificationGroups.SERVICE,
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    /**
     * 노티피케이션 그룹 생성
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createChannelGroup(
        id: String,
        name: String
    ): NotificationChannelGroup = NotificationChannelGroup(id, name).also {
        instance.createNotificationChannelGroup(it)
    }

    /**
     * 노티피케이션 채널 생성
     */
    @JvmOverloads
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createChannel(
        id: String,
        name: String,
        description: String = name,
        groupId: String,
        sound: Uri = this.sound,
        audioAttributes: AudioAttributes = this.audioAttributes,
        vibrate: LongArray = this.vibrate,
        lightColor: Int = this.lightColor,
        isShowBadge: Boolean = true,
        importance: Int = NotificationManager.IMPORTANCE_HIGH,
        lockScreenVisible: Int = Notification.VISIBILITY_PUBLIC
    ): NotificationChannel = NotificationChannel(id, name, importance).also {
        it.group = groupId
        it.description = description
        it.lockscreenVisibility = lockScreenVisible
        it.vibrationPattern = vibrate
        it.lightColor = lightColor
        it.setShowBadge(isShowBadge)
        it.setSound(sound, audioAttributes)
        instance.createNotificationChannel(it)
    }

    @SuppressLint("WrongConstant")
    fun createNotificationBuilder(data: NotificationData): NotificationCompat.Builder =
        NotificationCompat.Builder(applicationContext, data.channelId).setGroup(data.groupId)
            .setSmallIcon(data.smallIcon)
            .setContentTitle(data.contentTitle)
            .setDefaults(data.defaults)
            .setPriority(data.priority)
            .setCategory(data.category)
            .setVisibility(data.visibility)
            .setWhen(data.`when`)
            .setShowWhen(data.setShowWhen)
            .setAutoCancel(data.autoCancel)
            .setColor(ContextCompat.getColor(applicationContext, data.colorRes))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(data.contentTitle)
                    .bigText(data.contentText)
            )
            .apply {
                if (data.contentText.isNotEmpty()) {
                    setContentText(data.contentText)
                    setTicker(data.contentText)
                }
            }

    /**
     * 노티피케이션 객체를 만듬
     */
    fun createNotification(data: NotificationData): Notification =
        createNotificationBuilder(data).apply {
            if (data.isFullScreenIntent) {
                this.setFullScreenIntent(data.fullScreenPendingIntent, true)
            } else {
                this.setContentIntent(data.pendingIntent)
            }
        }.build()

    fun createSummaryNotification(data: NotificationData): Notification =
        createNotificationBuilder(data)
            .setGroupSummary(true)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .build()

    /**
     * 노티피케이션 객체를 만듬과 동시에 띄우기
     */
    fun notify(data: NotificationData) = notify(
        id = data.hashCode(),
        groupId = data.groupId,
        notification = createNotification(data)
    )

    /**
     * 노티피케이션을 띄움.
     */
    fun notify(id: Int, groupId: String, notification: Notification) {
        notify(
            groupId.hashCode(),
            createSummaryNotification(
                NotificationData(
                    contentTitle = applicationContext.getString(R.string.app_name),
                    smallIcon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        notification.smallIcon?.resId ?: R.drawable.ic_notifications
                    } else {
                        notification.icon

                    }
                )
            )
        )
        notify(id, notification)
    }

    /**
     * 노티피케이션을 띄움.
     */
    fun notify(id: Int, notification: Notification) {
        instance.notify(id, notification)
    }

    /**
     * 노티피케이션 캔슬.
     */
    fun cancel(id: Int) {
        instance.cancel(id)
    }

    /**
     * Notification Dismiss Receiver
     */
    class NotificationDismissReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            LogUtil.d(TAG, "NotificationDismissReceiver.onReceive()")
        }
    }

}