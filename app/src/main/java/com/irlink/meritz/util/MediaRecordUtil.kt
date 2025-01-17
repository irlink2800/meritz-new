package com.irlink.meritz.util

import android.media.MediaRecorder
import java.io.File

open class MediaRecordUtil {

    companion object {
        const val TAG: String = "MediaRecordUtil"

        const val AUDIO_SOURCE: Int = MediaRecorder.AudioSource.MIC

        /**
         * 확장자 포맷.
         */
        const val OUT_PUT_FORMAT: Int = MediaRecorder.OutputFormat.AMR_NB

        /**
         * 인코딩.
         */
        const val AUDIO_ENCODER: Int = MediaRecorder.AudioEncoder.AMR_NB
    }

    private var mediaRecorder: MediaRecorder? = null

    /**
     * 녹취 시작 여부.
     */
    private var isStart = false

    /**
     * 녹취 시작.
     */
    open fun startRecord(targetFile: File) = try {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(AUDIO_SOURCE)
            setOutputFormat(OUT_PUT_FORMAT)
            setAudioEncoder(AUDIO_ENCODER)
            setOutputFile(targetFile.path)
        }
        mediaRecorder?.let {
            it.prepare()
            it.start()
            isStart = true
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
    }

    /**
     * 녹취 종료.
     */
    open fun stopRecord() = try {
        mediaRecorder?.let {
            it.stop()
            it.release()
            isStart = false
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
    }

}