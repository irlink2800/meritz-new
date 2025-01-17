package com.irlink.meritz.util

import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Environment
import com.arthenica.mobileffmpeg.FFmpeg
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class AudioFileUtil {

    companion object {
        const val TAG = "AudioFileUtil"

        const val AMR_HEADER: String = "#!AMR"
    }

    private val externalStorageDirectoryPath = Environment.getExternalStorageDirectory().path

    /**
     * 오디오 파일 재생 길이 반환.
     */
    fun getAudioFileDuration(file: File): Long? {
        return try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(file.absolutePath)
            val durationStr: String? =
                mediaMetadataRetriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION))
            TimeUnit.MILLISECONDS.toSeconds((durationStr?.toLong() ?: 0))
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            null
        }
    }

    /**
     * 오디오 파일 변환.
     */
    fun convertAudioFile(
        sourceFile: File,
        targetFile: File
    ): Int = FFmpeg.execute(createConvertCommand(sourceFile, targetFile))

    /**
     * 변환 커맨드 생성.
     */
    private fun createConvertCommand(sourceFile: File, targetFile: File): Array<String> = arrayOf(
        "-i",
        sourceFile.absolutePath,
        "-vn",
        "-ar",
        "8000",
        "-ab",
        "12.2k",
        "-ac",
        "1",
        "-f",
        "amr",
        targetFile.absolutePath
    )

    /**
     * 오디오 파일 Mime 변경.
     */
    fun changeMimeType(
        sourceFile: File,
        onComplete: () -> Unit
    ) {
        var fileOutputStream: FileOutputStream? = null
        var size: Int = sourceFile.length().toInt()
        val bytes = ByteArray(size)
        val bufferedInputStream = BufferedInputStream(FileInputStream(sourceFile))
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(6)

        try {
            bufferedInputStream.read(bytes, 0, bytes.size)
            bufferedInputStream.close()

            val mdatIndex: Int = indexOf(bytes, byteArrayOf(0x6D, 0x64, 0x61, 0x74))
            val moovIndex: Int = indexOf(bytes, byteArrayOf(0x6D, 0x6F, 0x6F, 0x76))

            if (moovIndex != -1 && mdatIndex < moovIndex) size = moovIndex
            val tempByteArray: ByteArray = bytes.copyOfRange(mdatIndex + 4, size - mdatIndex - 4)

            byteBuffer.put("$AMR_HEADER\n".toByteArray(Charset.forName("ASCII")))
            fileOutputStream = FileOutputStream(sourceFile.path)
            fileOutputStream.write(byteBuffer.array(), 0, 6)
            fileOutputStream.write(tempByteArray)
            onComplete()

        } catch (e: Exception) {
            LogUtil.exception(TAG, e)

        } finally {
            fileOutputStream?.close()
        }
    }

    private fun indexOf(data: ByteArray, pattern: ByteArray): Int {
        val failure: IntArray = computeFailure(pattern)
        var j = 0
        for (i in data.indices) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1]
            }
            if (pattern[j] == data[i]) {
                j++
            }
            if (j == pattern.size) {
                return i - pattern.size + 1
            }
        }
        return -1
    }

    private fun computeFailure(pattern: ByteArray): IntArray {
        val failure = IntArray(pattern.size)
        var j = 0
        for (i in 1 until pattern.size) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1]
            }
            if (pattern[j] == pattern[i]) {
                j++
            }
            failure[i] = j
        }
        return failure
    }

    /**
     * 레거시 녹취 폴더 반환.
     */
    fun getCallRecordFileFolder(): List<File>? = when {
        Build.BRAND.equals("lge", ignoreCase = true) -> {
            listOf(
                File("${externalStorageDirectoryPath}/AudioRecorder/my_sounds/call_rec/")
            )
        }

        Build.BRAND.equals("samsung", ignoreCase = true) -> {
            listOf(
                File("${externalStorageDirectoryPath}/Call/"),
                File("${externalStorageDirectoryPath}/Recordings/Call")
            )
        }

        Build.BRAND.equals("oppo", ignoreCase = true) -> {
            listOf(
                File("${externalStorageDirectoryPath}/Recordings/Call Recordings/")
            )
        }

        Build.BRAND.equals("pointmobile", ignoreCase = true) -> {
            listOf(
                File("${externalStorageDirectoryPath}/CallRecord/")
            )
        }

        else -> null
    }

    /**
     * 레거시 녹취 파일 경로 반환.
     */
    fun getLegacyFilePath(legacyFile: File?): File? {
        var legacyFilePath: File? = null

        /**
         * 레거시 녹취 파일 전체를 탐색하여, 인자로 받은 파일명과 동일한 경우 해당 경로 반환.
         */
        getCallRecordFileFolder()?.let { list ->
            for (folder in list) {
                folder.listFiles()?.let { folder ->
                    for (file in folder) {
                        if (file.toString().contains(legacyFile.toString())) {
                            legacyFilePath = file
                        }
                    }
                }
            }
        }
        return legacyFilePath
    }

}