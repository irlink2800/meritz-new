package com.irlink.meritz.ocx

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.util.extension.format
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object SocketLog : KoinComponent {

    const val TAG: String = "SocketLog"

    const val MAX_FILE_SIZE: Long = 1024 * 1024 * 2 // 2MB

    private val applicationContext: Context by inject()

    private val directoryManager: DirectoryManager by inject()

    private val logWriteExecutor: Executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    /**
     * 읽기, 쓰기 권한 허용 여부.
     */
    private val isGrantedPermissions: Boolean
        get() {
            val isReadPermission = ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            val isWritePermission = ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            return isReadPermission && isWritePermission
        }

    /**
     * 로그 텍스트 파일 저장.
     */
    fun writeToFile(message: String) = logWriteExecutor.execute {
        if (!isGrantedPermissions) {
            return@execute
        }
        try {
            if (message.isEmpty()) {
                return@execute
            }
            val fileName = "socketLog_${Date() format "yyyyMMdd"}"
            val dateNow = Date() format "MM-dd HH:mm:ss.SSS"

            var logFile: File
            var fileCount: Int = 0

            while (true) {
                logFile = if (fileCount == 0) {
                    File(directoryManager.socketLogDir, "$fileName.txt")
                } else {
                    File(directoryManager.socketLogDir, "$fileName[$fileCount].txt")
                }
                if (!logFile.exists()) {
                    logFile.createNewFile()
                }
                if (logFile.length() >= MAX_FILE_SIZE) {
                    fileCount++
                    continue
                }
                break
            }
            BufferedWriter(FileWriter(logFile, true)).apply {
                write(String.format("[%1s] %2s\r\n", dateNow, message))
                close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}