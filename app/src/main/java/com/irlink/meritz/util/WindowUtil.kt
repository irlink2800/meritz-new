package com.irlink.meritz.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.irlink.meritz.R
import com.irlink.meritz.util.extension.runOnMainThread

open class WindowUtil(

    private val applicationContext: Context,
    private val displayUtil: DisplayUtil

) {

    companion object {
        const val TAG: String = "WindowUtil"
    }

    protected val windowManager: WindowManager by lazy {
        applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * 화면 입력 차단 뷰.
     */
    protected open val blockView: TextView by lazy {
        TextView(applicationContext).apply {
            text = applicationContext.getString(R.string.blocking_user_input)

            isClickable = true
            isFocusable = true

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.TOP or Gravity.START

            setTextColor(Color.GREEN)
            setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark))

            setPadding(displayUtil.dpToPx(10.0F).toInt())
        }
    }

    /**
     * 화면 차단 여부.
     */
    open val isBlockWindow: Boolean
        get() = blockView.isAttachedToWindow

    /**
     * 화면 입력 차단.
     */
    open fun block(isBlock: Boolean) = runOnMainThread {
        try {
            when (isBlock) {
                true -> if (!isBlockWindow) {
                    val layoutParams = WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                    ).apply {
                        format = PixelFormat.TRANSLUCENT
                        flags =
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        type = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                        } else {
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        }
                    }
                    windowManager.addView(blockView, layoutParams)
                }

                false -> if (isBlockWindow) {
                    windowManager.removeView(blockView)
                }
            }
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            windowManager.removeView(blockView)
        }
    }

    /**
     * 액티비티를 항상 켜진 상태로 설정.
     */
    open fun keepScreenOn(activity: Activity) = keepScreenOn(activity.window)

    /**
     * 화면을 항상 켜진 상태로 설정.
     */
    open fun keepScreenOn(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

}