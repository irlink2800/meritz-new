package com.irlink.meritz.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

object OuterActivities {

    const val TAG: String = "OuterActivities"

    /**
     * 전화 걸기.
     */
    fun intentCall(
        context: Context,
        remoteNumber: String
    ) {
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.parse("tel:${Uri.encode(remoteNumber.replace("-", ""))}")
            }
            context.startActivity(intent)

        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
        }
    }

    /**
     * 오디오 플레이어 열기.
     */
    fun intentAudioPlayer(
        context: Context,
        file: File
    ) = context.startActivity(
        Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(file.toProvideUri(context), "audio/*")
        }
    )

}