package com.irlink.meritz.ocx

enum class OcxMethods(

    override val usb: Int,
    override val wireless: String?,
    override val tag: String

) : OcxCommand {

    CHECK_CONNECTION(
        usb = 1,
        wireless = null,
        tag = "CHECK_CONNECTION"
    ),

    DIAL(
        usb = 2,
        wireless = "SetDialStr",
        tag = "DIAL"
    ),

    MUTE(
        usb = 3,
        wireless = "SetMicMute",
        tag = "MUTE"
    ),

    USER_INPUT(
        usb = 4,
        wireless = "SetUserInput",
        tag = "USER_INPUT"
    ),

    SET_RECORD(
        usb = 5,
        wireless = "SetRecord",
        tag = "SET_RECORD"
    ),

    SET_RECORD_FILE_NAME(
        usb = 6,
        wireless = "SetRecordFileName",
        tag = "SET_RECORD_FILE_NAME"
    ),

    SET_UPLOAD_FILE(
        usb = 7,
        wireless = "SetUploadFile",
        tag = "SET_UPLOAD_FILE"
    ),

    GET_CALL_STATE(
        usb = 8,
        wireless = "GetCallState",
        tag = "GET_CALL_STATE"
    ),

    SET_VOLUME(
        usb = 9,
        wireless = "SetVolume",
        tag = "SET_VOLUME"
    ),

    GET_VOLUME(
        usb = 10,
        wireless = "GetVolume",
        tag = "GET_VOLUME"
    ),

    SET_RECORD_FOLDER(
        usb = 11,
        wireless = "SetRecordFolder",
        tag = "SET_RECORD_FOLDER"
    ),

    GET_RECORD_FOLDER(
        usb = 12,
        wireless = "GetRecordFolder",
        tag = "GET_RECORD_FOLDER"
    ),

    CUT_CURRENT_RECORD(
        usb = 13,
        wireless = "CutCurrentRecord",
        tag = "CUT_CURRENT_RECORD"
    ),

    GET_CERT_STATE(
        usb = 14,
        wireless = null,
        tag = "GET_CERT_STATE"
    ),

    DO_LOG_CERT(
        usb = 15,
        wireless = null,
        tag = "DO_LOG_CERT"
    ),

    LISTEN_FILE(
        usb = 16,
        wireless = "ListenFile",
        tag = "LISTEN_FILE"
    ),

    GET_PHONE_BOOK(
        usb = 17,
        wireless = "GetPhoneBook",
        tag = "GET_PHONE_BOOK"
    ),

    SEND_SMS(
        usb = 18,
        wireless = "SendSMS",
        tag = "SEND_SMS"
    ),

    SET_ENC(
        usb = 19,
        wireless = "SetEnc",
        tag = "SET_ENC"
    ),

    GET_SMS_COUNT(
        usb = 20,
        wireless = "GetSmsCount",
        tag = "GET_SMS_COUNT"
    ),

    GET_FILES(
        usb = 21,
        wireless = "GetFileList",
        tag = "GET_FILES"
    ),

    CLOSE_DEVICE(
        usb = 22,
        wireless = "CloseDevice",
        tag = "CLOSE_DEVICE"
    ),

    CREATE_DEVICE(
        usb = 23,
        wireless = "CreateDevice",
        tag = "CREATE_DEVICE"
    ),

    SET_SAVE_PATH(
        usb = 24,
        wireless = "SetSavePath",
        tag = "SET_SAVE_PATH"
    ),

    GET_SAVE_PATH(
        usb = 25,
        wireless = "GetSavePath",
        tag = "GET_SAVE_PATH"
    ),

    SET_KCT(
        usb = 26,
        wireless = "SetKCT",
        tag = "SET_KCT"
    ),

    SET_REC_PARTIAL(
        usb = 27,
        wireless = "SetRecPartial",
        tag = "SET_REC_PARTIAL"
    ),

    GET_BATTERY_INFO(
        usb = 28,
        wireless = "GetBatteryInfo",
        tag = "GET_BATTERY_INFO"
    ),

    SET_EXTRA(
        usb = 29,
        wireless = "SetExtra",
        tag = "SET_EXTRA"
    ),

    GET_PHONE_BOOK_PARAM(
        usb = 30,
        wireless = null,
        tag = "GET_PHONE_BOOK_PARAM"
    ),

    GET_MSG_LOG(
        usb = 31,
        wireless = null,
        tag = "GET_MSG_LOG"
    ),

    GET_CALL_LOG(
        usb = 32,
        wireless = null,
        tag = "GET_CALL_LOG"
    ),

    GET_NETWORK_STATE(
        usb = 33,
        wireless = null,
        tag = "GET_NETWORK_STATE"
    ),

    GET_PHONE_INFO(
        usb = 34,
        wireless = null,
        tag = "GET_PHONE_INFO"
    ),

    GET_PROFILE_IMG_URI(
        usb = 35,
        wireless = null,
        tag = "GET_PROFILE_IMG_URI"
    ),

    SET_FILE_RECV_RESULT(
        usb = 36,
        wireless = null,
        tag = "SET_FILE_RECV_RESULT"
    ),

    PLAY_REC_FILE(
        usb = 37,
        wireless = null,
        tag = "PLAY_REC_FILE"
    ),

    GET_CALL_MEMO(
        usb = 38,
        wireless = null,
        tag = "GET_CALL_MEMO"
    ),

    SET_CALL_MEMO(
        usb = 39,
        wireless = null,
        tag = "SET_CALL_MEMO"
    ),

    START_TO_SEND_DATA(
        usb = 40,
        wireless = null,
        tag = "START_TO_SEND_DATA"
    ),

    SEND_MESSAGE(
        usb = 41,
        wireless = "SendMessageExt",
        tag = "SEND_MESSAGE"
    ),

    GET_ABSOLUTE_PATH(
        usb = 42,
        wireless = null,
        tag = "GET_ABSOLUTE_PATH"
    ),

    SET_RECORD_PAUSE(
        usb = 43,
        wireless = "PauseRecording",
        tag = "SET_RECORD_PAUSE"
    ),

    SET_RECORD_RESUME(
        usb = 44,
        wireless = "ResumeRecording",
        tag = "SET_RECORD_RESUME"
    ),

    SET_BACKUP(
        usb = 45,
        wireless = null,
        tag = "SET_BACKUP"
    ),

    SEND_DEVICE_INFO(
        usb = 46,
        wireless = null,
        tag = "SEND_DEVICE_INFO"
    ),

    SET_HOOK_MODE(
        usb = -1,
        wireless = "SetHookMode",
        tag = "SET_HOOK_MODE"
    ),

    GET_MAX_VOLUME(
        usb = -2,
        wireless = "GetMaxVolume",
        tag = "GET_MAX_VOLUME"
    ),

    GET_RECORD_FILE_NAME(
        usb = -3,
        wireless = "GetRecordFileName",
        tag = "GET_RECORD_FILE_NAME"
    ),

    KEEP_CONNECTION(
        usb = -4,
        wireless = "KeepConnection",
        tag = "KEEP_CONNECTION"
    ),

    HANGUP_CALL(
        usb = 47,
        wireless = null,
        tag = "HANGUP_CALL"
    ),

    ANSWER_CALL(
        usb = -6,
        wireless = null,
        tag = "ANSWER_CALL"
    ),

    REJECT_CALL(
        usb = -7,
        wireless = null,
        tag = "REJECT_CALL"
    ),


    START_PARTIAL_RECORDING(
        usb = -8,
        wireless = null,
        tag = "START_PARTIAL_RECORDING"
    ),

    STOP_PARTIAL_RECORDING(
        usb = -9,
        wireless = null,
        tag = "STOP_PARTIAL_RECORDING"
    ),

    GET_BATTERY_STATE(
        usb = -10,
        wireless = "GetBatteryState",
        tag = "GET_BATTERY_STATE"
    ),

    SET_DND(
        usb = -11,
        wireless = "SetDnd",
        tag = "SET_DND"
    )
}

/**
 * IR-USB(Int) to OcxMethod.
 */
fun Int?.toIrUsbMethod(): OcxCommand? = when (this) {
    OcxMethods.CHECK_CONNECTION.usb -> OcxMethods.CHECK_CONNECTION
    OcxMethods.DIAL.usb -> OcxMethods.DIAL
    OcxMethods.MUTE.usb -> OcxMethods.MUTE
    OcxMethods.USER_INPUT.usb -> OcxMethods.USER_INPUT
    OcxMethods.SET_RECORD.usb -> OcxMethods.SET_RECORD
    OcxMethods.SET_RECORD_FILE_NAME.usb -> OcxMethods.SET_RECORD_FILE_NAME
    OcxMethods.SET_UPLOAD_FILE.usb -> OcxMethods.SET_UPLOAD_FILE
    OcxMethods.GET_CALL_STATE.usb -> OcxMethods.GET_CALL_STATE
    OcxMethods.SET_VOLUME.usb -> OcxMethods.SET_VOLUME
    OcxMethods.GET_VOLUME.usb -> OcxMethods.GET_VOLUME
    OcxMethods.SET_RECORD_FOLDER.usb -> OcxMethods.SET_RECORD_FOLDER
    OcxMethods.GET_RECORD_FOLDER.usb -> OcxMethods.GET_RECORD_FOLDER
    OcxMethods.CUT_CURRENT_RECORD.usb -> OcxMethods.CUT_CURRENT_RECORD
    OcxMethods.GET_CERT_STATE.usb -> OcxMethods.GET_CERT_STATE
    OcxMethods.DO_LOG_CERT.usb -> OcxMethods.DO_LOG_CERT
    OcxMethods.LISTEN_FILE.usb -> OcxMethods.LISTEN_FILE
    OcxMethods.GET_PHONE_BOOK.usb -> OcxMethods.GET_PHONE_BOOK
    OcxMethods.SEND_SMS.usb -> OcxMethods.SEND_SMS
    OcxMethods.SET_ENC.usb -> OcxMethods.SET_ENC
    OcxMethods.GET_SMS_COUNT.usb -> OcxMethods.GET_SMS_COUNT
    OcxMethods.GET_FILES.usb -> OcxMethods.GET_FILES
    OcxMethods.CLOSE_DEVICE.usb -> OcxMethods.CLOSE_DEVICE
    OcxMethods.CREATE_DEVICE.usb -> OcxMethods.CREATE_DEVICE
    OcxMethods.SET_SAVE_PATH.usb -> OcxMethods.SET_SAVE_PATH
    OcxMethods.GET_SAVE_PATH.usb -> OcxMethods.GET_SAVE_PATH
    OcxMethods.SET_KCT.usb -> OcxMethods.SET_KCT
    OcxMethods.SET_REC_PARTIAL.usb -> OcxMethods.SET_REC_PARTIAL
    OcxMethods.GET_BATTERY_INFO.usb -> OcxMethods.GET_BATTERY_INFO
    OcxMethods.SET_EXTRA.usb -> OcxMethods.SET_EXTRA
    OcxMethods.GET_PHONE_BOOK_PARAM.usb -> OcxMethods.GET_PHONE_BOOK_PARAM
    OcxMethods.GET_MSG_LOG.usb -> OcxMethods.GET_MSG_LOG
    OcxMethods.GET_CALL_LOG.usb -> OcxMethods.GET_CALL_LOG
    OcxMethods.GET_NETWORK_STATE.usb -> OcxMethods.GET_NETWORK_STATE
    OcxMethods.GET_PHONE_INFO.usb -> OcxMethods.GET_PHONE_INFO
    OcxMethods.GET_PROFILE_IMG_URI.usb -> OcxMethods.GET_PROFILE_IMG_URI
    OcxMethods.SET_FILE_RECV_RESULT.usb -> OcxMethods.SET_FILE_RECV_RESULT
    OcxMethods.PLAY_REC_FILE.usb -> OcxMethods.PLAY_REC_FILE
    OcxMethods.GET_CALL_MEMO.usb -> OcxMethods.GET_CALL_MEMO
    OcxMethods.SET_CALL_MEMO.usb -> OcxMethods.SET_CALL_MEMO
    OcxMethods.START_TO_SEND_DATA.usb -> OcxMethods.START_TO_SEND_DATA
    OcxMethods.SEND_MESSAGE.usb -> OcxMethods.SEND_MESSAGE
    OcxMethods.GET_ABSOLUTE_PATH.usb -> OcxMethods.GET_ABSOLUTE_PATH
    OcxMethods.SET_RECORD_PAUSE.usb -> OcxMethods.SET_RECORD_PAUSE
    OcxMethods.SET_RECORD_RESUME.usb -> OcxMethods.SET_RECORD_RESUME
    OcxMethods.SET_BACKUP.usb -> OcxMethods.SET_BACKUP
    OcxMethods.SEND_DEVICE_INFO.usb -> OcxMethods.SEND_DEVICE_INFO
    OcxMethods.SET_HOOK_MODE.usb -> OcxMethods.SET_HOOK_MODE
    OcxMethods.GET_MAX_VOLUME.usb -> OcxMethods.GET_MAX_VOLUME
    OcxMethods.GET_RECORD_FILE_NAME.usb -> OcxMethods.GET_RECORD_FILE_NAME
    OcxMethods.KEEP_CONNECTION.usb -> OcxMethods.KEEP_CONNECTION
    OcxMethods.HANGUP_CALL.usb -> OcxMethods.HANGUP_CALL
    OcxMethods.ANSWER_CALL.usb -> OcxMethods.ANSWER_CALL
    OcxMethods.REJECT_CALL.usb -> OcxMethods.REJECT_CALL
    OcxMethods.START_PARTIAL_RECORDING.usb -> OcxMethods.START_PARTIAL_RECORDING
    OcxMethods.STOP_PARTIAL_RECORDING.usb -> OcxMethods.STOP_PARTIAL_RECORDING
    OcxMethods.GET_BATTERY_STATE.usb -> OcxMethods.GET_BATTERY_STATE
    OcxMethods.SET_DND.usb -> OcxMethods.SET_DND
    else -> null
}

/**
 * IR-WIRELESS(String) to OcxMethod.
 */
fun String?.toIrWirelessMethod(): OcxCommand? = when (this) {
    OcxMethods.CHECK_CONNECTION.wireless -> OcxMethods.CHECK_CONNECTION
    OcxMethods.DIAL.wireless -> OcxMethods.DIAL
    OcxMethods.MUTE.wireless -> OcxMethods.MUTE
    OcxMethods.USER_INPUT.wireless -> OcxMethods.USER_INPUT
    OcxMethods.SET_RECORD.wireless -> OcxMethods.SET_RECORD
    OcxMethods.SET_RECORD_FILE_NAME.wireless -> OcxMethods.SET_RECORD_FILE_NAME
    OcxMethods.SET_UPLOAD_FILE.wireless -> OcxMethods.SET_UPLOAD_FILE
    OcxMethods.GET_CALL_STATE.wireless -> OcxMethods.GET_CALL_STATE
    OcxMethods.SET_VOLUME.wireless -> OcxMethods.SET_VOLUME
    OcxMethods.GET_VOLUME.wireless -> OcxMethods.GET_VOLUME
    OcxMethods.SET_RECORD_FOLDER.wireless -> OcxMethods.SET_RECORD_FOLDER
    OcxMethods.GET_RECORD_FOLDER.wireless -> OcxMethods.GET_RECORD_FOLDER
    OcxMethods.CUT_CURRENT_RECORD.wireless -> OcxMethods.CUT_CURRENT_RECORD
    OcxMethods.GET_CERT_STATE.wireless -> OcxMethods.GET_CERT_STATE
    OcxMethods.DO_LOG_CERT.wireless -> OcxMethods.DO_LOG_CERT
    OcxMethods.LISTEN_FILE.wireless -> OcxMethods.LISTEN_FILE
    OcxMethods.GET_PHONE_BOOK.wireless -> OcxMethods.GET_PHONE_BOOK
    OcxMethods.SEND_SMS.wireless -> OcxMethods.SEND_SMS
    OcxMethods.SET_ENC.wireless -> OcxMethods.SET_ENC
    OcxMethods.GET_SMS_COUNT.wireless -> OcxMethods.GET_SMS_COUNT
    OcxMethods.GET_FILES.wireless -> OcxMethods.GET_FILES
    OcxMethods.CLOSE_DEVICE.wireless -> OcxMethods.CLOSE_DEVICE
    OcxMethods.CREATE_DEVICE.wireless -> OcxMethods.CREATE_DEVICE
    OcxMethods.SET_SAVE_PATH.wireless -> OcxMethods.SET_SAVE_PATH
    OcxMethods.GET_SAVE_PATH.wireless -> OcxMethods.GET_SAVE_PATH
    OcxMethods.SET_KCT.wireless -> OcxMethods.SET_KCT
    OcxMethods.SET_REC_PARTIAL.wireless -> OcxMethods.SET_REC_PARTIAL
    OcxMethods.GET_BATTERY_INFO.wireless -> OcxMethods.GET_BATTERY_INFO
    OcxMethods.SET_EXTRA.wireless -> OcxMethods.SET_EXTRA
    OcxMethods.GET_PHONE_BOOK_PARAM.wireless -> OcxMethods.GET_PHONE_BOOK_PARAM
    OcxMethods.GET_MSG_LOG.wireless -> OcxMethods.GET_MSG_LOG
    OcxMethods.GET_CALL_LOG.wireless -> OcxMethods.GET_CALL_LOG
    OcxMethods.GET_NETWORK_STATE.wireless -> OcxMethods.GET_NETWORK_STATE
    OcxMethods.GET_PHONE_INFO.wireless -> OcxMethods.GET_PHONE_INFO
    OcxMethods.GET_PROFILE_IMG_URI.wireless -> OcxMethods.GET_PROFILE_IMG_URI
    OcxMethods.SET_FILE_RECV_RESULT.wireless -> OcxMethods.SET_FILE_RECV_RESULT
    OcxMethods.PLAY_REC_FILE.wireless -> OcxMethods.PLAY_REC_FILE
    OcxMethods.GET_CALL_MEMO.wireless -> OcxMethods.GET_CALL_MEMO
    OcxMethods.SET_CALL_MEMO.wireless -> OcxMethods.SET_CALL_MEMO
    OcxMethods.START_TO_SEND_DATA.wireless -> OcxMethods.START_TO_SEND_DATA
    OcxMethods.SEND_MESSAGE.wireless -> OcxMethods.SEND_MESSAGE
    OcxMethods.GET_ABSOLUTE_PATH.wireless -> OcxMethods.GET_ABSOLUTE_PATH
    OcxMethods.SET_RECORD_PAUSE.wireless -> OcxMethods.SET_RECORD_PAUSE
    OcxMethods.SET_RECORD_RESUME.wireless -> OcxMethods.SET_RECORD_RESUME
    OcxMethods.SET_BACKUP.wireless -> OcxMethods.SET_BACKUP
    OcxMethods.SEND_DEVICE_INFO.wireless -> OcxMethods.SEND_DEVICE_INFO
    OcxMethods.SET_HOOK_MODE.wireless -> OcxMethods.SET_HOOK_MODE
    OcxMethods.GET_MAX_VOLUME.wireless -> OcxMethods.GET_MAX_VOLUME
    OcxMethods.GET_RECORD_FILE_NAME.wireless -> OcxMethods.GET_RECORD_FILE_NAME
    OcxMethods.KEEP_CONNECTION.wireless -> OcxMethods.KEEP_CONNECTION
    OcxMethods.HANGUP_CALL.wireless -> OcxMethods.HANGUP_CALL
    OcxMethods.ANSWER_CALL.wireless -> OcxMethods.ANSWER_CALL
    OcxMethods.REJECT_CALL.wireless -> OcxMethods.REJECT_CALL
    OcxMethods.START_PARTIAL_RECORDING.wireless -> OcxMethods.START_PARTIAL_RECORDING
    OcxMethods.STOP_PARTIAL_RECORDING.wireless -> OcxMethods.STOP_PARTIAL_RECORDING
    OcxMethods.GET_BATTERY_STATE.wireless -> OcxMethods.GET_BATTERY_STATE
    OcxMethods.SET_DND.wireless -> OcxMethods.SET_DND
    else -> null
}