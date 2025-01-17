package com.irlink.meritz.record

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.irrecorder.AudioExtensions
import com.irlink.irrecorder.IRRecorderv2
import com.irlink.irrecorder.IRecordingCallback
import com.irlink.irrecorder.OnAutoPartialEndedListener
import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.ocx.OcxPreference
import com.irlink.meritz.util.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import com.irlink.meritz.util.extension.parseDate
import com.irlink.meritz.util.extension.postValue
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

abstract class RecordManager(

    private val applicationContext: Context,
    protected val ocxPref: OcxPreference,
    protected val recordPref: RecordPreference,
    protected val fileUtil: FileUtil,
    protected val formatUtil: FormatUtil,
    protected val deviceUtil: DeviceUtil,
    protected val mediaRecordUtil: MediaRecordUtil,
    protected val directoryManager: DirectoryManager

) : IRecordingCallback, OnAutoPartialEndedListener {

    companion object {
        const val TAG: String = "RecordManager"

        const val DEFAULT_IS_USE_STT: Boolean = false
        const val DEFAULT_RECORDER_LOG_FILE_NAME: String = "IRRecorderLog"
    }

    /**
     * STT 사용 여부.
     */
    val isUseSTT: Boolean
        get() = DEFAULT_IS_USE_STT

    /**
     * 레코더 로그 파일 명.
     */
    val recorderLogFileName: String
        get() = DEFAULT_RECORDER_LOG_FILE_NAME

    /**
     * 현재 진행중인 전화 녹취의 정보가 저장되는 리스트.
     * 통화 종료 시, null로 세팅된다.
     * 주의) null이 아니라고 해서 반드시 녹취가 정상 동작 중이라는 보장은 없다.
     */
    var currentCallRecord: Record? = null
        private set

    /**
     * 현재 진행중인 대면 녹취의 정보가 저장되는 리스트.
     * 녹취 종료 시, null로 세팅된다.
     */
    var currentFtfRecord: Record? = null
        private set

    /**
     * 통화 종료 시 삭제되지 않고, 모든 작업이 끝나면 리스트에서 제거한다.
     * 키값은 Record.defaultFileName이다.
     * @Deprecated Room(SQLite로 대체 예정)
     */
    @Deprecated("삭제 예정")
    private val _records: ConcurrentHashMap<String, Record> by lazy {
        ConcurrentHashMap<String, Record>().apply { putAll(recordPref.records) }
    }

    @Deprecated("삭제 예정")
    val records: Map<String, Record> = _records

    protected val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    /**
     * 현재 전화 녹취가 진행중이면 true.
     */
    private val _isCallRecording: MutableLiveData<Boolean> = MutableLiveData(false)
    val isCallRecording: LiveData<Boolean> = _isCallRecording

    /**
     * 현재 대면 녹취가 진행중이면 true.
     */
    private val _isFtfRecording: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFtfRecording: LiveData<Boolean> = _isFtfRecording

    /**
     * IRRecorder 초기화.
     */
    init {
        LogUtil.d(TAG, "init.")
        LogUtil.d(TAG, "init. records: ${recordPref.recordsJson}")
        LogUtil.d(TAG, "init. records.size: ${records.size}")

        IRRecorderv2.init(
            applicationContext,
            isUseSTT,
            directoryManager.recordDir,
            directoryManager.recordLogDir,
            recorderLogFileName
        )
        IRRecorderv2.setAudioExtension(AudioExtensions.AMR)
    }

    /**
     * 활성화.
     */
    open fun enable(isUseRecord: Boolean = true) {
        LogUtil.d(TAG, "enable. isUseRecord: $isUseRecord")

        if (isUseRecord) {
            IRRecorderv2.setCallback(this)
            IRRecorderv2.setOnAutoPartialEndedListener(this)
            IRRecorderv2.startWatchingFolder()
        }
        initCurrentRecords()
        deleteOldRecords()
        startSyncRecords()
    }

    /**
     * 현재 통화중인 레코드 객체 설정.
     */
    protected open fun initCurrentRecords() {
        val telephonyManager =
            applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                telephonyManager.listen(this, LISTEN_NONE)
                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    return
                }
                currentCallRecord = records.toList().maxByOrNull { it.first }?.second

            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    protected open fun deleteOldRecords() {
        val iterator = _records.iterator()
        val compareDate = System.currentTimeMillis() - (1_000 * 60 * 60 * 24 * 7) // 7 days
        while (iterator.hasNext()) {
            try {
                val record = iterator.next().value
                if (record.callDate == Record.DEFAULT_VALUE) {
                    throw NullPointerException("is default value.")
                }
                val callDate = (record.callDate parseDate "yyyyMMdd")!!.time
                if (callDate < compareDate) {
                    iterator.remove()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                iterator.remove()
            }
        }
    }

    /**
     * 비활성화.
     */
    open fun disable() {
        LogUtil.d(TAG, "disable.")
        IRRecorderv2.setCallback(null)
        IRRecorderv2.setOnAutoPartialEndedListener(null)
        IRRecorderv2.stopWatchingFolder()
        compositeDisposable.clear()
    }

    /**
     * pref, records 정보 동기화.
     */
    open fun startSyncRecords() = Flowable.interval(500, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .onBackpressureBuffer()
        .subscribeBy(
            onNext = {
                recordPref.records = records
            },
            onError = {
                LogUtil.exception(TAG, it)
            }
        ).let {
            compositeDisposable += it
        }

    /**
     * 새로운 레코드 객체 생성 및 기본 값 초기화.
     */
    open fun newRecord(
        type: RecordType,
        remoteNumber: String,
        isSyncCurrent: Boolean = true
    ): Record = Record(
        recordType = type,
        remoteNumber = remoteNumber,
        userNumber = deviceUtil.phoneNumber,
        callDate = formatUtil.toDate(Date()),
        callStartTime = formatUtil.toTime(Date()),
        isSaCall = ocxPref.isSaCall,
        isRecord = ocxPref.isRecord,
        shouldEnc = ocxPref.isEnc,
        extra = ocxPref.extra,
        saveDir = directoryManager.recordDir,
        uploadPath = ocxPref.uploadPath,
        extension = IRRecorderv2.getAudioExtension()
    ).also { newRecord ->
        LogUtil.d(
            TAG,
            "newRecord. type: $type, remoteNumber: $remoteNumber, isSyncCurrent: $isSyncCurrent"
        )
        if (isSyncCurrent) {
            when (newRecord.recordType) {
                RecordType.FTF -> {
                    currentFtfRecord = newRecord
                    _isFtfRecording.postValue = true
                }

                else -> {
                    currentCallRecord = newRecord
                    _isCallRecording.postValue = true
                }
            }
        }
        addRecord(newRecord)
    }

    /**
     * 전화 녹취 시작.
     */
    open fun startCallRecord() {
        if (currentCallRecord?.isRecord == true) {
            currentCallRecord?.isStartedRecord = true
            IRRecorderv2.sendReceive(applicationContext, TelephonyManager.EXTRA_STATE_OFFHOOK)
        }
    }

    /**
     * 전화 녹취 종료.
     */
    open fun endCallRecord() {
        if (currentCallRecord?.isStartedRecord == true) {
            IRRecorderv2.sendReceive(applicationContext, TelephonyManager.EXTRA_STATE_IDLE)
        }
        clearCallCurrentRecord()
    }

    /**
     * 대면 녹취 시작.
     */
    open fun startFtfRecord(remoteNumber: String) {
        if (isCallRecording.value == true) {
            LogUtil.w(TAG, "is currently call recording.")
            return
        }
        if (isFtfRecording.value == true) {
            LogUtil.w(TAG, "is already ftf recording.")
            return
        }
        val record = newRecord(
            type = RecordType.FTF,
            remoteNumber = remoteNumber
        ).apply {
            isStartedRecord = true
            callConnectedDate = callDate
            callConnectedTime = callStartTime
        }
        onRecordStart(record.initializeFileName, null)
        mediaRecordUtil.startRecord(record.initializeFile)
    }

    /**
     * 대면 녹취 종료.
     */
    open fun endFtfRecord() {
        if (isFtfRecording.value == false) {
            LogUtil.w(TAG, "is already stopped ftf recording.")
            return
        }
        mediaRecordUtil.stopRecord()
        currentFtfRecord?.let {
            it.callEndDate = formatUtil.toDate(Date())
            it.callEndTime = formatUtil.toTime(Date())
            it.talkTime = formatUtil.toDurationSec(
                fromTime = it.callStartTime,
                toTime = it.callEndTime
            )
        }
        onSaveFiles(currentFtfRecord?.initializeFileName)
        clearFtfCurrentRecord()
    }

    /**
     * 녹취 여부 설정.
     */
    open fun setIsRecord(isRecord: Boolean) {
        this.currentCallRecord?.isRecord = isRecord
    }

    /**
     * 암호화 필요 여부 설정.
     */
    open fun setShouldEnc(shouldEnc: Boolean) {
        this.currentCallRecord?.shouldEnc = shouldEnc
    }

    /**
     * IrRecorder에서 생성할 파일 명.
     */
    open fun setIrRecorderFileName(recordFileName: String?) {
        IRRecorderv2.setRecordFileName(recordFileName)
    }

    /**
     * ocx 녹취 파일명 설정.
     */
    open fun setOcxFileName(recordFileName: String) {
        LogUtil.d(TAG, "setOcxFileName. recordFileName: $recordFileName")
        this.currentCallRecord?.ocxFileName = recordFileName
    }

    /**
     * 녹취 시작 시간 / 날짜 설정.
     */
    open fun setRecordStarted(callDate: String, callStartTime: String) {
        LogUtil.d(TAG, "setRecordStarted. callDate: $callDate, callStartTime: $callStartTime")
        this.currentCallRecord?.callDate = callDate
        this.currentCallRecord?.callStartTime = callStartTime
    }

    /**
     * 통화 연결 시간
     */
    open fun setRecordConnected(connectedDate: String, connectedTime: String) {

        // 통화 연결 일시
        this.currentCallRecord?.callConnectedDate = connectedDate

        // 통화 연결 시간
        this.currentCallRecord?.callConnectedTime = connectedTime

        // 링 타임
        this.currentCallRecord?.ringTime = formatUtil.toDurationSec(
            fromTime = currentCallRecord!!.callStartTime,
            toTime = currentCallRecord!!.callConnectedTime
        )
        LogUtil.d(
            TAG,
            "setInboundRecordConnected. connectedTime: ${currentCallRecord?.callConnectedTime}, ringTime: ${currentCallRecord?.ringTime}"
        )
    }

    /**
     * 녹취 종료 시간 / 순수 통화 시간.
     */
    open fun setRecordEnded(callEndDate: String, callEndTime: String, talkTime: String) {
        this.currentCallRecord?.callEndDate = callEndDate
        this.currentCallRecord?.callEndTime = callEndTime
        this.currentCallRecord?.talkTime = talkTime
        if (currentCallRecord?.ringTime == Record.DEFAULT_VALUE) {
            this.currentCallRecord?.ringTime = formatUtil.toDurationSec(
                fromTime = currentCallRecord!!.callStartTime,
                toTime = currentCallRecord!!.callEndTime
            )
        }
        LogUtil.d(
            TAG,
            "setRecordEnded. callEndTime: $callEndTime, talkTime: $talkTime, ringTime: ${currentCallRecord?.ringTime}"
        )
    }

    /**
     * 정상 녹취 여부 설정.
     */
    open fun setMissedRecord(isMissedRecord: Boolean) {
        LogUtil.d(TAG, "setMissedRecord. isMissedRecord: $isMissedRecord")
        this.currentCallRecord?.isMissedRecord = isMissedRecord
    }

    /**
     * 부분 녹취 시작 지점 설정.
     */
    open fun refreshPartialIndex(index: Long = currentCallRecord?.initializeFile?.length() ?: 0) {
        LogUtil.d(TAG, "refreshPartialIndex. index: $index")
        this.currentCallRecord?.partialStartIndex = index
    }

    /**
     * 레거시 녹취 파일 경로 설정.
     */
    open fun setLegacyFilePath(path: String) {
        LogUtil.d(TAG, "setLegacyFilePath. path: $path")
        this.currentCallRecord?.legacyFilePath = path
    }

    /**
     * 녹취 재개.
     */
    open fun resumeRecording() {
        LogUtil.d(TAG, "resumeRecording.")
        IRRecorderv2.resumeRecording()
    }

    /**
     * 녹취 정지.
     */
    open fun pauseRecording() {
        LogUtil.d(TAG, "pauseRecording.")
        IRRecorderv2.pauseRecording()
    }

    /**
     * 녹취 자르기. (파일명 + suffix)
     */
    open fun cutRecordAppendSuffix(
        record: Record? = currentCallRecord,
        suffix: String,
        startIndex: Long = 0L,
        callback: ((File) -> Unit)? = null
    ) = cutRecord(
        fileName = "${record?.finalFileName}$suffix.${record?.extensionName}",
        startIndex = startIndex,
        callback = callback
    )

    /**
     * 녹취 자르기. (파일명 직접 지정)
     */
    open fun cutRecord(
        record: Record? = currentCallRecord,
        fileName: String,
        startIndex: Long = 0L,
        callback: ((File) -> Unit)? = null
    ) {
        LogUtil.d(TAG, "cutRecord. fileName: $fileName, startIndex: $startIndex")

        if (record == null) {
            LogUtil.d(TAG, "cutRecording: record is null.")
            return
        }
        if (!record.initializeFile.exists()) {
            LogUtil.d(TAG, "cutRecording: recordFile is not exists.")
            return
        }
        val cutRecordFile: File = File(
            record.saveDir, fileName
        )
        if (!cutRecordFile.exists()) {
            fileUtil.createFile(cutRecordFile)
        }
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            var copiedLength: Long = 0
            val buffer: ByteArray = ByteArray(1024)

            inputStream = FileInputStream(record.initializeFile)
            outputStream = FileOutputStream(cutRecordFile)

            while (true) {
                val read: Int = inputStream.read(buffer)
                if (read == -1) {
                    break
                }
                if (startIndex != 0L && copiedLength == 0L) {
                    record.extension.header?.also { header ->
                        outputStream.write(header, 0, header.size)
                    }
                }
                copiedLength += read
                if (startIndex > copiedLength) {
                    continue
                }
                outputStream.write(buffer, 0, read)
            }

        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            LogUtil.w(TAG, "cutRecording: ${e.message}")

        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        callback?.let { it(cutRecordFile) }
    }

    /**
     * 부분 녹취 시작.
     */
    open fun startPartialRecord(
        record: Record? = currentCallRecord,
        fileName: String,
        callback: (() -> Unit)? = null
    ) {
        LogUtil.d(TAG, "startPartialRecord. fileName: $fileName")

        if (record == null) {
            LogUtil.w(TAG, "startPartialRecord: currentRecord is null.")
            return
        }
        if (record.isPartialRecord) {
            LogUtil.w(TAG, "startPartialRecord: already started partial recording.")
            return
        }
        when (IRRecorderv2.isACRRecordSet()) {
            true -> refreshPartialIndex()
            else -> IRRecorderv2.startRecordPartial(fileName)
        }
        record.isPartialRecord = true
        record.partialFileName = fileName

        callback?.let { it() }
    }

    /**
     * 부분 녹취 종료.
     */
    open fun endPartialRecord(
        record: Record? = currentCallRecord,
        callback: ((File) -> Unit)? = null
    ) {
        LogUtil.d(TAG, "stopPartialRecord.")

        if (record == null) {
            LogUtil.w(TAG, "stopPartialRecord: currentRecord is null.")
            return
        }
        if (!record.isPartialRecord) {
            LogUtil.w(TAG, "stopPartialRecord: already stopped partial recording.")
            return
        }
        if (!record.partialFile.exists()) {
            fileUtil.createFile(record.partialFile)
        }
        when (IRRecorderv2.isACRRecordSet()) {
            true -> cutRecord(record, record.partialFile.name, record.partialStartIndex) {
                onStoppedPartialRecord(record.partialFile, callback)
            }

            else -> IRRecorderv2.endRecordingPartial {
                onStoppedPartialRecord(record.partialFile, callback)
            }
        }
    }

    /**
     * 부분 녹취 종료 완료.
     */
    open fun onStoppedPartialRecord(partialRecordFile: File, callback: ((File) -> Unit)? = null) {
        LogUtil.d(TAG, "onStoppedPartialRecord. partialRecordFile: ${partialRecordFile.path}")
        callback?.let { it(partialRecordFile) }
        currentCallRecord?.partialStartIndex = 0
        currentCallRecord?.partialFileName = null
        currentCallRecord?.isPartialRecord = false
    }

    /**
     * 최종 파일명으로 레코드 찾기.
     */
    open fun findByFinalName(finalName: String): Record? {
        for (record: Record in records.values) {
            if (record.finalFileName != finalName) {
                continue
            }
            return record
        }
        return null
    }

    /**
     * 현재 전화 녹취 레코드 객체 초기화.
     */
    open fun clearCallCurrentRecord() {
        LogUtil.d(TAG, "clearCallCurrentRecord.")
        this.currentCallRecord = null
        this._isCallRecording.postValue = false
    }

    /**
     * 현재 대면 녹취 레코드 객체 초기회.
     */
    open fun clearFtfCurrentRecord() {
        LogUtil.d(TAG, "clearFtfCurrentRecord.")
        this.currentFtfRecord = null
        this._isFtfRecording.postValue = false
    }

    /**
     * 전송 실패 폴더로 이동.
     */
    open fun moveToFailDir(record: Record) {
        fileUtil.moveFile(
            record.finalFile,
            File(
                directoryManager.failDir,
                "${record.initializeFileName}^${record.finalFileName}.${record.extensionName}"
            )
        )
        record.saveDir = directoryManager.failDir
//        removeRecord(record)
    }

    /**
     * 사콜 폴더로 이동.
     */
    open fun moveToSaCallDir(record: Record) {
        fileUtil.moveFile(
            record.finalFile,
            File(directoryManager.saCallDir, record.finalFile.name)
        )
        record.saveDir = directoryManager.saCallDir
//        removeRecord(record)
    }

    /**
     * 레코드 파일 삭제.
     */
    open fun deleteRecordFile(record: Record) {
        fileUtil.deleteFile(record.finalFile)
//        removeRecord(record)
    }

    /**
     * 최종 파일명으로 변경.
     */
    open fun renameFinalFileName(record: Record) {
        if (record.ocxFileName.isNullOrEmpty()) {
            return
        }
        if (record.ocxFile.exists()) {
            LogUtil.w(TAG, "already exists file.")
            fileUtil.renameFile(
                record.ocxFile,
                "duplicate_${System.currentTimeMillis()}_record.ocxFile.name"
            )
        }
        fileUtil.renameFile(record.initializeFile, record.ocxFile)
    }

    /**
     * 맵에 레코드 객체 추가.
     */
    open fun addRecord(record: Record) {
        this._records += record.initializeFileName to record
        LogUtil.d(TAG, "addRecord. records.size: ${records.size}")
    }

    /**
     * 맵에서 레코드 객체 삭제.
     */
    open fun removeRecord(record: Record) {
        LogUtil.d(TAG, "removeRecord. record.finalFileName: ${record.finalFileName}")
        _records.remove(record.initializeFileName)
        LogUtil.d(TAG, "removeRecord. records.size: ${records.size}")
    }

    /**
     * 레코드 파일 업로드.
     */
    abstract fun uploadRecord(file: File, initFileName: String?): Disposable

    /**
     * 레코드 파일 업로드.
     */
    abstract fun uploadRecord(record: Record): Disposable

    /**
     * 업로드 완료 시 호출.
     */
    abstract fun onUploadedRecord(record: Record, result: String, isNotify: Boolean)

}