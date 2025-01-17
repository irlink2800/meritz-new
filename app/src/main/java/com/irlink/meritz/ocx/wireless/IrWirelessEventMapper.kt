package com.irlink.meritz.ocx.wireless

import com.irlink.meritz.ocx.*
import com.irlink.meritz.ocx.mapper.OcxEventMapper
import com.irlink.meritz.util.LogUtil
import org.json.JSONObject

class IrWirelessEventMapper : OcxEventMapper() {

    companion object {
        const val TAG: String = "IrWirelessEventMapper"
    }

    var key: String? = null

    override val ignoreEvents: Array<OcxEvents> = arrayOf(
        OcxEvents.DEV_HOOK,
        OcxEvents.DEV_CID_DATA,
        OcxEvents.DEV_OUTGOING,
        OcxEvents.DEV_BELL
    )

    override fun map(params: OcxParams): JSONObject? {
        if (ignoreEvents.contains(params.command)) {
            return null
        }
        if (key.isNullOrEmpty()) {
            LogUtil.e(TAG, "sendData. key is null or empty.")
            return null
        }
        val message = createMessage(params)
        if (message == null) {
            LogUtil.e(TAG, "sendData. message is null or empty.")
            return null
        }
        return JSONObject().apply {
            put(IrWirelessMapKey.KEY, key)
            put(IrWirelessMapKey.MESSAGE, message.toString())

        }.also {
            printData(it)
        }
    }

    private fun createMessage(params: OcxParams): JSONObject? = when (params.command) {
        OcxEvents.DEV_CREATE_DEVICE -> mapCreateDevice(
            connFlag = params.first
        )
        OcxEvents.DEV_HOOK -> mapDevHook(
            hookFlag = params.first
        )
        OcxEvents.DEV_BELL -> mapDevBell(
            bell = params.first
        )
        OcxEvents.DEV_CID_DATA -> {
            params.command = OcxEvents.DEV_STATE
            mapDevCidData(
                callInfo = params.first
            )
        }
        OcxEvents.DEV_VOLUME -> mapDevVolume(
            volume = params.first
        )
        OcxEvents.DEV_MAX_VOLUME -> mapDevMaxVolume(
            maxVolume = params.first
        )
        OcxEvents.DEV_OUTGOING -> {
            params.command = OcxEvents.DEV_STATE
            mapDevOutGoing(
                callInfo = params.first
            )
        }
        OcxEvents.DEV_REC_START_END -> {
            when (params.first) {
                RecordState.PAUSE -> params.command = OcxEvents.DEV_PAUSE_RECORDING
                RecordState.RESUME -> params.command = OcxEvents.DEV_RESUME_RECORDING
            }
            mapDevRecStartEnd(
                recordState = params.first,
                fileName = params.second,
                callInfo = params.third
            )
        }
        OcxEvents.DEV_RECORD_FOLDER -> mapDevRecordFolder(
            folderName = params.first
        )
        OcxEvents.DEV_UPLOADED -> mapDevUploaded(
            result = params.first,
            localFileName = params.second,
            serverFileName = params.third
        )
        OcxEvents.DEV_CALL_STATE -> mapDevCallState(
            isCallActive = params.first
        )
        OcxEvents.DEV_GET_FILES -> mapDevFileList(
            filesInfo = params.first
        )
        OcxEvents.DEV_SMS -> mapDevSms(
            result = params.first
        )
        OcxEvents.DEV_CALL_START -> {
            params.command = OcxEvents.DEV_STATE
            mapDevCallStart(
                callState = params.first,
                callInfo = params.second
            )
        }
        OcxEvents.DEV_CALL_CONNECT -> {
            params.command = OcxEvents.DEV_STATE
            mapDevCallConnect(
                callInfo = params.first
            )
        }
        OcxEvents.DEV_CALL_END -> {
            params.command = OcxEvents.DEV_STATE
            mapDevCallEnd(
                callInfo = params.first
            )
        }
        OcxEvents.DEV_SAVE_PATH -> mapDevSavePath(
            saveState = params.first,
            uploadUrl = params.second,
            uploadPath = params.third,
            isSort = params.fourth
        )
        OcxEvents.DEV_RECORD_FILE_NAME -> mapDevRecordFileName(
            isSuccess = params.first,
            fileName = params.second
        )
        OcxEvents.DEV_REC_PARTIAL -> mapDevRecPartial(
            isStart = params.first,
            fileName = params.second,
            isSuccess = params.third
        )
        OcxEvents.DEV_BATTERY_INFO -> mapDevBatteryInfo(
            batteryLevel = params.first
        )
        OcxEvents.DEV_PHONE_BOOK -> mapDevPhoneBook(
            contacts = params.first
        )
        OcxEvents.DEV_SMS_COUNT -> mapDevSmsCount(
            smsCountInfo = params.first
        )
        OcxEvents.DEV_KEEP_CONNECTION -> mapDevKeepConnection()

        OcxEvents.DEV_CUT_RECORD -> mapDevCutRecord(
            fileName = params.first
        )
        OcxEvents.DEV_ERROR -> mapDevError(
            errorCode = params.first
        )
        OcxEvents.DEV_BATTERY_STATE -> mapDevBatteryState(
            state = params.first
        )
        OcxEvents.DEV_MIC_MUTE -> mapDevMicMute(
            isMiceMute = params.first
        )
        OcxEvents.DEV_DND -> mapDevDnd(
            code = params.first
        )
        else -> null

    }?.apply {
        put(IrWirelessMapKey.TYPE, params.command?.wireless)
    }

    private fun mapCreateDevice(connFlag: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.N_CONN_FLAG, connFlag?.toInt())
    }

    private fun mapDevHook(hookFlag: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.N_HOOK_FLAG, hookFlag?.toInt())
    }

    private fun mapDevBell(bell: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.N_BELL, bell)
    }

    private fun mapDevCidData(callInfo: String?): JSONObject = JSONObject().apply {
        val toJson = callInfo?.let { JSONObject(it) }
        val number = toJson?.optString(IrWirelessMapKey.CUS_TEL_NO)

        put(IrWirelessMapKey.STATE, DevCallState.INBOUND.toInt())
        put(IrWirelessMapKey.NUMBER, number)
        put(IrWirelessMapKey.FILE_NAME, callInfo)
    }

    private fun mapDevOutGoing(callInfo: String?): JSONObject = JSONObject().apply {
        val toJson = callInfo?.let { JSONObject(it) }
        val number = toJson?.optString(IrWirelessMapKey.CUS_TEL_NO)

        put(IrWirelessMapKey.STATE, DevCallState.OUTBOUND.toInt())
        put(IrWirelessMapKey.NUMBER, number)
        put(IrWirelessMapKey.FILE_NAME, callInfo)
    }

    private fun mapDevVolume(volume: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.VOLUME, volume?.toInt())
    }

    private fun mapDevMaxVolume(maxVolume: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.VOLUME, maxVolume?.toInt())
    }

    private fun mapDevRecStartEnd(
        recordState: String?,
        fileName: String?,
        callInfo: String?
    ): JSONObject = JSONObject().apply {
        when (recordState) {
            RecordState.START, RecordState.END -> put(IrWirelessMapKey.N_FLAG, recordState)
        }
        put(IrWirelessMapKey.SZ_FILE_NAME, fileName)
        put(IrWirelessMapKey.SZ_CALL_INFO, callInfo)
    }

    private fun mapDevRecordFolder(folderName: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.PATH, folderName)
    }

    private fun mapDevUploaded(
        result: String?,
        localFileName: String?,
        serverFileName: String?
    ): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.N_RESULT, result?.toInt())
        put(IrWirelessMapKey.LOCAL_FILE_NAME, localFileName)
        put(IrWirelessMapKey.SERVER_FILE_NAME, serverFileName)
    }

    private fun mapDevCallState(isCallActive: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.N_CALL_FLAG, isCallActive)
    }

    private fun mapDevFileList(filesInfo: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.SZ_FILE_LIST, filesInfo)
    }

    private fun mapDevSms(result: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.SZ_STATE, result)
    }

    private fun mapDevCallStart(callState: String?, callInfo: String?): JSONObject =
        JSONObject().apply {
            val toJson = callInfo?.let { JSONObject(it) }
            val number = toJson?.optString(IrWirelessMapKey.CUS_TEL_NO)

            put(IrWirelessMapKey.STATE, callState?.toInt())
            put(IrWirelessMapKey.NUMBER, number)
            put(IrWirelessMapKey.FILE_NAME, callInfo)
        }

    private fun mapDevCallConnect(callInfo: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.STATE, DevCallState.CONNECTED.toInt())
        put(IrWirelessMapKey.FILE_NAME, callInfo)
    }

    private fun mapDevCallEnd(callInfo: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.STATE, DevCallState.IDLE.toInt())
        put(IrWirelessMapKey.FILE_NAME, callInfo)
    }

    private fun mapDevSavePath(
        saveState: String?,
        uploadUrl: String?,
        uploadPath: String?,
        isSort: String?
    ): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.N_MODE, saveState)
        put(IrWirelessMapKey.SZ_URL, uploadUrl)
        put(IrWirelessMapKey.SZ_PATH, uploadPath)
        put(IrWirelessMapKey.N_SORT, isSort)
    }

    private fun mapDevRecordFileName(isSuccess: String?, fileName: String?): JSONObject =
        JSONObject().apply {
            put(IrWirelessMapKey.N_RESULT, isSuccess?.toInt())
            put(IrWirelessMapKey.SZ_FILE_NAME, fileName)
        }

    private fun mapDevRecPartial(
        isStart: String?,
        fileName: String?,
        isSuccess: String?
    ): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.N_START_END, isStart)
        put(IrWirelessMapKey.SZ_FILE_NAME, fileName)
        put(IrWirelessMapKey.N_RESULT, isSuccess)
    }

    private fun mapDevBatteryInfo(batteryLevel: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.LEVEL, batteryLevel?.toInt())
    }

    private fun mapDevPhoneBook(contacts: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.SZ_PB_LIST, contacts)
    }

    private fun mapDevSmsCount(smsCountInfo: String?): JSONObject = JSONObject().apply {
        put(
            IrWirelessMapKey.SZ_CNT,
            (smsCountInfo?.split(OcxParams.SUB_SEPARATOR)?.getOrNull(1) ?: "0").toInt()
        )
    }

    private fun mapDevKeepConnection(): JSONObject = JSONObject().apply {
        // 별도의 파라미터 추가 없음.
    }

    private fun mapDevCutRecord(fileName: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.FILE_NAME, fileName)
    }

    private fun mapDevError(errorCode: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.CODE, errorCode?.toIntOrNull())
    }

    private fun mapDevBatteryState(state: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.STATE, (state ?: BatteryState.DISCHARGED).toInt())
    }

    private fun mapDevMicMute(isMiceMute: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.STATE, isMiceMute?.toIntOrNull())
    }

    private fun mapDevDnd(code: String?): JSONObject = JSONObject().apply {
        put(IrWirelessMapKey.CODE, code?.toIntOrNull())
    }

    override fun printData(data: JSONObject) {
        try {
            val message = data.optString(IrWirelessMapKey.MESSAGE)
            if (message.isNullOrEmpty()) {
                return
            }
            val log = JSONObject().apply {
                put(IrWirelessMapKey.KEY, data.optString(IrWirelessMapKey.KEY))
                put(IrWirelessMapKey.MESSAGE, JSONObject(message))
            }
            LogUtil.d(TAG, log.toString(4))

        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
        }
    }

}