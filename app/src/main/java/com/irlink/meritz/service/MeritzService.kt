package com.irlink.meritz.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.irlink.meritz.R
import com.irlink.meritz.manager.CallManager
import com.irlink.meritz.manager.DeviceManager
import com.irlink.meritz.observer.HeadSetPlugReceiver
import com.irlink.meritz.observer.VolumeObserver
import com.irlink.meritz.util.network.socket.subscribeSocket
import com.irlink.meritz.ocx.OcxManager
import com.irlink.meritz.ocx.ocxExecute
import com.irlink.meritz.ui.screen.login.LoginActivity
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.base.livedata.EventObserver
import com.irlink.meritz.util.extension.timer
import com.irlink.meritz.util.notification.NotificationData
import com.irlink.meritz.util.notification.NotificationGroups
import com.irlink.meritz.util.notification.NotificationUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject

class MeritzService : LifecycleService() {

    object Extra {
        const val STATE: String = "MeritzService.Extra.STATE"
    }

    private object State {
        const val STOP: Int = 0
        const val START: Int = 1
    }

    companion object {
        const val TAG: String = "MeritzService"

        var IS_RUNNING: Boolean = false

        private fun createStateIntent(context: Context, state: Int): Intent =
            Intent(context, MeritzService::class.java).apply {
                putExtra(Extra.STATE, state)
            }

        fun start(context: Context) {
            intent(context, createStateIntent(context, State.START))
        }

        fun stop(context: Context) {
            if (!IS_RUNNING) {
                LogUtil.w(TAG, "already stopped service.")
                return
            }
            intent(context, createStateIntent(context, State.STOP))
        }

        fun restart(context: Context) {
            if (!IS_RUNNING) {
                return
            }
            LogUtil.d(TAG, "restart.")
            stop(context)
            timer(5000) {
                start(context)
            }
        }

        private fun intent(context: Context, intent: Intent) {
            context.startForegroundService(intent)
        }
    }

    private var isAutoRestart: Boolean = true

    private val deviceManager: DeviceManager by inject()
    private val callManager: CallManager by inject()

    private val ocxManager: OcxManager by inject()

    private val headSetPlugReceiver: HeadSetPlugReceiver by inject()
    private val volumeObserver: VolumeObserver by inject()

    private val notificationUtil: NotificationUtil by inject()

    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    /**
     * 서비스 시작.
     */
    override fun onCreate() {
        super.onCreate()
        LogUtil.d(TAG, "onCreate.")

        IS_RUNNING = true

        showNotification()

        registerHeadSetReceiver()
        registerVolumeObserver()

        initOcxManager()

        callManager.newCall()
    }

    /**
     * Foreground Service 실행을 위한 Notification 생성.
     */
    private fun showNotification() {
        notificationUtil.notify(
            NotificationGroups.SERVICE.hashCode(),
            notificationUtil.createSummaryNotification(
                data = NotificationData(
                    contentTitle = applicationContext.getString(R.string.app_name),
                    smallIcon = R.drawable.ic_launcher
                )
            )
        )
        startForeground(
            TAG.hashCode(), notificationUtil.createNotification(
                data = NotificationData(
                    contentTitle = applicationContext.getString(R.string.app_name),
                    smallIcon = R.drawable.ic_launcher,
                    pendingIntent = PendingIntent.getActivity(
                        this,
                        TAG.hashCode(),
                        LoginActivity.createIntent(this),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            )
        )
    }

    /**
     * 서비스 호출.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // state.
        when (intent?.getIntExtra(Extra.STATE, State.START)) {
            State.START -> {
                LogUtil.d(TAG, "onStartCommand. Extra.STATE: Start")
                isAutoRestart = true
            }

            State.STOP -> {
                LogUtil.d(TAG, "onStartCommand. Extra.STATE: Stop")
                isAutoRestart = false
                stopService(Intent(applicationContext, MeritzService::class.java))
                return START_NOT_STICKY
            }

            else -> LogUtil.d(TAG, "onStartCommand.")
        }
        return START_STICKY
    }


    /**
     * Ocx 소켓 생성.
     */
    private fun openOcxSocket() = ocxManager.open()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .onBackpressureBuffer()
        .subscribeSocket(ocxManager)

    /**
     * Ocx 매니저 초기화.
     */
    private fun initOcxManager() {
        ocxManager.socketState.observe(this) { socketState ->
            when (socketState) {
//                SocketState.Closed -> getGenerate204UseCase.request(Unit) { response ->
//                    LogUtil.d(TAG, "getGenerate204UseCase: $response")
//                }
                else -> Unit
            }
        }
        deviceManager.isLogin.observe(this) {
            if (it) openOcxSocket() else closeOcxSocket()
        }
    }

    /**
     * 헤드셋 옵저버 등록.
     */
    private fun registerHeadSetReceiver() {
        headSetPlugReceiver.register(applicationContext)
    }

    /**
     * 볼륨 옵저버 등록.
     */
    private fun registerVolumeObserver() {
        volumeObserver.onChangedVolume.observe(this, EventObserver {
            ocxManager.onGetVolume(isMaxVolume = false)
        })
        volumeObserver.register()
    }

    /**
     * 서비스 종료.
     */
    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(TAG, "onDestroy.")

        IS_RUNNING = false

        notificationUtil.cancel(NotificationGroups.SERVICE.hashCode())

        stopForeground(true)

        unregisterHeadSetReceiver()
        unregisterVolumeObserver()

        closeOcxSocket()

        compositeDisposable.clear()

        if (isAutoRestart) {
            start(applicationContext)
        }
    }

    /**
     * Ocx 소켓 종료.
     */
    private fun closeOcxSocket() = ocxManager.close()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onBackpressureBuffer()
        .ocxExecute()

    /**
     * 헤드셋 옵저버 해제.
     */
    private fun unregisterHeadSetReceiver() {
        headSetPlugReceiver.unregister(applicationContext)
    }

    /**
     * 볼륨 옵저버 해제.
     */
    private fun unregisterVolumeObserver() {
        volumeObserver.unregister()
    }

//    /**
//     * FCM 토큰 업데이트.
//     */
//    private fun updateFcmToken() = FcmService.getFcmToken { token ->
//        LogUtil.d(FcmService.TAG, "updateFcmToken: $token")
//
//        val request = UpdateFcmTokenUseCase.Request(
//            token = token ?: return@getFcmToken
//        )
//        updateFcmTokenUseCase.request(request) {
//            LogUtil.d(FcmService.TAG, "onNewToken: [${it.code}] ${it.message}")
//        }
//    }
//
//    /**
//     * 디바이스 정보 로그.
//     */
//    private fun loggingDeviceInfo() = JSONObject().apply {
//        put("os", deviceUtil.os)
//        put("brand", deviceUtil.brand)
//        put("device_model", deviceUtil.model)
//        put("phone_number", deviceUtil.phoneNumber)
//        put("telecom", deviceUtil.telecom)
//        put("deviceId", deviceUtil.deviceId)
//        put("app_version", "${deviceUtil.versionName}(${BuildConfig.VERSION_CODE})")
//        put(
//            "build_type",
//            "${appPref.appType}(${BuildConfig.BUILD_TYPE.toUpperCase(Locale.getDefault())})"
//        )
//    }.run {
//        LogUtil.i(App.TAG, toString(4))
//    }
}