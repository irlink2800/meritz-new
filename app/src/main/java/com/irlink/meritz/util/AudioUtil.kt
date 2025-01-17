package com.irlink.meritz.util

import android.content.Context
import android.media.AudioManager

open class AudioUtil(

    private val applicationContext: Context

) {

    protected val audioManager: AudioManager by lazy {
        applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * 디바이스 볼륨 값.
     */
    open var volume: Int
        set(value) {
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, value, AudioManager.FLAG_SHOW_UI)
        }
        get() = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)

    /**
     * 디바이스 최대 볼륨 값.
     */
    open val maxVolume: Int
        get() = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)

    /**
     * 마이크 음소거 ON/OFF.
     */
    open var isMicrophoneMute: Boolean
        set(value) {
            audioManager.isMicrophoneMute = value
        }
        get() = audioManager.isMicrophoneMute

}