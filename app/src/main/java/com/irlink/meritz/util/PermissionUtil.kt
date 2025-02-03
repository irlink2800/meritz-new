package com.irlink.meritz.util

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.util.extension.checkPermission
import com.irlink.meritz.util.extension.lifecycleContext
import com.irlink.meritz.util.message.MessageUtil
import io.reactivex.rxjava3.disposables.Disposable

class PermissionUtil(

    private val batteryUtil: BatteryUtil,
    private val directoryManager: DirectoryManager,
    private val messageUtil: MessageUtil

) {

    companion object {
        const val TAG: String = "PermissionUtil"
    }

    /**
     * 유저 퍼미션 허가.
     */
    fun checkPermission(lifecycleOwner: LifecycleOwner, onGranted: () -> Unit): Disposable =
        lifecycleOwner.checkPermission(
            permissions = mutableListOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.ANSWER_PHONE_CALLS
            ).toTypedArray(),
            onGranted = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestStoragePermission(lifecycleOwner, onGranted)
                } else {
                    requestBatteryPermission(lifecycleOwner, onGranted)
                }
            },
            onDenied = {
                checkPermission(lifecycleOwner, onGranted)
            }
        )

    /**
     * 안드로이드 11 저장소 권한.
     */
    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestStoragePermission(lifecycleOwner: LifecycleOwner, onGranted: () -> Unit) {
        directoryManager.requestPermission(lifecycleOwner.lifecycleContext) { isGranted ->
            when (isGranted) {
                true -> requestBatteryPermission(lifecycleOwner, onGranted)
                else -> requestStoragePermission(lifecycleOwner, onGranted)
            }
        }
    }

    /**
     * 배터리 최적화 제외 설정 요청.
     */
    private fun requestBatteryPermission(
        lifecycleOwner: LifecycleOwner,
        onGranted: () -> Unit
    ): Disposable =
        batteryUtil.ignoreBatteryOptimizations(lifecycleOwner.lifecycleContext) { isGranted ->
            when (isGranted) {
                true -> onGranted()
                false -> requestBatteryPermission(lifecycleOwner, onGranted)
            }
        }

    /**
     * 기본 문자 앱 설정.
     */
    private fun requestDefaultMessageApp(
        lifecycleOwner: LifecycleOwner,
        onGranted: () -> Unit
    ): Disposable =
        messageUtil.requestDefaultMessageApp(lifecycleOwner.lifecycleContext) { isGranted ->
            when (isGranted) {
                true -> onGranted()
                else -> requestDefaultMessageApp(lifecycleOwner, onGranted)
            }
        }

}