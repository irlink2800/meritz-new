package com.irlink.meritz.observer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.meritz.util.LogUtil

class HeadSetPlugReceiver : BroadcastReceiver() {

    companion object {
        const val TAG: String = "HeadSetPlugReceiver"

        /**
         * 헤드셋 연결 재부팅 브로드캐스트 액션 필터.
         */
        fun createIntentFilter(): IntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtil.d(TAG, "onReceive.")
        if (intent?.action != Intent.ACTION_HEADSET_PLUG) {
            return
        }
    }

    /**
     * 리시버 등록.
     */
    fun register(context: Context) {
        LogUtil.d(TAG, "register.")
        context.registerReceiver(this, createIntentFilter())
    }

    /**
     * 리시버 해제.
     */
    fun unregister(context: Context) {
        LogUtil.d(TAG, "unregister.")
        context.unregisterReceiver(this)
    }

}