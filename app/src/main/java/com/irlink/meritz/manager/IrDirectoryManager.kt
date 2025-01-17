package com.irlink.meritz.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.irlink.irrecorder.IRRecorderv2
import com.irlink.meritz.R
import com.irlink.meritz.ocx.OcxPreference
import com.irlink.meritz.util.FileUtil
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.extension.emptyDisposable
import com.irlink.meritz.util.extension.toV3
import com.tedpark.tedonactivityresult.rx2.TedRxOnActivityResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class IrDirectoryManager(

    private val applicationContext: Context,
    private val ocxPref: OcxPreference,
    private val fileUtil: FileUtil

) : DirectoryManager {

    companion object {
        const val TAG: String = "IrDirectoryManager"

        const val LOG_DIR: String = "log"
        const val SOCKET_LOG_DIR: String = "socketLog"
        const val RECORD_LOG_DIR: String = "recordLog"
        const val DOWNLOAD_DIR: String = "download"
        const val ENC_DIR: String = "enc"
        const val DEC_DIR: String = "dec"
        const val SACALL_DIR: String = "sacall"
        const val BACKUP_DIR: String = "backup"
        const val MESSAGE_DIR: String = "message"
        const val FAIL_DIR: String = "fail"
        const val OKHTTP_DIR: String = "okhttp"
    }

    /**
     * root dir.
     */
    override val rootDir: File
        get() = obtainRootDir()

    /**
     * {root}/cache dir.
     */
    override val cacheDir: File
        get() = obtainCacheDir()

    /**
     * {root}/files/log dir.
     */
    override val logDir: File
        get() = obtainLogDir()

    /**
     * {root}/files/socketLog dir.
     */
    override val socketLogDir: File
        get() = obtainSocketLogDir()

    /**
     * {root}/files/log/recordLog dir.
     */
    override val recordLogDir: File
        get() = obtainRecordLogDir()

    /**
     * {root}/files/download dir.
     */
    override val downloadDir: File
        get() = obtainDownloadDir()

    /**
     * {root}/files/record dir.
     */
    override val recordDir: File
        get() = obtainRecordDir()

    /**
     * {root}/files/record/enc dir.
     */
    override val encDir: File
        get() = obtainEncDir()

    /**
     * {root}/files/record/dec dir.
     */
    override val decDir: File
        get() = obtainDecDir()

    /**
     * {root}/files/record/sacall dir.
     */
    override val saCallDir: File
        get() = obtainSaCall()

    /**
     * {root}/files/record/backup dir.
     */
    override val backupDir: File
        get() = obtainBackupDir()

    /**
     * {root}/files/record/message dir.
     */
    override val messageDir: File
        get() = obtainMessageDir()

    /**
     * {root}/files/record/fail dir.
     */
    override val failDir: File
        get() = obtainFailDir()

    /**
     * {root}/cache/retrofit dir.
     */
    override val okHttpDir: File
        get() = obtainOkHttpDir()

    /**
     * 레코드 폴더명 변경.
     */
    override fun renameRecordDir(name: String) {
        if (name.isEmpty() || name == ocxPref.recordFolderName) {
            return
        }
        val oldRecordDir = recordDir
        val newRecordDir = File(oldRecordDir.parent, name)

        if (newRecordDir.exists()) {
            fileUtil.moveFiles(backupDir, File(newRecordDir, backupDir.name))
            fileUtil.moveFiles(encDir, File(newRecordDir, encDir.name))
            fileUtil.moveFiles(decDir, File(newRecordDir, decDir.name))
            fileUtil.moveFiles(failDir, File(newRecordDir, failDir.name))
            fileUtil.moveFiles(saCallDir, File(newRecordDir, saCallDir.name))
        } else {
            fileUtil.renameFile(oldRecordDir, newRecordDir)
        }
        if (oldRecordDir.exists()) {
            fileUtil.deleteFile(oldRecordDir, isForceDelete = true)
        }
        ocxPref.recordFolderName = name
        IRRecorderv2.setFileDirectory(newRecordDir.path)
    }

    /**
     * Root 폴더 리턴.
     */
    private fun obtainRootDir(): File = File(
        Environment.getExternalStorageDirectory(),
        applicationContext.getString(R.string.app_eng_name)
    )

    /**
     * Cache 폴더 리턴.
     */
    private fun obtainCacheDir(): File = (applicationContext.externalCacheDir
        ?: applicationContext.cacheDir).also { cache ->
        fileUtil.createDirectory(cache)
    }

    /**
     * log 폴더 리턴.
     */
    private fun obtainLogDir(): File = File(rootDir, LOG_DIR).also { logDir ->
        fileUtil.createDirectory(logDir)
    }

    /**
     * socketLog 폴더 리턴.
     */
    private fun obtainSocketLogDir(): File = File(logDir, SOCKET_LOG_DIR).also { logDir ->
        fileUtil.createDirectory(logDir)
    }

    /**
     * recordLog 폴더 리턴.
     */
    private fun obtainRecordLogDir(): File = File(logDir, RECORD_LOG_DIR).also { logDir ->
        fileUtil.createDirectory(logDir)
    }

    /**
     * Download 폴더 리턴.
     */
    private fun obtainDownloadDir(): File = File(rootDir, DOWNLOAD_DIR).also { downloadDir ->
        fileUtil.createDirectory(downloadDir)
    }

    /**
     * Record 폴더 리턴.
     */
    private fun obtainRecordDir(): File =
        File(rootDir, ocxPref.recordFolderName).also { recordDir ->
            fileUtil.createDirectory(recordDir)
        }

    /**
     * Enc 폴더 리턴.
     */
    private fun obtainEncDir(): File = File(recordDir, ENC_DIR).also { encDir ->
        fileUtil.createDirectory(encDir)
    }

    /**
     * Dec 폴더 리턴.
     */
    private fun obtainDecDir(): File = File(recordDir, DEC_DIR).also { decDir ->
        fileUtil.createDirectory(decDir)
    }

    /**
     * SaCall 폴더 리턴.
     */
    private fun obtainSaCall(): File = File(recordDir, SACALL_DIR).also { saCall ->
        fileUtil.createDirectory(saCall)
    }

    /**
     * Backup 폴더 리턴.
     */
    private fun obtainBackupDir(): File = File(recordDir, BACKUP_DIR).also { backupDir ->
        fileUtil.createDirectory(backupDir)
    }

    /**
     * Message 폴더 리턴.
     */
    private fun obtainMessageDir(): File = File(rootDir, MESSAGE_DIR).also { message ->
        fileUtil.createDirectory(message)
    }

    /**
     * Fail 폴더 리턴.
     */
    private fun obtainFailDir(): File = File(recordDir, FAIL_DIR).also { failDir ->
        fileUtil.createDirectory(failDir)
    }

    /**
     * Okhttp 폴더 리턴.
     */
    private fun obtainOkHttpDir(): File = File(cacheDir, OKHTTP_DIR).also { okHttpDir ->
        fileUtil.createDirectory(okHttpDir)
    }

    /**
     * 퍼미션 요청.
     */
    @RequiresApi(Build.VERSION_CODES.R)
    override fun requestPermission(
        context: Context,
        callback: (isGranted: Boolean) -> Unit
    ): Disposable {
        if (Environment.isExternalStorageManager()) {
            callback(true)
            return emptyDisposable()
        }
        val intent = Intent(
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        return TedRxOnActivityResult.with(context)
            .startActivityForResult(intent)
            .toV3()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    callback(Environment.isExternalStorageManager())
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
    }

}