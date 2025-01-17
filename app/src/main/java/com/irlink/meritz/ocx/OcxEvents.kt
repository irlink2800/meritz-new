package com.irlink.meritz.ocx

enum class OcxEvents(

    override val usb: Int,
    override val wireless: String?,
    override val tag: String

) : OcxCommand {

    DEV_CREATE_DEVICE(
        usb = 0,
        wireless = "CreateDevice",
        tag = "DEV_CREATE_DEVICE"
    ),

    DEV_HOOK(
        usb = 10,
        wireless = "DevHook",
        tag = "DEV_HOOK"
    ),

    DEV_BELL(
        usb = 11,
        wireless = "DevBell",
        tag = "DEV_BELL"
    ),

    DEV_CID_DATA(
        usb = 12,
        wireless = "DevCidData",
        tag = "DEV_CID_DATA"
    ),

    DEV_OUTGOING(
        usb = 13,
        wireless = "DevOutGoing",
        tag = "DEV_OUTGOING"
    ),

    DEV_REC_START_END(
        usb = 14,
        wireless = "DevRecStartEnd",
        tag = "DEV_REC_START_END"
    ),

    DEV_UPLOADED(
        usb = 15,
        wireless = "DevUploaded",
        tag = "DEV_UPLOADED"
    ),

    DEV_CALL_STATE(
        usb = 16,
        wireless = "DevCallState",
        tag = "DEV_CALL_STATE"
    ),

    DEV_VOLUME(
        usb = 17,
        wireless = "DevVolume",
        tag = "DEV_VOLUME"
    ),

    DEV_MAX_VOLUME(
        usb = 18,
        wireless = "DevMaxVolume",
        tag = "DEV_MAX_VOLUME"
    ),

    DEV_RECORD_FOLDER(
        usb = 19,
        wireless = "DevRecordFolder",
        tag = "DEV_RECORD_FOLDER"
    ),

    DEV_CUT_RECORD(
        usb = 20,
        wireless = "DevCutRecord",
        tag = "DEV_CUT_RECORD"
    ),

    DEV_CERT_STATE(
        usb = 21,
        wireless = null,
        tag = "DEV_CERT_STATE"
    ),

    DEV_PHONE_BOOK(
        usb = 23,
        wireless = "DevPhoneBook",
        tag = "DEV_PHONE_BOOK"
    ),

    DEV_SMS(
        usb = 24,
        wireless = "DevSMS",
        tag = "DEV_SMS"
    ),

    DEV_CALL_START(
        usb = 25,
        wireless = "DevCallStart",
        tag = "DEV_CALL_START"
    ),

    DEV_CALL_END(
        usb = 26,
        wireless = "DevCallEnd",
        tag = "DEV_CALL_END"
    ),

    DEV_SMS_COUNT(
        usb = 27,
        wireless = "DevSmsCount",
        tag = "DEV_SMS_COUNT"
    ),

    DEV_GET_FILES(
        usb = 28,
        wireless = "DevFileList",
        tag = "DEV_GET_FILES"
    ),

    DEV_SAVE_PATH(
        usb = 29,
        wireless = "DevSavePath",
        tag = "DEV_SAVE_PATH"
    ),

    DEV_REC_PARTIAL(
        usb = 30,
        wireless = "DevRecPartial",
        tag = "DEV_REC_PARTIAL"
    ),

    DEV_RECORD_FILE_NAME(
        usb = 31,
        wireless = "DevRecordFileName",
        tag = "DEV_RECORD_FILE_NAME"
    ),

    DEV_DATA_START(
        usb = 32,
        wireless = null,
        tag = "DEV_DATA_START"
    ),

    DEV_DATA_END(
        usb = 33,
        wireless = null,
        tag = "DEV_DATA_END"
    ),

    DEV_SMS_RECV(
        usb = 34,
        wireless = null,
        tag = "DEV_SMS_RECV"
    ),

    DEV_BATTERY_INFO(
        usb = 35,
        wireless = "DevBatteryInfo",
        tag = "DEV_BATTERY_INFO"
    ),

    DEV_PHONE_BOOK_PARAM(
        usb = 36,
        wireless = null,
        tag = "DEV_PHONE_BOOK_PARAM"
    ),

    DEV_MSG_LOG(
        usb = 37,
        wireless = null,
        tag = "DEV_MSG_LOG"
    ),

    DEV_CALL_LOG(
        usb = 38,
        wireless = null,
        tag = "DEV_CALL_LOG"
    ),

    DEV_NETWORK_STATE(
        usb = 39,
        wireless = null,
        tag = "DEV_NETWORK_STATE"
    ),

    DEV_PHONE_INFO(
        usb = 40,
        wireless = null,
        tag = "DEV_PHONE_INFO"
    ),

    DEV_PROFILE_IMAGE(
        usb = 41,
        wireless = null,
        tag = "DEV_PROFILE_IMAGE"
    ),

    DEV_PLAY_REC_FILE(
        usb = 42,
        wireless = null,
        tag = "DEV_PLAY_REC_FILE"
    ),

    DEV_CALL_MEMO(
        usb = 43,
        wireless = null,
        tag = "DEV_CALL_MEMO"
    ),

    DEV_FILE_CREATED(
        usb = 44,
        wireless = null,
        tag = "DEV_FILE_CREATED"
    ),

    DEV_MESSAGE(
        usb = 45,
        wireless = "DevMessageExt",
        tag = "DEV_MESSAGE"
    ),

    DEV_ABSOLUTE_PATH(
        usb = 46,
        wireless = null,
        tag = "DEV_ABSOLUTE_PATH"
    ),

    DEV_SMS_MMS_RECV(
        usb = 47,
        wireless = null,
        tag = "DEV_SMS_MMS_RECV"
    ),

    DEV_DEVICE_INFO(
        usb = 48,
        wireless = null,
        tag = "DEV_DEVICE_INFO"
    ),

    DEV_ERROR(
        usb = 49,
        wireless = "recordingError",
        tag = "DEV_ERROR"
    ),

    DEV_KEEP_CONNECTION(
        usb = -1,
        wireless = "KeepConnection",
        tag = "DEV_KEEP_CONNECTION"
    ),

    DEV_PAUSE_RECORDING(
        usb = -2,
        wireless = "DevPauseRecording",
        tag = "DEV_PAUSE_RECORDING"
    ),

    DEV_RESUME_RECORDING(
        usb = -3,
        wireless = "DevResumeRecording",
        tag = "DEV_RESUME_RECORDING"
    ),

    DEV_STATE(
        usb = -4,
        wireless = "state",
        tag = "DEV_STATE"
    ),

    DEV_BATTERY_STATE(
        usb = -5,
        wireless = "DevBatteryState",
        tag = "DEV_BATTERY_STATE"
    ),

    DEV_CALL_CONNECT(
        usb = -6,
        wireless = "DevCallConnect",
        tag = "DEV_CALL_CONNECT"
    ),

    DEV_MIC_MUTE(
        usb = -7,
        wireless = "DevMicMute",
        tag = "DEV_MIC_MUTE"
    ),

    DEV_DND(
        usb = -8,
        wireless = "DevDnd",
        tag = "DEV_DND"
    )
}

/**
 * IR-USB(Int) to OcxMethod.
 */
fun Int?.toIrUsbEvent(): OcxCommand? = when (this) {
    OcxEvents.DEV_CREATE_DEVICE.usb -> OcxEvents.DEV_CREATE_DEVICE
    OcxEvents.DEV_HOOK.usb -> OcxEvents.DEV_HOOK
    OcxEvents.DEV_BELL.usb -> OcxEvents.DEV_BELL
    OcxEvents.DEV_CID_DATA.usb -> OcxEvents.DEV_CID_DATA
    OcxEvents.DEV_OUTGOING.usb -> OcxEvents.DEV_OUTGOING
    OcxEvents.DEV_REC_START_END.usb -> OcxEvents.DEV_REC_START_END
    OcxEvents.DEV_UPLOADED.usb -> OcxEvents.DEV_UPLOADED
    OcxEvents.DEV_CALL_STATE.usb -> OcxEvents.DEV_CALL_STATE
    OcxEvents.DEV_VOLUME.usb -> OcxEvents.DEV_VOLUME
    OcxEvents.DEV_MAX_VOLUME.usb -> OcxEvents.DEV_MAX_VOLUME
    OcxEvents.DEV_RECORD_FOLDER.usb -> OcxEvents.DEV_RECORD_FOLDER
    OcxEvents.DEV_CUT_RECORD.usb -> OcxEvents.DEV_CUT_RECORD
    OcxEvents.DEV_CERT_STATE.usb -> OcxEvents.DEV_CERT_STATE
    OcxEvents.DEV_PHONE_BOOK.usb -> OcxEvents.DEV_PHONE_BOOK
    OcxEvents.DEV_SMS.usb -> OcxEvents.DEV_SMS
    OcxEvents.DEV_CALL_START.usb -> OcxEvents.DEV_CALL_START
    OcxEvents.DEV_CALL_END.usb -> OcxEvents.DEV_CALL_END
    OcxEvents.DEV_SMS_COUNT.usb -> OcxEvents.DEV_SMS_COUNT
    OcxEvents.DEV_GET_FILES.usb -> OcxEvents.DEV_GET_FILES
    OcxEvents.DEV_SAVE_PATH.usb -> OcxEvents.DEV_SAVE_PATH
    OcxEvents.DEV_REC_PARTIAL.usb -> OcxEvents.DEV_REC_PARTIAL
    OcxEvents.DEV_RECORD_FILE_NAME.usb -> OcxEvents.DEV_RECORD_FILE_NAME
    OcxEvents.DEV_DATA_START.usb -> OcxEvents.DEV_DATA_START
    OcxEvents.DEV_DATA_END.usb -> OcxEvents.DEV_DATA_END
    OcxEvents.DEV_SMS_RECV.usb -> OcxEvents.DEV_SMS_RECV
    OcxEvents.DEV_BATTERY_INFO.usb -> OcxEvents.DEV_BATTERY_INFO
    OcxEvents.DEV_PHONE_BOOK_PARAM.usb -> OcxEvents.DEV_PHONE_BOOK_PARAM
    OcxEvents.DEV_MSG_LOG.usb -> OcxEvents.DEV_MSG_LOG
    OcxEvents.DEV_CALL_LOG.usb -> OcxEvents.DEV_CALL_LOG
    OcxEvents.DEV_NETWORK_STATE.usb -> OcxEvents.DEV_NETWORK_STATE
    OcxEvents.DEV_PHONE_INFO.usb -> OcxEvents.DEV_PHONE_INFO
    OcxEvents.DEV_PROFILE_IMAGE.usb -> OcxEvents.DEV_PROFILE_IMAGE
    OcxEvents.DEV_PLAY_REC_FILE.usb -> OcxEvents.DEV_PLAY_REC_FILE
    OcxEvents.DEV_CALL_MEMO.usb -> OcxEvents.DEV_CALL_MEMO
    OcxEvents.DEV_FILE_CREATED.usb -> OcxEvents.DEV_FILE_CREATED
    OcxEvents.DEV_MESSAGE.usb -> OcxEvents.DEV_MESSAGE
    OcxEvents.DEV_ABSOLUTE_PATH.usb -> OcxEvents.DEV_ABSOLUTE_PATH
    OcxEvents.DEV_SMS_MMS_RECV.usb -> OcxEvents.DEV_SMS_MMS_RECV
    OcxEvents.DEV_DEVICE_INFO.usb -> OcxEvents.DEV_DEVICE_INFO
    OcxEvents.DEV_ERROR.usb -> OcxEvents.DEV_ERROR
    OcxEvents.DEV_KEEP_CONNECTION.usb -> OcxEvents.DEV_KEEP_CONNECTION
    OcxEvents.DEV_PAUSE_RECORDING.usb -> OcxEvents.DEV_PAUSE_RECORDING
    OcxEvents.DEV_RESUME_RECORDING.usb -> OcxEvents.DEV_RESUME_RECORDING
    OcxEvents.DEV_BATTERY_STATE.usb -> OcxEvents.DEV_BATTERY_STATE
    OcxEvents.DEV_CALL_CONNECT.usb -> OcxEvents.DEV_CALL_CONNECT
    OcxEvents.DEV_MIC_MUTE.usb -> OcxEvents.DEV_MIC_MUTE
    OcxEvents.DEV_DND.usb -> OcxEvents.DEV_DND
    else -> null
}

/**
 * IR-WIRELESS(String) to OcxMethod.
 */
fun String?.toIrWirelessEvent(): OcxCommand? = when (this) {
    OcxEvents.DEV_CREATE_DEVICE.wireless -> OcxEvents.DEV_CREATE_DEVICE
    OcxEvents.DEV_HOOK.wireless -> OcxEvents.DEV_HOOK
    OcxEvents.DEV_BELL.wireless -> OcxEvents.DEV_BELL
    OcxEvents.DEV_CID_DATA.wireless -> OcxEvents.DEV_CID_DATA
    OcxEvents.DEV_OUTGOING.wireless -> OcxEvents.DEV_OUTGOING
    OcxEvents.DEV_REC_START_END.wireless -> OcxEvents.DEV_REC_START_END
    OcxEvents.DEV_UPLOADED.wireless -> OcxEvents.DEV_UPLOADED
    OcxEvents.DEV_CALL_STATE.wireless -> OcxEvents.DEV_CALL_STATE
    OcxEvents.DEV_VOLUME.wireless -> OcxEvents.DEV_VOLUME
    OcxEvents.DEV_MAX_VOLUME.wireless -> OcxEvents.DEV_MAX_VOLUME
    OcxEvents.DEV_RECORD_FOLDER.wireless -> OcxEvents.DEV_RECORD_FOLDER
    OcxEvents.DEV_CUT_RECORD.wireless -> OcxEvents.DEV_CUT_RECORD
    OcxEvents.DEV_CERT_STATE.wireless -> OcxEvents.DEV_CERT_STATE
    OcxEvents.DEV_PHONE_BOOK.wireless -> OcxEvents.DEV_PHONE_BOOK
    OcxEvents.DEV_SMS.wireless -> OcxEvents.DEV_SMS
    OcxEvents.DEV_CALL_START.wireless -> OcxEvents.DEV_CALL_START
    OcxEvents.DEV_CALL_END.wireless -> OcxEvents.DEV_CALL_END
    OcxEvents.DEV_SMS_COUNT.wireless -> OcxEvents.DEV_SMS_COUNT
    OcxEvents.DEV_GET_FILES.wireless -> OcxEvents.DEV_GET_FILES
    OcxEvents.DEV_SAVE_PATH.wireless -> OcxEvents.DEV_SAVE_PATH
    OcxEvents.DEV_REC_PARTIAL.wireless -> OcxEvents.DEV_REC_PARTIAL
    OcxEvents.DEV_RECORD_FILE_NAME.wireless -> OcxEvents.DEV_RECORD_FILE_NAME
    OcxEvents.DEV_DATA_START.wireless -> OcxEvents.DEV_DATA_START
    OcxEvents.DEV_DATA_END.wireless -> OcxEvents.DEV_DATA_END
    OcxEvents.DEV_SMS_RECV.wireless -> OcxEvents.DEV_SMS_RECV
    OcxEvents.DEV_BATTERY_INFO.wireless -> OcxEvents.DEV_BATTERY_INFO
    OcxEvents.DEV_PHONE_BOOK_PARAM.wireless -> OcxEvents.DEV_PHONE_BOOK_PARAM
    OcxEvents.DEV_MSG_LOG.wireless -> OcxEvents.DEV_MSG_LOG
    OcxEvents.DEV_CALL_LOG.wireless -> OcxEvents.DEV_CALL_LOG
    OcxEvents.DEV_NETWORK_STATE.wireless -> OcxEvents.DEV_NETWORK_STATE
    OcxEvents.DEV_PHONE_INFO.wireless -> OcxEvents.DEV_PHONE_INFO
    OcxEvents.DEV_PROFILE_IMAGE.wireless -> OcxEvents.DEV_PROFILE_IMAGE
    OcxEvents.DEV_PLAY_REC_FILE.wireless -> OcxEvents.DEV_PLAY_REC_FILE
    OcxEvents.DEV_CALL_MEMO.wireless -> OcxEvents.DEV_CALL_MEMO
    OcxEvents.DEV_FILE_CREATED.wireless -> OcxEvents.DEV_FILE_CREATED
    OcxEvents.DEV_MESSAGE.wireless -> OcxEvents.DEV_MESSAGE
    OcxEvents.DEV_ABSOLUTE_PATH.wireless -> OcxEvents.DEV_ABSOLUTE_PATH
    OcxEvents.DEV_SMS_MMS_RECV.wireless -> OcxEvents.DEV_SMS_MMS_RECV
    OcxEvents.DEV_DEVICE_INFO.wireless -> OcxEvents.DEV_DEVICE_INFO
    OcxEvents.DEV_ERROR.wireless -> OcxEvents.DEV_ERROR
    OcxEvents.DEV_KEEP_CONNECTION.wireless -> OcxEvents.DEV_KEEP_CONNECTION
    OcxEvents.DEV_PAUSE_RECORDING.wireless -> OcxEvents.DEV_PAUSE_RECORDING
    OcxEvents.DEV_RESUME_RECORDING.wireless -> OcxEvents.DEV_RESUME_RECORDING
    OcxEvents.DEV_BATTERY_STATE.wireless -> OcxEvents.DEV_BATTERY_STATE
    OcxEvents.DEV_CALL_CONNECT.wireless -> OcxEvents.DEV_CALL_CONNECT
    OcxEvents.DEV_MIC_MUTE.wireless -> OcxEvents.DEV_MIC_MUTE
    OcxEvents.DEV_DND.wireless -> OcxEvents.DEV_DND
    else -> null
}