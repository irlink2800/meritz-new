package com.irlink.meritz.observer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.irlink.meritz.ui.screen.splash.SplashActivity
import com.irlink.meritz.util.LogUtil

class ReBootingReceiver : BroadcastReceiver() {

    companion object {
        const val TAG: String = "ReBootingReceiver"

        /**
         * 재부팅 브로드캐스트 액션 필터.
         */
        fun createIntentFilter(): IntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_BOOT_COMPLETED)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtil.d(TAG, "onReceive.")
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        onBootCompleted(context)
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

    /**
     * 재부팅 완료시 호출.
     */
    private fun onBootCompleted(context: Context?) {
        context?.let { SplashActivity.intent(it) }
    }

}