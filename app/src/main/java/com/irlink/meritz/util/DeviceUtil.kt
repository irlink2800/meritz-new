package com.irlink.meritz.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi

@SuppressLint("MissingPermission", "HardwareIds")
open class DeviceUtil(

    private val applicationContext: Context,
    private val formatUtil: FormatUtil

) {

    companion object {
        const val TAG = "DeviceUtil"
    }

    protected val telephonyManager: TelephonyManager by lazy {
        applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    protected val telecomManager: TelecomManager by lazy {
        applicationContext.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    }

    protected val subscriptionManager: SubscriptionManager
        @RequiresApi(value = Build.VERSION_CODES.LOLLIPOP_MR1)
        get() = applicationContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

    /**
     * 디바이스 모델 리턴.
     */
    open val model: String
        get() = Build.MODEL

    /**
     * 디바이스 브랜드 리턴.
     */
    open val brand: String
        get() = Build.BRAND

    /**
     * 디바이스 OS 버전 리턴.
     */
    open val os: String
        get() = Build.VERSION.RELEASE

    /**
     * 안드로이드 OS 버전에 따라, 10 이상은 usimNumber, 미만은 imei 리턴.
     */
    open val deviceId: String
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> androidId
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> usimNumber
            else -> imei
        }

    /**
     * 디바이스의 IMEI 리턴.
     */
    open val imei: String
        get() = try {
            telephonyManager.deviceId ?: ""
        } catch (e: Exception) {
//            LogUtil.exception(TAG, e)
            ""
        }

    /**
     * 디바이스의 USIM Serial Number 리턴.
     */
    open val usimNumber: String
        get() = try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ->
                    subscriptionManager.activeSubscriptionInfoList?.firstOrNull()?.iccId ?: ""

                else -> telephonyManager.simSerialNumber ?: ""
            }
        } catch (e: Exception) {
//            LogUtil.exception(TAG, e)
            ""
        }

    /**
     * 안드로이드 ID.
     * Android ~7: 영구 유지.
     * Android 8~: 공장초기화 후 재설정 됨.
     */
    open val androidId: String
        get() = Settings.Secure.getString(
            applicationContext.contentResolver, Settings.Secure.ANDROID_ID
        )

    /**
     * 통신사 정보
     */
    open val telecom: String
        get() = try {
            telephonyManager.networkOperatorName ?: ""
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            ""
        }

    /**
     * 디바이스의 전화번호 리턴.
     * ex)01012345678
     */
    open val phoneNumber: String
        get() = try {
            formatUtil.toLocalPhoneNumber(globalPhoneNumber)
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            ""
        }

    /**
     * 디바이스의 전화번호 리턴.
     * ex)821012345678
     */
    open val globalPhoneNumber: String
        get() = try {
            telephonyManager.line1Number ?: ""
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            ""
        }

    /**
     * 기본 전화 앱 리턴.
     */
    open val deviceCallApp: String
        get() = when (telecomManager.defaultDialerPackage) {
            "com.samsung.android.dialer", "com.samsung.android.contacts" -> "삼성 전화"
            "com.skt.prod.dialer" -> "T 전화"
            "com.irlink.meritz" -> "메리츠 모바일"
            else -> "알 수 없음"
        }

}