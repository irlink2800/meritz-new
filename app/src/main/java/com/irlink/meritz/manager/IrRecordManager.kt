package com.irlink.meritz.manager

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.irrecorder.IRRecorderv2
import com.irlink.meritz.ocx.OcxPreference
import com.irlink.meritz.ocx.UploadState
import com.irlink.meritz.record.Record
import com.irlink.meritz.record.RecordManager
import com.irlink.meritz.record.RecordPreference
import com.irlink.meritz.util.*
import com.irlink.meritz.util.base.livedata.EmptyEvent
import com.irlink.meritz.util.base.livedata.Event
import com.irlink.meritz.util.extension.postNotify
import com.irlink.meritz.util.extension.timer
import com.irlink.meritz.util.extension.wait
import com.irlink.meritz.util.network.upload.UploadManager
import io.reactivex.rxjava3.disposables.Disposable
import com.irlink.meritz.record.RecordError
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class IrRecordManager(

    applicationContext: Context,
    ocxPref: OcxPreference,
    recordPref: RecordPreference,
    fileUtil: FileUtil,
    formatUtil: FormatUtil,
    deviceUtil: DeviceUtil,
    directoryManager: DirectoryManager,
    private val audioFileUtil: AudioFileUtil,
    private val uploadManager: UploadManager

) : RecordManager(
    applicationContext,
    ocxPref,
    recordPref,
    fileUtil,
    formatUtil,
    deviceUtil,
    directoryManager
) {

    companion object {
        const val TAG: String = "IrRecordManager"
    }

    /**
     * 녹취 시작 이벤트.
     */
    private val _onRecordStart: MutableLiveData<EmptyEvent> = MutableLiveData()
    val onRecordStart: LiveData<EmptyEvent> = _onRecordStart

    /**
     * 업로드 완료 이벤트.
     */
    private val _onUploaded: MutableLiveData<Event<Pair<UploadManager.Task, String>>> =
        MutableLiveData()
    val onUploaded: LiveData<Event<Pair<UploadManager.Task, String>>> = _onUploaded

    /**
     * 녹취 에러 발생 이벤트.
     */
    private val _onError: MutableLiveData<Event<RecordError>> = MutableLiveData()
    val onError: LiveData<Event<RecordError>> = _onError

    /**
     * onRecordStart 메소드 처리 스레드.
     */
    private val onRecordStartExecutor: Executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    /**
     * onSaveFiles 메소드 처리 스레드.
     */
    private val onSaveFilesExecutor: Executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    /**
     * 녹취가 시작되면 호출.
     */
    override fun onRecordStart(fileName: String?, legacyFile: File?) =
        onRecordStartExecutor.execute {
            LogUtil.d(TAG, "onRecordStart. fileName: $fileName / legacyFile: ${legacyFile?.path}")
            _onRecordStart.postNotify()

            if (fileName.isNullOrEmpty()) {
                LogUtil.e(TAG, "onRecordStart. fileName is null or empty.")
                return@execute
            }
            val legacyFilePath = audioFileUtil.getLegacyFilePath(legacyFile)
            if (legacyFilePath?.exists() == true) {
                setLegacyFilePath(legacyFilePath.toString())
            }
            setMissedRecord(false)
        }

    /**
     * 녹취가 완료되면 호출.
     */
    override fun onSaveFiles(fileName: String?) = onSaveFilesExecutor.execute {
        LogUtil.d(TAG, "onSaveFiles. fileName: $fileName")
        if (fileName.isNullOrEmpty()) {
            LogUtil.e(TAG, "onSaveFiles. fileName is null or empty.")
            return@execute
        }
        val record: Record = records[fileName.nameWithoutExtension] ?: let {
            LogUtil.e(TAG, "onSaveFiles. record is null.")
            return@execute
        }
        if (IRRecorderv2.isACRRecordSet() && record.isPartialRecord) wait(10_000) {
            !record.isPartialRecord
        }
        renameFinalFileName(record)

        if (record.isSaCall) {
            LogUtil.w(TAG, "onSaveFiles. is saCall.")
            moveToSaCallDir(record)
            return@execute
        }
        if (!record.isStartedRecord) {
            LogUtil.w(TAG, "onSaveFiles. is not started.")
            deleteRecordFile(record)
            return@execute
        }
        LogUtil.d(TAG, "block morecx insert.")
        measureTimeMillis {
            if (!record.isMorecxUploaded) wait(60_000) {
                record.isMorecxUploaded
            }
        }.let {
            LogUtil.d(TAG, "wait morecx insert: $it")
        }
        uploadRecord(record)
    }

    /**
     * 레코드 파일 업로드.
     */
    override fun uploadRecord(file: File, initFileName: String?): Disposable =
        uploadManager.upload(file, isEnc = false, onUploaded = { result ->
            onUploadedRecord(
                file.nameWithoutExtension,
                file.nameWithoutExtension,
                result,
                true
            )
        })

    /**
     * 레코드 파일 업로드.
     */
    override fun uploadRecord(record: Record) = timer(2000) {
        uploadManager.upload(record) { result ->
            onUploadedRecord(
                record,
                result,
                isNotify = true
            )
        }
    }

    /**
     * 업로드 완료 시 호출.
     */
    override fun onUploadedRecord(record: Record, result: String, isNotify: Boolean) {
        if (result == UploadState.SUCCESS) {
            removeRecord(record)
        }
        onUploadedRecord(
            record.finalFileName,
            record.finalFileName,
            result,
            isNotify
        )
    }

    private fun onUploadedRecord(
        localFileName: String,
        remoteFileName: String,
        result: String,
        isNotify: Boolean
    ) {
        LogUtil.d(
            TAG,
            "onUploadedRecordFile. remoteFileName: $remoteFileName, result: $result, isNotify: $isNotify"
        )
        if (isNotify) UploadManager.Task(
            localFileName = localFileName,
            remoteFileName = remoteFileName,
            remoteHost = ocxPref.uploadHost,
            remotePath = ocxPref.uploadPath
        ).also { task ->
            _onUploaded.postNotify = task to result
        }
    }

    /**
     * 부분녹취 중 통화가 종료된 경우 자동으로 부분녹취를 종료한 후 호출됨.
     */
    override fun onAutoPartialEndedListener(originRecordFileName: String?) {
        LogUtil.d(TAG, "onAutoPartialEndedListener. originRecordFileName = $originRecordFileName")
        if (originRecordFileName.isNullOrEmpty()) {
            return
        }
        val record = records[originRecordFileName.nameWithoutExtension] ?: return

        record.partialRecord()?.let { partialRecord ->
            addRecord(partialRecord)
            uploadRecord(partialRecord)
        }
        onStoppedPartialRecord(record.partialFile)
    }

    /**
     * 에러 발생 시 호출.
     */
    override fun onError(code: Int, msg: String?) {
        _onError.postNotify = RecordError(currentCallRecord, code, msg)
    }
}