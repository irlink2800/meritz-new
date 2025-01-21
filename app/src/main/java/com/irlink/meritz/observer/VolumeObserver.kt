package com.irlink.meritz.observer

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.meritz.util.AudioUtil
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.base.livedata.Event
import com.irlink.meritz.util.extension.postNotify

class VolumeObserver(

    private val applicationContext: Context,
    private val audioUtil: AudioUtil

) : ContentObserver(Handler()) {

    companion object {
        const val TAG: String = "VolumeObserver"

        const val HEADSET: String = "volume_voice_headset"
        const val EARPIECE: String = "volume_voice_earpiece"
    }

    /**
     * 볼륨 변경 이벤트.
     */
    private val _onChangedVolume: MutableLiveData<Event<Int>> = MutableLiveData()
    val onChangedVolume: LiveData<Event<Int>> = _onChangedVolume

    /**
     * 옵저버 등록.
     */
    fun register() {
        LogUtil.d(TAG, "register.")
        applicationContext.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI, true, this
        )
    }

    /**
     * 옵저버 해제.
     */
    fun unregister() {
        LogUtil.d(TAG, "unregister.")
        applicationContext.contentResolver.unregisterContentObserver(this)
    }

    /**
     * 상태값이 변경되면 호출.
     * 헤드셋 또는 이어폰 상태값 변경만 필터링함.
     */
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if (!uri.isHeadset() && !uri.isEarPiece()) {
            return
        }
        LogUtil.d(TAG, "onChangedVolume. volume: ${audioUtil.volume}, uri: $uri")
        _onChangedVolume.postNotify = audioUtil.volume
    }

    /**
     * 헤드셋이면 true.
     */
    protected fun Uri?.isHeadset(): Boolean =
        this.toString().contains(HEADSET)

    /**
     * 이어폰이면 true.
     */
    protected fun Uri?.isEarPiece(): Boolean =
        this.toString().contains(EARPIECE)

    override fun deliverSelfNotifications(): Boolean = false

}