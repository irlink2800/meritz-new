package com.irlink.meritz.ocx

import com.irlink.meritz.util.DeviceUtil
import org.koin.core.KoinComponent

abstract class OcxErrorHandler(

    private val deviceUtil: DeviceUtil

) : KoinComponent {

    companion object {
        const val TAG: String = "OcxErrorHandler"
    }

    /**
     * 에러 대상 제외 디바이스 (스마트폰 기종).
     */
    abstract val ignoreDevices: Array<String>

    /**
     * 에러 대상 식별 번호 (폰번호).
     */
    abstract val acceptNumbers: Array<String>

    /**
     * 실행 기기.
     */
    private val targetDevice: String
        get() = deviceUtil.model

//    /**
//     * 에러 발생 시 호출.
//     */
//    open fun onRecordError(recordError: RecordError): Boolean {
//        LogUtil.d(TAG, "onError. code: ${recordError.code}, message: ${recordError.message}")
//        return when (recordError.code) {
//            OcxErrorType.RECORDING_ERROR -> onRecordingError()
//            OcxErrorType.RECORDING_FILE_NOT_CREATE -> onRecordingFileNotCreatedError(recordError.record)
//            else -> false
//        }
//    }

//    /**
//     * 녹취 중 에러 발생 시 호출.
//     */
//    abstract fun onRecordingError(): Boolean
//
//    /**
//     * 녹취 파일 미생성 시 호출.
//     */
//    abstract fun onRecordingFileNotCreatedError(record: Record?): Boolean
//
//    /**
//     * 녹취 에러를 사용할지에 대한 여부.
//     */
//    protected open fun isNotifyRecordError(currentRecord: Record?): Boolean {
//        // 레코드 객체가 있는지 체크.
//        if (currentRecord == null) {
//            return false
//        }
//        val callStateListener = get<CallStateListener>()
//
//        // 녹취 파일이 생겼다 삭제된경우.
//        if (!currentRecord.isMissedRecord) {
//            wait(5_000) {
//                callStateListener.currentCallState == CallState.IDLE
//            }
//        }
//        // 통화가 종료됐는지 체크.
//        if (callStateListener.currentCallState == CallState.IDLE) {
//            return false
//        }
//        // 디바이스 체크.
//        if (!isIgnoreDevice()) {
//            return true
//        }
//        // 식별번호 체크.
//        if (!isAcceptNumber(currentRecord.remoteNumber)) {
//            return false
//        }
//        return true
//    }

    /**
     * 에러를 무시 할 디바이스 검사.
     */
    protected open fun isIgnoreDevice(): Boolean {
        for (device: String in ignoreDevices) {
            if (targetDevice == device) {
                return true
            }
        }
        return false
    }

    /**
     * 식별 번호 검사.
     */
    protected open fun isAcceptNumber(remoteNumber: String): Boolean {
        if (remoteNumber.isEmpty()) {
            return false
        }
        for (areaCode: String in acceptNumbers) {
            if (remoteNumber.startsWith(areaCode)) {
                return true
            }
        }
        return false
    }

}
