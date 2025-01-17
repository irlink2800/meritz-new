package com.irlink.meritz.ocx

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.util.network.socket.SocketState
import com.irlink.meritz.util.network.socket.subscribeSocket
import com.irlink.meritz.ocx.wireless.IrWireless
import com.irlink.meritz.ocx.wireless.WirelessPreference
import com.irlink.meritz.util.*
import com.irlink.meritz.util.base.livedata.Event
import com.irlink.meritz.util.call.CallUtil
import com.irlink.meritz.util.extension.runOnMainThread
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class OcxManager(

    private val applicationContext: Context,
    private val versionCode: Int,
    override var ocxMode: OcxMode,
    private val ocxPref: OcxPreference,
    private val wirelessPref: WirelessPreference,
    private val callUtil: CallUtil,
    private val audioUtil: AudioUtil,
    private val windowUtil: WindowUtil,
    private val deviceUtil: DeviceUtil,
    private val batteryUtil: BatteryUtil,
    private val okHttpClient: OkHttpClient,
    private val directoryManager: DirectoryManager

) : Ocx, OcxCallback {

    companion object {
        const val TAG: String = "OcxManager"
    }

    private val irWireless: Ocx by lazy {
        IrWireless(okHttpClient, wirelessPref)
    }

    private val currentOcx: Ocx
        get() = when (ocxMode) {
            OcxMode.WIRELESS -> irWireless
        }

    override val event: OcxEvent
        get() = currentOcx.event

    /**
     * 소켓 연결상태 리턴. (Response, Error 제외)
     */
    private val _socketState: MutableLiveData<SocketState> = MutableLiveData(SocketState.Closed)
    val socketState: LiveData<SocketState> = _socketState

    /**
     * 파일명 변경 이벤트.
     */
    private val _onSetRecordFileName: MutableLiveData<Event<Pair<Boolean, String>>> =
        MutableLiveData()
    val onSetRecordFileName: LiveData<Event<Pair<Boolean, String>>> = _onSetRecordFileName

    /**
     * PC 저장 결과 이벤트.
     */
    private val _onFileUploadedToPc: MutableLiveData<Event<Pair<Boolean, String>>> =
        MutableLiveData()
    val onFileUploadedToPc: LiveData<Event<Pair<Boolean, String>>> = _onFileUploadedToPc

    /**
     * 전화 걸기.
     */
    private val _startCall: PublishSubject<String> = PublishSubject.create()
    val startCall: Observable<String> = _startCall

    /**
     * 전화 받기.
     */
    private val _connectCall: PublishSubject<Unit> = PublishSubject.create()
    val connectCall: Observable<Unit> = _connectCall

    /**
     * 전화 끊기.
     */
    private val _disconnectCall: PublishSubject<Unit> = PublishSubject.create()
    val disconnectCall: Observable<Unit> = _disconnectCall


    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val sendSmsExecutor by lazy {
        Executors.newFixedThreadPool(3)
    }

    init {
        startCall
            .throttleFirst(
                3_000, TimeUnit.MILLISECONDS
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { phoneNumber ->
                    ocxPref.keepSetDialStr = phoneNumber
                    OuterActivities.intentCall(applicationContext, phoneNumber)
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
        connectCall
            .throttleFirst(
                3_000, TimeUnit.MILLISECONDS
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    callUtil.connectCall()
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
        disconnectCall
            .throttleFirst(
                3_000, TimeUnit.MILLISECONDS
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    callUtil.disconnectCall()
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
    }


    fun setOcxMode(ocxMode: OcxMode, isRestart: Boolean = true) {
        LogUtil.d(TAG, "setOcxMode: ${ocxMode.name}")
        if (this.ocxMode == ocxMode) {
            return
        }
        if (!isRestart || socketState.value == SocketState.Closed) {
            this.ocxMode = ocxMode
            return
        }
        close()
            .onBackpressureBuffer()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                this.ocxMode = ocxMode
                open()
                    .onBackpressureBuffer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribeSocket(this)
    }

    fun setKey(key: String?) {
        (currentOcx as? IrWireless)?.key = key
    }

    override fun accept(state: SocketState) {
        super.accept(state)
        if (state is SocketState.Response) {
            return
        }
        if (state is SocketState.Error) {
            return
        }
        _socketState.postValue(state)
    }

    override fun open(): Flowable<SocketState> = currentOcx.open()

    override fun close(): Flowable<Unit> = currentOcx.close()

    override fun sendData(params: OcxParams): Flowable<Unit> =
        currentOcx.sendData(params)

    override fun onOpened() {
        LogUtil.d(TAG, "onOpened.")
    }

    override fun onConnected() {
        LogUtil.d(TAG, "onConnected.")
        if (ocxMode == OcxMode.WIRELESS) {
            setKey(deviceUtil.phoneNumber)
        }
        compositeDisposable += event.createDevice().ocxExecute()
    }

    override fun onCheckConnection() {
        // empty..
    }

    override fun onDial(phoneNumber: String) {
        _startCall.onNext(phoneNumber)
    }

    override fun onMute(isMute: Boolean) {
        audioUtil.isMicrophoneMute = isMute
    }

    override fun onUserInput(isInputBlock: Boolean) {
        compositeDisposable += runOnMainThread {
            windowUtil.block(isInputBlock)
        }
    }

    override fun onSetRecord(isRecord: Boolean) {
        ocxPref.isRecord = isRecord
    }

    override fun onSetRecordFileName(recordFileName: String) {
    }

    override fun onGetCallState() {
        compositeDisposable += event.sendCallState(ocxPref.isCallActive).ocxExecute()
    }

    override fun onSetVolume(volume: Int) {
        audioUtil.volume = volume
    }

    override fun onGetVolume(isMaxVolume: Boolean) {
        compositeDisposable += when (isMaxVolume) {
            true -> event.sendMaxVolume(audioUtil.maxVolume)
            else -> event.sendVolume(audioUtil.volume)
        }.ocxExecute()
    }

    override fun onSetRecordFolder(folderName: String) {
        directoryManager.renameRecordDir(folderName)
    }

    override fun onGetRecordFolder() {
        ocxPref.recordFolderPath = directoryManager.recordDir.path
        compositeDisposable += event.sendRecordFolder(
            folderPath = ocxPref.recordFolderPath
        ).ocxExecute()
    }

    override fun onCutCurrentRecord(fileNameSuffix: String) {
    }

    override fun onGetCertState() {
        // empty..
    }

    override fun onDoLogCert() {
        // empty..
    }

    override fun onListenFile(fileUrl: String) {}

    override fun onGetPhoneBook() {}

    override fun onSendSms(receiveNumber: String, content: String) {
        onSendMessage(arrayListOf(receiveNumber), content, null, "")
    }

    override fun onSendMessage(
        receiveNumbers: List<String>,
        content: String,
        parts: Map<String, String>?,
        externalKey: String
    ) {
    }

    override fun onSetEnc(isEnc: Boolean) {
    }

    override fun onGetSmsCount(date: String) {}

    override fun onGetFiles() {
        val recordFiles: MutableList<File> = mutableListOf<File>().apply {
            this += directoryManager.recordDir.listFiles()?.toList() ?: listOf()
            this += directoryManager.encDir.listFiles()?.toList() ?: listOf()
            this += directoryManager.decDir.listFiles()?.toList() ?: listOf()
            this += directoryManager.failDir.listFiles()?.toList() ?: listOf()
        }
        compositeDisposable += event.sendFiles(recordFiles).ocxExecute()
    }

    override fun onCreateDevice(code: String?) {
        when (code) {
            CreateDeviceState.PAIRED -> {

            }
            CreateDeviceState.DUPLICATE_WEB_LOGIN -> {
                compositeDisposable += close().ocxExecute()
            }
        }
        ocxPref.isSaCall = false
    }

    override fun onCloseDevice() {
        ocxPref.isSaCall = true
        windowUtil.block(false)
    }

    override fun onSetSavePath(
        saveState: Int,
        uploadUrl: String,
        uploadPath: String,
        isIncludeDate: Boolean
    ) {
        ocxPref.saveState = saveState
        ocxPref.uploadHost = uploadUrl
        ocxPref.uploadPath = uploadPath
        ocxPref.isRecordIncludeDate = isIncludeDate
        onGetSavePath()
    }

    override fun onGetSavePath() {
        this.compositeDisposable += event.sendSavePath(
            mode = ocxPref.saveState,
            uploadUrl = ocxPref.uploadHost,
            uploadPath = ocxPref.uploadPath,
            isIncludeDate = ocxPref.isRecordIncludeDate
        ).ocxExecute()
    }

    override fun onSetKct(
        title: String,
        content: String,
        number: String,
        size: Long
    ) {
        ocxPref.kctTitle = title
        ocxPref.kctContent = content
        ocxPref.kctNumber = number
        ocxPref.kctSize = size
    }

    override fun onSetRecPartial(isStartPartial: Boolean, fileName: String) {
//        val record = when (isStartPartial) {
//            true -> recordManager.currentCallRecord
//            else -> recordManager.records.values.firstOrNull {
//                it.partialFileName == fileName
//            }
//        }
//        onSetRecPartial(record, isStartPartial, fileName)
    }

//    open fun onSetRecPartial(record: Record?, isStartPartial: Boolean, fileName: String) {
//        compositeDisposable += when (isStartPartial) {
//            true -> {
//                recordManager.startPartialRecord(record, fileName)
//                event.startPartialRecord(fileName).ocxExecute()
//            }
//            false -> {
//                recordManager.endPartialRecord(record) {
//                    record?.partialRecord()?.let { partialRecord ->
//                        recordManager.addRecord(partialRecord)
//                        recordManager.uploadRecord(partialRecord)
//                    }
//                }
//                event.endPartialRecord(fileName).ocxExecute()
//            }
//        }
//    }

    override fun onGetBatteryInfo() {
        compositeDisposable += event.sendBatteryInfo(batteryUtil.batteryCapacity).ocxExecute()
    }

    override fun onGetBatteryState() {
        compositeDisposable += when (batteryUtil.isCharging) {
            true -> event.chargedBattery()
            else -> event.dischargedBattery()
        }.ocxExecute()
    }

    override fun onSetExtra(
        extra: String,
        poolName: String,
        userId: String,
        agencyCode: Long,
        agencyName: String,
        smartDmId: String,
        smartDmNumber: String,
        isMissedCallMessage: Boolean
    ) {
        ocxPref.extra = extra
        ocxPref.poolName = poolName
        ocxPref.userId = userId
        ocxPref.agencyCode = agencyCode
        ocxPref.agencyName = agencyName
        ocxPref.smartDmId = smartDmId
        ocxPref.smartDmNumber = smartDmNumber
        ocxPref.isMissedCallMessage = isMissedCallMessage
    }

    override fun onGetPhoneBookParam() {
        // empty..
    }

    override fun onGetMsgLog() {
        // empty..
    }

    override fun onGetCallLog() {
        // empty..
    }

    override fun onGetNetworkState() {
        // empty..
    }

    override fun onGetPhoneInfo() {
        // empty..
    }

    override fun onGetProfileImgUri() {
        // empty..
    }

    override fun onSetFileRecvResult(filePath: String, isSuccess: Boolean) {
//        _onFileUploadedToPc.postNotify = isSuccess to filePath
    }

    override fun onPlayRecFile() {
        // empty..
    }

    override fun onGetCallMemo() {
        // empty..
    }

    override fun onSetCallMemo() {
        // empty..
    }

    override fun onStartToSendData() {
        // empty..
    }


    override fun onGetAbsolutePath() {
        val path: String = directoryManager.rootDir.path
        compositeDisposable += event.sendAbsolutePath(path).ocxExecute()
    }

    override fun onSetRecordPause() {
    }

    override fun onSetRecordResume() {

    }

//    override fun onSetRecordPause() {
//        if (!ocxPref.isCallActive) {
//            return
//        }
//        recordManager.pauseRecording()
//        compositeDisposable += event.pauseRecord(recordManager.currentCallRecord).ocxExecute()
//    }
//
//    override fun onSetRecordResume() {
//        if (!ocxPref.isCallActive) {
//            return
//        }
//        recordManager.resumeRecording()
//        compositeDisposable += event.resumeRecord(recordManager.currentCallRecord).ocxExecute()
//    }

    override fun onSetBackup(isBackup: Boolean) {
        ocxPref.isRecordBackup = isBackup
    }

    override fun onSendDeviceInfo() {
        compositeDisposable += event.sendDeviceInfo(
            deviceId = deviceUtil.deviceId,
            phoneNumber = deviceUtil.phoneNumber,
            telecom = deviceUtil.telecom,
            versionCode = versionCode
        ).ocxExecute()
    }

    override fun onSetHookMode(hookState: String) {
        when (hookState) {
            HookState.ON -> _disconnectCall.onNext(Unit)
            HookState.OFF -> _connectCall.onNext(Unit)
        }
    }

    override fun onGetRecordFileName() {}

    override fun onKeepConnection(code: Int) {
        if (code == CreateDeviceState.DUPLICATE_WEB_LOGIN.toInt()) {
            compositeDisposable += event.sendKeepConnection().ocxExecute()
        }
    }

    override fun onDisconnected() {
        LogUtil.d(TAG, "onDisconnected.")
        setKey(null)
        ocxPref.isSaCall = true
        ocxPref.extra = ""
        ocxPref.poolName = ""
        ocxPref.userId = ""
        ocxPref.agencyCode = 0
        ocxPref.agencyName = ""
        ocxPref.smartDmId = ""
        ocxPref.smartDmNumber = ""
        ocxPref.isMissedCallMessage = false
        windowUtil.block(false)
    }

    override fun onClosed() {
        LogUtil.d(TAG, "onClosed.")
        clear()
    }

    /**
     * 소켓 관련 에러.
     */
    override fun onError(throwable: Throwable) {
        LogUtil.e(TAG, "onError = ${throwable.message}")
    }

    /**
     * 녹취 에러 처리 스레드.
     */
    private val recordErrorExecutor: Executor by lazy {
        Executors.newSingleThreadExecutor()
    }

//    /**
//     * 녹취 에러.
//     */
//    open fun onRecordError(recordError: RecordError) {
//        recordErrorExecutor.execute {
//            if (ocxErrorHandler.onRecordError(recordError)) {
//                compositeDisposable += event.sendError(recordError.code).ocxExecute()
//            }
//        }
//    }

    fun clear() {
        LogUtil.d(TAG, "clear.")
        compositeDisposable.clear()
    }

}