package com.irlink.meritz.service

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.irlink.meritz.domain.firebase.UpdateFcmTokenUseCase
import com.irlink.meritz.domain.firebase.UploadLogsUseCase
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.extension.parseDate
import org.koin.android.ext.android.inject
import java.util.Date

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FcmService : FirebaseMessagingService() {

    companion object {
        const val TAG: String = "FcmService"

        /**
         * 현재 토큰 리턴.
         */
        fun getFcmToken(callback: (token: String?) -> Unit) {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    it.result?.let { token ->
                        LogUtil.d(TAG, "token: $token")
                        callback(token)
                    }
                }
                .addOnCanceledListener {
                    callback(null)
                }.addOnFailureListener {
                    LogUtil.exception(TAG, it)
                    callback(null)
                }
        }
    }

    /**
     * 이벤트 타입.
     */
    object Type {
        const val UPLOAD_LOGS: Int = 0
        const val SEND_RECORD_FILE: Int = 1
        const val FORCE_SEND_RECORD_FILE: Int = 2
    }

    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase by inject()
    private val uploadLogsUseCase: UploadLogsUseCase by inject()

    /**
     * 메시지 수신.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) = try {
        LogUtil.d(TAG, "onMessageReceived.")
        when (remoteMessage.data["sendType"]?.toIntOrNull()) {
            Type.UPLOAD_LOGS -> onUploadLogs(
                date = remoteMessage.data["date"]
            )

            else -> Unit
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
    }

    /**
     * 로그 업로드.
     */
    private fun onUploadLogs(date: String?) {
        LogUtil.d(TAG, "onUploadLogs. date: $date")
        val request = UploadLogsUseCase.Request(
            date = (date parseDate "yyyy-MM-dd") ?: Date()
        )
        uploadLogsUseCase.request(request) {
            LogUtil.d(TAG, "onUploadLogs: [${it.code}] ${it.message}")
        }
    }

    /**
     * 녹취 파일 재전송.
     */
    private fun onSendRecordFile() {
        // TODO amr 전송 기능 구현
    }

    /**
     * 원본 파일 변환 후 재전송.
     */
    private fun onForceSendRecordFile() {
        // TODO 원본 파일 변환 후 전송하는 기능 구현
    }

    /**
     * 토큰 갱신.
     */
    override fun onNewToken(token: String) {
        LogUtil.d(TAG, "onNewToken: $token")

        val request = UpdateFcmTokenUseCase.Request(
            token = token
        )
        updateFcmTokenUseCase.request(request) {
            LogUtil.d(TAG, "onNewToken: [${it.code}] ${it.message}")
        }
    }

}