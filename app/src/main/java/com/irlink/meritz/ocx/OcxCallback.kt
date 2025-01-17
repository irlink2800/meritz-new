package com.irlink.meritz.ocx


import com.irlink.meritz.util.network.socket.SocketCallback
import com.irlink.meritz.util.network.socket.SocketState
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.removeSpecialCharacters


interface OcxCallback : SocketCallback {

    companion object {
        const val TAG: String = "OcxCallback"
    }

    override fun accept(state: SocketState) {
        try {
            if (state is SocketState.Response) {
                state.data.split(OcxParams.END).forEach { method: String? ->
                    if (!method.isNullOrEmpty()) onResponse("$method${OcxParams.END}")
                }
                return
            }
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
        }
        super.accept(state)
    }

    override fun onResponse(response: String) {
        try {
            val ocxParams: OcxParams = OcxParams(response, OcxCommand.Type.METHOD).apply {
                if (isAvailable) LogUtil.d(TAG, "onResponse. receiveMethod: $logData")
            }
            if (!ocxParams.isAvailable) {
                return
            }
            when (ocxParams.command) {
                OcxMethods.CHECK_CONNECTION -> {
                    onCheckConnection()
                }
                OcxMethods.DIAL -> onDial(
                    phoneNumber = ocxParams.first ?: return
                )
                OcxMethods.MUTE -> onMute(
                    isMute = ocxParams.first?.toOcxBoolean() ?: return
                )
                OcxMethods.USER_INPUT -> onUserInput(
                    isInputBlock = ocxParams.first?.toOcxBoolean(isReverse = true) ?: return
                )
                OcxMethods.SET_RECORD -> onSetRecord(
                    isRecord = ocxParams.first?.toOcxBoolean() ?: return
                )
                OcxMethods.SET_RECORD_FILE_NAME -> onSetRecordFileName(
                    recordFileName = ocxParams.first?.removeSpecialCharacters()?.trim() ?: return
                )
                OcxMethods.GET_CALL_STATE -> {
                    onGetCallState()
                }
                OcxMethods.SET_VOLUME -> onSetVolume(
                    volume = ocxParams.first?.toIntOrNull() ?: return
                )
                OcxMethods.GET_VOLUME -> onGetVolume(
                    isMaxVolume = ocxParams.first?.toOcxBoolean() ?: return
                )
                OcxMethods.SET_RECORD_FOLDER -> onSetRecordFolder(
                    folderName = ocxParams.first ?: return
                )
                OcxMethods.GET_RECORD_FOLDER -> {
                    onGetRecordFolder()
                }
                OcxMethods.CUT_CURRENT_RECORD -> onCutCurrentRecord(
                    fileNameSuffix = ocxParams.first?.removeSpecialCharacters() ?: return
                )
                OcxMethods.GET_CERT_STATE -> {
                    onGetCertState()
                }
                OcxMethods.DO_LOG_CERT -> {
                    onDoLogCert()
                }
                OcxMethods.LISTEN_FILE -> onListenFile(
                    fileUrl = ocxParams.first ?: return
                )
                OcxMethods.GET_PHONE_BOOK -> {
                    onGetPhoneBook()
                }
                OcxMethods.SEND_SMS -> onSendSms(
                    receiveNumber = ocxParams.second ?: "",
                    content = ocxParams.fourth ?: "",
                )
                OcxMethods.SET_ENC -> onSetEnc(
                    isEnc = ocxParams.first?.toOcxBoolean() ?: return
                )
                OcxMethods.GET_SMS_COUNT -> onGetSmsCount(
                    date = ocxParams.first?.replace("-", "") ?: return
                )
                OcxMethods.GET_FILES -> {
                    onGetFiles()
                }
                OcxMethods.CLOSE_DEVICE -> {
                    onCloseDevice()
                }
                OcxMethods.CREATE_DEVICE -> {
                    onCreateDevice(
                        code = ocxParams.first
                    )
                }
                OcxMethods.SET_SAVE_PATH -> onSetSavePath(
                    saveState = ocxParams.first?.toIntOrNull() ?: return,
                    uploadUrl = ocxParams.second ?: return,
                    uploadPath = ocxParams.third ?: return,
                    isIncludeDate = ocxParams.fourth?.toOcxBoolean() ?: return
                )
                OcxMethods.GET_SAVE_PATH -> {
                    onGetSavePath()
                }
                OcxMethods.SET_KCT -> onSetKct(
                    title = ocxParams.first ?: return,
                    content = ocxParams.second ?: return,
                    number = ocxParams.third ?: return,
                    size = ocxParams.fourth?.toLongOrNull()?.times(1024) ?: return
                )
                OcxMethods.SET_REC_PARTIAL -> onSetRecPartial(
                    isStartPartial = ocxParams.first?.toOcxBoolean() ?: return,
                    fileName = ocxParams.second?.removeSpecialCharacters() ?: return
                )
                OcxMethods.GET_BATTERY_INFO -> {
                    onGetBatteryInfo()
                }
                OcxMethods.SET_EXTRA -> ocxParams.first?.split(OcxParams.SUB_SEPARATOR)
                    ?.let { params ->
                        onSetExtra(
                            extra = params.getOrNull(OcxParams.SubIndex.FIRST) ?: "",
                            poolName = params.getOrNull(OcxParams.SubIndex.SECOND) ?: "",
                            userId = params.getOrNull(OcxParams.SubIndex.THIRD) ?: "",
                            agencyCode = params.getOrNull(OcxParams.SubIndex.FOURTH)?.toLongOrNull()
                                ?: 0L,
                            agencyName = params.getOrNull(OcxParams.SubIndex.FIFTH) ?: "",
                            smartDmId = params.getOrNull(OcxParams.SubIndex.SIXTH) ?: "",
                            smartDmNumber = params.getOrNull(OcxParams.SubIndex.SEVENTH) ?: "",
                            isMissedCallMessage = params.getOrNull(OcxParams.SubIndex.EIGHTH)
                                ?.toOcxBoolean() ?: false
                        )
                    }
                OcxMethods.GET_PHONE_BOOK_PARAM -> {
                    onGetPhoneBookParam()
                }
                OcxMethods.GET_MSG_LOG -> {
                    onGetMsgLog()
                }
                OcxMethods.GET_CALL_LOG -> {
                    onGetCallLog()
                }
                OcxMethods.GET_NETWORK_STATE -> {
                    onGetNetworkState()
                }
                OcxMethods.GET_PHONE_INFO -> {
                    onGetPhoneInfo()
                }
                OcxMethods.GET_PROFILE_IMG_URI -> {
                    onGetProfileImgUri()
                }
                OcxMethods.SET_FILE_RECV_RESULT -> onSetFileRecvResult(
                    filePath = ocxParams.first ?: return,
                    isSuccess = ocxParams.second?.toOcxBoolean() ?: return
                )
                OcxMethods.PLAY_REC_FILE -> {
                    onPlayRecFile()
                }
                OcxMethods.GET_CALL_MEMO -> {
                    onGetCallMemo()
                }
                OcxMethods.SET_CALL_MEMO -> {
                    onSetCallMemo()
                }
                OcxMethods.START_TO_SEND_DATA -> {
                    onStartToSendData()
                }
                OcxMethods.GET_ABSOLUTE_PATH -> {
                    onGetAbsolutePath()
                }
                OcxMethods.SET_RECORD_PAUSE -> {
                    onSetRecordPause()
                }
                OcxMethods.SET_RECORD_RESUME -> {
                    onSetRecordResume()
                }
                OcxMethods.SET_BACKUP -> onSetBackup(
                    isBackup = ocxParams.first?.toOcxBoolean() ?: return
                )
                OcxMethods.SEND_DEVICE_INFO -> {
                    onSendDeviceInfo()
                }
                OcxMethods.SET_HOOK_MODE -> onSetHookMode(
                    hookState = ocxParams.first ?: return
                )
                OcxMethods.GET_MAX_VOLUME -> {
                    onGetVolume(true)
                }
                OcxMethods.GET_RECORD_FILE_NAME -> {
                    onGetRecordFileName()
                }
                OcxMethods.KEEP_CONNECTION -> onKeepConnection(
                    code = ocxParams.first?.toIntOrNull() ?: return
                )
                OcxMethods.HANGUP_CALL -> onSetHookMode(
                    hookState = ocxParams.first ?: HookState.ON
                )
                OcxMethods.ANSWER_CALL -> onSetHookMode(
                    hookState = ocxParams.first ?: HookState.OFF
                )
                OcxMethods.REJECT_CALL -> onSetHookMode(
                    hookState = ocxParams.first ?: HookState.ON
                )
                OcxMethods.START_PARTIAL_RECORDING -> onSetRecPartial(
                    isStartPartial = true,
                    fileName = ocxParams.second ?: return
                )
                OcxMethods.STOP_PARTIAL_RECORDING -> onSetRecPartial(
                    isStartPartial = false,
                    fileName = ocxParams.second ?: return
                )
                OcxMethods.GET_BATTERY_STATE -> onGetBatteryState()
            }
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
        }
    }

    fun onCheckConnection()

    fun onDial(phoneNumber: String)

    fun onMute(isMute: Boolean)

    fun onUserInput(isInputBlock: Boolean)

    fun onSetRecord(isRecord: Boolean)

    fun onSetRecordFileName(recordFileName: String)

    fun onGetCallState()

    fun onSetVolume(volume: Int)

    fun onGetVolume(isMaxVolume: Boolean)

    fun onSetRecordFolder(folderName: String)

    fun onGetRecordFolder()

    fun onCutCurrentRecord(fileNameSuffix: String)

    fun onGetCertState()

    fun onDoLogCert()

    fun onListenFile(fileUrl: String)

    fun onGetPhoneBook()

    fun onSendSms(receiveNumber: String, content: String)

    fun onSetEnc(isEnc: Boolean)

    fun onGetSmsCount(date: String)

    fun onGetFiles()

    fun onCloseDevice()

    fun onCreateDevice(code: String?)

    fun onSetSavePath(
        saveState: Int,
        uploadUrl: String,
        uploadPath: String,
        isIncludeDate: Boolean
    )

    fun onGetSavePath()

    fun onSetKct(
        title: String,
        content: String,
        number: String,
        size: Long
    )

    fun onSetRecPartial(isStartPartial: Boolean, fileName: String)

    fun onGetBatteryInfo()

    fun onSetExtra(
        extra: String,
        poolName: String,
        userId: String,
        agencyCode: Long,
        agencyName: String,
        smartDmId: String,
        smartDmNumber: String,
        isMissedCallMessage: Boolean
    )

    fun onGetPhoneBookParam()

    fun onGetMsgLog()

    fun onGetCallLog()

    fun onGetNetworkState()

    fun onGetPhoneInfo()

    fun onGetProfileImgUri()

    fun onSetFileRecvResult(filePath: String, isSuccess: Boolean)

    fun onPlayRecFile()

    fun onGetCallMemo()

    fun onSetCallMemo()

    fun onStartToSendData()

    fun onSendMessage(
        receiveNumbers: List<String>,
        content: String,
        parts: Map<String, String>?,
        externalKey: String
    )

    fun onGetAbsolutePath()

    fun onSetRecordPause()

    fun onSetRecordResume()

    fun onSetBackup(isBackup: Boolean)

    fun onSendDeviceInfo()

    fun onSetHookMode(hookState: String)

    fun onGetRecordFileName()

    fun onKeepConnection(code: Int)

    fun onGetBatteryState()

}