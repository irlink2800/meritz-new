package com.irlink.meritz.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.irlink.meritz.util.extension.emptyDisposable
import com.irlink.meritz.util.extension.toV3
import com.tedpark.tedonactivityresult.rx2.TedRxOnActivityResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

open class BatteryUtil(

    private val applicationContext: Context

) {

    companion object {
        const val TAG: String = "BatteryUtil"
    }

    protected val powerManager: PowerManager by lazy {
        applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    val wakeLock: PowerManager.WakeLock by lazy {
        powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag")
    }

    /**
     * 배터리 충전 상태.
     */
    open val isCharging: Boolean
        get() {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val register = applicationContext.registerReceiver(null, filter)

            val status = register?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

            return when (status) {
                BatteryManager.BATTERY_STATUS_FULL, BatteryManager.BATTERY_STATUS_CHARGING -> true
                else -> false
            }
        }

    /**
     * 디바이스의 배터리 정보 리턴.
     */
    open val batteryCapacity: Int
        get() {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = applicationContext.registerReceiver(null, filter)

            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            val batteryPct = level?.div(scale!!.toFloat())

            return (batteryPct?.times(100))?.toInt() ?: -1
        }

    /**
     * 앱을 배터리 최적화 대상에서 제외.
     */
    @SuppressLint("BatteryLife")
    open fun ignoreBatteryOptimizations(
        context: Context,
        callback: (isIgnored: Boolean) -> Unit
    ): Disposable {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return emptyDisposable()
        }
        val packageName: String = applicationContext.packageName
        val isIgnoringBatteryOptimizations: Boolean =
            powerManager.isIgnoringBatteryOptimizations(packageName)

        if (isIgnoringBatteryOptimizations) {
            callback(true)
            return emptyDisposable()
        }
        val intent: Intent = Intent().apply {
            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            data = Uri.parse("package:$packageName")
        }
        return TedRxOnActivityResult.with(context)
            .startActivityForResult(intent)
            .toV3()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    callback(powerManager.isIgnoringBatteryOptimizations(packageName))
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
    }

    /**
     * CPU 켜신 상태로 유지.
     */
    open fun acquireCpu(isEnable: Boolean) = when (isEnable) {
        true -> wakeLock.acquire()
        else -> wakeLock.release()
    }

}