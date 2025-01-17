package com.irlink.meritz.util.call

import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.extension.emptyDisposable
import com.irlink.meritz.util.extension.runOnBackgroundThread
import com.irlink.meritz.util.extension.toV3
import com.tedpark.tedonactivityresult.rx2.TedRxOnActivityResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.reflect.Method

@SuppressLint("MissingPermission")
open class CallUtil(

    private val applicationContext: Context

) {

    companion object {
        const val TAG: String = "CallUtil"
    }

    protected open val roleManager: RoleManager
        @RequiresApi(Build.VERSION_CODES.Q)
        get() = applicationContext.getSystemService(RoleManager::class.java)!!

    protected val telecomManager: TelecomManager by lazy {
        applicationContext.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    }

    protected val telephonyManager: TelephonyManager by lazy {
        applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    /**
     * 현재 앱이 기본 전화앱인지 체크.
     */
    open val isDefaultCallApp: Boolean
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
            else -> telecomManager.defaultDialerPackage == applicationContext.packageName
        }


    /**
     * 전화 기록 개수 구하기.
     */
    open fun getCallHistoryCount(): Int {
        var managedCursor: Cursor? = null
        return try {
            managedCursor = createCallHistoryCursor()
            managedCursor?.count ?: 0

        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            0

        } finally {
            managedCursor?.close()
        }
    }

    /**
     * 비동기 getCallHistoryCount.
     */
    open fun getCallHistoryCount(response: (historyCount: Int) -> Unit): Disposable = runOnBackgroundThread {
        response(getCallHistoryCount())
    }

    /**
     * 마지막 순수 통화 시간 구하기 (스마트폰 통화 이력).
     */
    open fun getLastCallHistory(lastIndex: Int = 0): CallHistory {
        val callHistory: CallHistory = CallHistory()
        var cursor: Cursor? = null

        try {
            cursor = createCallHistoryCursor(lastIndex + 1)
            if (cursor != null && cursor.count > 0) {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                        cursor.move(lastIndex + 1)
                    else ->
                        cursor.moveToLast()
                }

                val idColumnIndex = cursor.getColumnIndex(CallLog.Calls._ID)
                val durationColumnIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
                val remoteNumberColumnIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)

                callHistory.id = cursor.getLongOrNull(idColumnIndex)
                callHistory.duration = cursor.getStringOrNull(durationColumnIndex)?.toLongOrNull()
                callHistory.remoteNumber = cursor.getStringOrNull(remoteNumberColumnIndex)
            }
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)

        } finally {
            cursor?.close()
        }
        return callHistory
    }

    /**
     * 비동기 getLastCallHistory.
     */
    open fun getLastCallHistory(lastIndex: Int = 0, response: (CallHistory) -> Unit): Disposable = runOnBackgroundThread {
        response(getLastCallHistory(lastIndex))
    }

    /**
     * 전화 기록 커서 생성.
     */
    protected open fun createCallHistoryCursor(limitCount: Int = 0): Cursor? {
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    return applicationContext.contentResolver.query(
                        CallLog.Calls.CONTENT_URI,
                        null,
                        Bundle().apply {
                            putInt(
                                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                            )
                            putStringArray(
                                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                                arrayOf(CallLog.Calls.DATE)
                            )
                            putInt(ContentResolver.QUERY_ARG_LIMIT, limitCount)
                        },
                        null
                    )
                }
                else -> {
                    return applicationContext.contentResolver.query(
                        CallLog.Calls.CONTENT_URI,
                        null,
                        null,
                        null,
                        CallLog.Calls.DATE + " DESC" + when {
                            limitCount > 0 -> " LIMIT $limitCount"
                            else -> ""
                        } + ";"
                    )
                }
            }
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            return null
        }
    }

    /**
     * 전화 기록 삭제.
     */
    open fun deleteCallHistories() = applicationContext.contentResolver.delete(
        CallLog.Calls.CONTENT_URI, null, null
    )

    /**
     * 전화 연결 받기
     */
    @SuppressLint("NewApi")
    open fun connectCall(): Boolean = try {
        LogUtil.d(TAG, "connectCall.")
        telecomManager.acceptRingingCall()
        true

    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        false
    }

    /**
     * 전화 연결 끊기
     */
    open fun disconnectCall(): Boolean = try {
        LogUtil.d(TAG, "disconnectCall.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            telecomManager.endCall()

        } else {
            var getClass: Class<*> = Class.forName(telephonyManager.javaClass.name)
            var getMethod: Method = getClass.getDeclaredMethod("getITelephony").apply {
                isAccessible = true
            }
            val telephonyService = getMethod.invoke(telephonyManager)

            getClass = Class.forName(telephonyService.javaClass.name)
            getMethod = getClass.getDeclaredMethod("endCall").apply {
                isAccessible = true
            }
            getMethod.invoke(telephonyService)
            true
        }

    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        false
    }

    /**
     * 기본 전화 앱 설정.
     */
    open fun requestDefaultCallApp(context: Context, onCallback: (isGranted: Boolean) -> Unit): Disposable {
        if (isDefaultCallApp) {
            onCallback(true)
            return emptyDisposable()
        }
        val intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            }
            else -> Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, applicationContext.packageName)
            }
        }
        return TedRxOnActivityResult.with(context)
            .startActivityForResult(intent)
            .toV3()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    onCallback(isDefaultCallApp)
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
    }
}