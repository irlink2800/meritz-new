package com.irlink.meritz.ocx

import com.irlink.meritz.record.Record
import com.irlink.meritz.util.contact.Contacts
import io.reactivex.rxjava3.core.Flowable
import java.io.File

open class OcxEvent(

    private val ocx: Ocx

) {

    companion object {
        const val TAG: String = "OcxEvent"
    }

    open fun createDevice() = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_CREATE_DEVICE
        )
    )

    open fun hookOn() = updateHookState(
        hookState = HookState.ON
    )

    open fun hookOff() = updateHookState(
        hookState = HookState.OFF
    )

    protected open fun updateHookState(hookState: String) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_HOOK,
            first = hookState
        )
    )

    open fun bellOn() = updateBellState(
        isBell = true
    )

    open fun bellOff() = updateBellState(
        isBell = false
    )

    open fun updateBellState(isBell: Boolean) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_BELL,
            first = isBell.toOcxBoolean()
        )
    )

    open fun sendCidData(record: Record?) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_CID_DATA,
            first =  record?.getRecordInfo(ocx.ocxMode)
        )
    )

    open fun outgoing(record: Record?) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_OUTGOING,
            first =  record?.getRecordInfo(ocx.ocxMode)
        )
    )

    open fun startCall(record: Record?) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_CALL_START,
            first = record?.getRecordInfo(ocx.ocxMode)
        )
    )

    open fun endCall(record: Record?) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_CALL_END,
            first = record?.getRecordInfo(ocx.ocxMode)
        )
    )

    open fun sendVolume(volume: Int) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_VOLUME,
            first = volume.toString()
        )
    )

    open fun sendMaxVolume(maxVolume: Int) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_MAX_VOLUME,
            first = maxVolume.toString()
        )
    )

    open fun sendRecordFolder(folderPath: String) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_RECORD_FOLDER,
            first = folderPath
        )
    )

    open fun startRecord(record: Record?) = updateRecordState(
        recordState = RecordState.START,
        record = record
    )

    open fun endRecord(record: Record?) = updateRecordState(
        recordState = RecordState.END,
        record = record
    )

    open fun pauseRecord(record: Record?) = updateRecordState(
        recordState = RecordState.PAUSE,
        record = record
    )

    open fun resumeRecord(record: Record?) = updateRecordState(
        recordState = RecordState.RESUME,
        record = record
    )

    protected open fun updateRecordState(recordState: String, record: Record?) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_REC_START_END,
            first = recordState,
            second = when (recordState) {
                RecordState.END -> record?.finalFileName
                else -> record?.initializeFileName
            },
            third = record?.getRecordInfo(ocx.ocxMode)
        )
    )

    open fun cutRecord(fileName: String) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_CUT_RECORD,
            first = fileName
        )
    )

    open fun cert() = updateCertState(
        isCert = true
    )

    open fun unCert() = updateCertState(
        isCert = false
    )

    protected open fun updateCertState(isCert: Boolean) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_CERT_STATE,
            first = isCert.toOcxBoolean()
        )
    )

    open fun sendPhoneBooks(contacts: Contacts) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_PHONE_BOOK,
            first = contacts.summary
        )
    )

    open fun sendMessageSuccess() = sendMessageResult(
        result = SmsState.SUCCESS
    )

    open fun sendMessageFail() = sendMessageResult(
        result = SmsState.FAIL
    )

    protected open fun sendMessageResult(result: String) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_SMS,
            first = result
        )
    )

    open fun sendSmsCount(date: String, smsCount: Int) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_SMS_COUNT,
            first = "$date${OcxParams.SUB_SEPARATOR}$smsCount"
        )
    )

    open fun sendFiles(files: List<File>) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_GET_FILES,
            first = files.toOcxData()
        )
    )

    open fun sendSavePath(mode: Int, uploadUrl: String, uploadPath: String, isIncludeDate: Boolean) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_SAVE_PATH,
            first = mode.toString(),
            second = uploadUrl,
            third = uploadPath,
            fourth = isIncludeDate.toOcxBoolean()
        )
    )

    open fun startPartialRecord(fileName: String) = sendPartialRecord(
        isStart = true,
        fileName = fileName
    )

    open fun endPartialRecord(fileName: String) = sendPartialRecord(
        isStart = false,
        fileName = fileName
    )

    protected open fun sendPartialRecord(
        isStart: Boolean,
        fileName: String,
        isSuccess: Boolean = true
    ) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_REC_PARTIAL,
            first = isStart.toOcxBoolean(),
            second = fileName,
            third = isSuccess.toOcxBoolean()
        )
    )

    open fun sendRecordFileNameResult(fileName: String, isSuccess: Boolean) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_RECORD_FILE_NAME,
            first = isSuccess.toOcxBoolean(),
            second = fileName
        )
    )

    open fun sendCallState(isCallActive: Boolean) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_CALL_STATE,
            first = isCallActive.toOcxBoolean()
        )
    )

    open fun sendBatteryInfo(batteryLevel: Int) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_BATTERY_INFO,
            first = batteryLevel.toString()
        )
    )

    open fun sendAbsolutePath(path: String) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_ABSOLUTE_PATH,
            first = path
        )
    )

    open fun sendMessageInfo(address: String, timestamp: Long, message: String) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_SMS_MMS_RECV,
            first = address,
            second = timestamp.toString(),
            third = "sms",
            fourth = message
        )
    )

    open fun sendFileToPc(file: File, uploadPath: String) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_FILE_CREATED,
            first = file.path,
            second = uploadPath,
            third = file.length().toString()
        )
    )

    open fun sendUploadedResult(
        result: String,
        localFileName: String,
        remoteFileName: String
    ) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_UPLOADED,
            first = result,
            second = localFileName,
            third = remoteFileName
        )
    )

    open fun sendDeviceInfo(
        deviceId: String,
        phoneNumber: String,
        telecom: String,
        versionCode: Int
    ) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_DEVICE_INFO,
            first = deviceId,
            second = "${phoneNumber}${OcxParams.SUB_SEPARATOR}$telecom${OcxParams.SUB_SEPARATOR}$versionCode"
        )
    )

    open fun sendKeepConnection() = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_KEEP_CONNECTION
        )
    )

    open fun sendError(errorCode: Int) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_ERROR,
            first = errorCode.toString()
        )
    )

    open fun chargedBattery() = sendBatteryState(
        state = BatteryState.CHARGED
    )

    open fun dischargedBattery() = sendBatteryState(
        state = BatteryState.DISCHARGED
    )

    open fun sendBatteryState(state: String) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_BATTERY_STATE,
            first = state
        )
    )

    open fun sendMicMute(isMicMute: Boolean) = sendEvent(
        params = OcxParams(
            command = OcxEvents.DEV_MIC_MUTE,
            first = isMicMute.toOcxBoolean()
        )
    )

    open fun sendEvent(params: OcxParams): Flowable<Unit> = ocx.sendData(params)

}