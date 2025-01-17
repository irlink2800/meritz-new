package com.irlink.meritz.ocx

/**
 * 훅 상태값.
 */
object HookState {
    const val ON: String = "1"
    const val OFF: String = "3"
}

/**
 * 문자 전송 결과값.
 */
object SmsState {
    const val SUCCESS: String = "-1"
    const val FAIL: String = "1"
    const val OFF_WIRELESS: String = "2"
    const val PDU_ERROR: String = "3"
    const val NO_SERVICE_LOCATION: String = "4"
}

/**
 * 녹취 상태값.
 */
object RecordState {
    const val END: String = "0"
    const val START: String = "1"
    const val PAUSE: String = "2"
    const val RESUME: String = "3"
}

/**
 * 녹취 저장 위치 값.
 */
object SaveState {
    const val LOCAL: String = "0"
    const val PC: String = "1"
    const val HTTP: String = "2"
}

/**
 * 업로드 결과값.
 */
object UploadState {
    const val FAIL: String = "0"
    const val SUCCESS: String = "1"
    const val NOT_EXISTS: String = "2"
    const val RECORDING_ERROR: String = "3"
    const val SAVE_PATH_ERROR: String = "4"
}

/**
 * 디바이스 연결 상태값.
 * IR-WIRELESS 전용.
 */
object CreateDeviceState {
    const val DISCONNECTED: String = "0"
    const val CONNECTED: String = "1"
    const val PAIRED: String = "2"
    const val REJECT: String = "-1"
    const val DUPLICATE_LOGIN: String = "-2"
    const val DISCONNECTED_PAIRING: String = "-3"
    const val DUPLICATE_WEB_LOGIN: String = "-4"
}

/**
 * 통화 상태값.
 * IR-WIRELESS 전용.
 */
object DevCallState {
    const val IDLE: String = "0"
    const val INBOUND: String = "1"
    const val OUTBOUND: String = "2"
    const val CONNECTED: String = "3"
}

/**
 * 콜백 상태값.
 * IR-WIRELESS 전용.
 */

/**
 * 배터리 충전 여부.
 */
object BatteryState {
    const val CHARGED: String = "1"
    const val DISCHARGED: String = "0"
}