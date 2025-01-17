package com.irlink.meritz.util.network.upload

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.meritz.BuildConfig
import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.util.network.http.OkHttpUtil
import com.irlink.meritz.ocx.OcxPreference
import com.irlink.meritz.ocx.SaveState
import com.irlink.meritz.ocx.UploadState
import com.irlink.meritz.record.Record
import com.irlink.meritz.util.CipherUtil
import com.irlink.meritz.util.FileUtil
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.base.livedata.Event
import com.irlink.meritz.util.extension.emptyDisposable
import com.irlink.meritz.util.extension.format
import com.irlink.meritz.util.extension.postNotify
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers


import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class UploadManager(

    private val ocxPref: OcxPreference,
    private val okHttpClient: OkHttpClient,
    private val okHttpUtil: OkHttpUtil,
    private val fileUtil: FileUtil,
    private val cipherUtil: CipherUtil,
    private val directoryManager: DirectoryManager

) {

    companion object {
        const val TAG: String = "UploadManager"

        const val REQ_TYPE: String = "REQ_TYPE_FILE"
        const val REQ_ID: String = "ziphone"
        const val REQ_PWD: String = "irlink"

        const val RES_SUCCESS: String = "RES_SUCCESS"
        const val RES_FAIL: String = "RES_FAIL"
    }

    /**
     * 업로드 중인 파일 목록.
     */
    private val _uploadingFiles: ConcurrentHashMap<String, File> = ConcurrentHashMap()
    val uploadingFiles: Map<String, File> = _uploadingFiles

    /**
     * PC 업로드 이벤트.
     */
    private val _onUploadToPc: MutableLiveData<Event<Triple<File, String, ((result: String) -> Unit)?>>> =
        MutableLiveData()
    val onUploadToPc: LiveData<Event<Triple<File, String, ((result: String) -> Unit)?>>> =
        _onUploadToPc

    /**
     * 녹취 파일이 저장된 폴더.
     */
    protected open val recordFiles: List<File>
        get() = mutableListOf<File>().apply {
            this += directoryManager.recordDir.listFiles()?.toList() ?: listOf()
            this += directoryManager.encDir.listFiles()?.toList() ?: listOf()
            this += directoryManager.decDir.listFiles()?.toList() ?: listOf()
            this += directoryManager.failDir.listFiles()?.toList() ?: listOf()
        }

    /**
     * 파일이 업로드중이면 true.
     */
    protected val File.isUploadingFile: Boolean
        get() = uploadingFiles.containsKey(nameWithoutExtension)

    /**
     * 업로드 중 리스트에 추가.
     */
    protected fun addUploadingFile(file: File) {
        if (file.isUploadingFile) {
            return
        }
        _uploadingFiles += file.nameWithoutExtension to file

        LogUtil.d(TAG, "addUploadingFile: fileName = ${file.nameWithoutExtension}")
        LogUtil.d(TAG, "addUploadingFile: uploadingFiles.size = ${uploadingFiles.size}")
    }

    /**
     * 업로드 중 리스트에서 삭제.
     */
    protected fun removeUploadingFile(file: File) {
        if (!file.isUploadingFile) {
            return
        }
        _uploadingFiles -= file.nameWithoutExtension

        LogUtil.d(TAG, "removeUploadingFile: fileName = ${file.nameWithoutExtension}")
        LogUtil.d(TAG, "removeUploadingFile: uploadingFiles.size = ${uploadingFiles.size}")
    }

    /**
     * Task 업로드.
     */
    open fun upload(
        task: Task,
        isEnc: Boolean,
        onEncrypted: ((File) -> Unit)? = null,
        onUploaded: ((result: String) -> Unit)? = null
    ): Disposable {
        val recordFile: File = recordFiles.firstOrNull { file ->
            file.nameWithoutExtension == task.localFileName

        }.also {
            if (it == null) LogUtil.e(TAG, "upload(task). finalFile is not exists.")

        } ?: let {
            onUploaded(null, RES_FAIL, onUploaded)
            return emptyDisposable()
        }
        val shouldEnc: Boolean =
            isEnc && (recordFile.parentFile?.name == directoryManager.recordDir.name)

        return upload(recordFile, shouldEnc, onEncrypted, onUploaded)
    }

    /**
     * Record 업로드.
     */
    open fun upload(
        record: Record,
        onEncrypted: ((File) -> Unit)? = null,
        onUploaded: ((result: String) -> Unit)? = null
    ): Disposable {
        if (!record.finalFile.exists()) {
            LogUtil.e(TAG, "upload(record). finalFile is not exists.")
            onUploaded(null, RES_FAIL, onUploaded)
            return emptyDisposable()
        }
        val shouldEnc: Boolean =
            record.shouldEnc && !record.isEnc

        return upload(record.finalFile, shouldEnc,
            onEncrypted = { file ->
                if (shouldEnc) {
                    record.isEnc = true
                    record.saveDir = directoryManager.encDir
                }
                onEncrypted?.let { it(file) }
            },
            onUploaded = { result ->
                if (result == UploadState.SUCCESS) {
                    record.saveDir = directoryManager.backupDir
                }
                record.tryUploadCount++
                onUploaded?.let { it(result) }
            }
        )
    }

    /**
     * File 업로드.
     */
    open fun upload(
        file: File,
        isEnc: Boolean,
        onEncrypted: ((File) -> Unit)? = null,
        onUploaded: ((result: String) -> Unit)? = null
    ): Disposable {
        synchronized(UploadManager::class) {
            if (file.isUploadingFile) {
                LogUtil.e(TAG, "upload. already uploading file. [${file.name}]")
                return emptyDisposable()
            }
            addUploadingFile(file)
        }
        return when {
            file.exists() -> when (isEnc) {
                true -> cipherUtil.encrypt(file)
                else -> Flowable.just(file)
            }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onBackpressureBuffer()
                .subscribeBy(
                    onNext = { uploadFile ->
                        if (isEnc) {
                            onEncrypted?.let { it(uploadFile) }
                        }
                        when (ocxPref.saveState) {
                            SaveState.PC.toInt() -> uploadToPc(uploadFile, onUploaded)
                            SaveState.HTTP.toInt() -> uploadToHttp(uploadFile, onUploaded)
                            SaveState.LOCAL.toInt() -> onUploaded(
                                uploadFile,
                                RES_SUCCESS,
                                onUploaded
                            )
                        }
                    },
                    onError = {
                        LogUtil.exception(TAG, it)
                        onUploaded(file, RES_FAIL, onUploaded)
                    }
                )
            else -> {
                onUploaded(file, RES_FAIL, onUploaded)
                emptyDisposable()
            }
        }
    }

    /**
     * PC 업로드.
     */
    @WorkerThread
    protected open fun uploadToPc(
        uploadFile: File,
        onUploaded: ((result: String) -> Unit)? = null
    ) {
        _onUploadToPc.postNotify = Triple(
            uploadFile,
            createUploadPath(uploadFile, SaveState.PC) + "\\" + uploadFile.name,
            onUploaded
        )
    }

    /**
     * HTTP 업로드.
     */
    @WorkerThread
    protected open fun uploadToHttp(
        uploadFile: File,
        onUploaded: ((result: String) -> Unit)? = null
    ) {
        val uploadPath = createUploadPath(uploadFile, SaveState.HTTP)
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("REQ_TYPE", REQ_TYPE)
            .addFormDataPart("REQ_ID", REQ_ID)
            .addFormDataPart("REQ_PWD", REQ_PWD)
            .addFormDataPart("REQ_PATHNAME", uploadPath)
            .addFormDataPart("REQ_SIZE", uploadFile.length().toString())
            .addPart(
                okHttpUtil.createMultiPart(
                    "REQ_FILENAME", uploadFile.name, uploadFile
                )
            )
            .build()

        val request: Request = Request.Builder()
            .url(ocxPref.uploadHost)
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string() ?: RES_FAIL
                onUploaded(uploadFile, body, onUploaded)
                printUploadLog(uploadFile, ocxPref.uploadHost, uploadPath, body)
            }

            override fun onFailure(call: Call, e: IOException) {
                LogUtil.exception(TAG, e)
                onUploaded(uploadFile, RES_FAIL, onUploaded)
            }
        })
    }

    /**
     * 업로드 완료 시 호출.
     */
    open fun onUploaded(
        file: File?,
        response: String,
        onUploaded: ((result: String) -> Unit)? = null
    ) {
        when {
            file?.exists() != true -> {
                onUploaded?.let { it(UploadState.NOT_EXISTS) }
            }
            response.contains(RES_SUCCESS) -> {
                fileUtil.deleteFile(file)
                onUploaded?.let { it(UploadState.SUCCESS) }
            }
            else -> {
                onUploaded?.let { it(UploadState.FAIL) }
            }
        }
        file?.let { removeUploadingFile(it) }
    }

    /**
     * 업로드 경로 생성.
     */
    protected open fun createUploadPath(uploadFile: File, saveState: String): String =
        when (ocxPref.isRecordIncludeDate) {
            true -> {
                val separator = when (saveState) {
                    SaveState.PC -> "\\"
                    else -> "/"
                }
                ocxPref.uploadPath + separator + (Date() format "yyyyMMdd")
            }
            else -> ocxPref.uploadPath
        }

    /**
     * 업로드 로그.
     */
    protected open fun printUploadLog(
        uploadFile: File,
        uploadHost: String,
        uploadPath: String,
        response: String
    ) {
        if (BuildConfig.DEBUG) {
            return
        }
        val log = JSONObject().apply {
            put("file_name", uploadFile.name)
            put("file_path", uploadFile.path)
            put("host", uploadHost)
            put("path", uploadPath)
            put("response", response.trim())
        }
        LogUtil.d(TAG, "uploadInfo: ${log.toString(4)}")
    }

    /**
     * 초기화.
     */
    open fun clear() {
        _uploadingFiles.clear()
    }

    /**
     * 업로드 정보를 담고있는 객체.
     */
    data class Task(
        val localFileName: String,
        val remoteFileName: String,
        val remoteHost: String,
        val remotePath: String
    )

}