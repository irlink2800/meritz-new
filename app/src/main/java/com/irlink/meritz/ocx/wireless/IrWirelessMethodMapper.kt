package com.irlink.meritz.ocx.wireless

import com.irlink.meritz.ocx.*
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.ocx.mapper.OcxMethodMapper
import org.json.JSONArray
import org.json.JSONObject

class IrWirelessMethodMapper : OcxMethodMapper() {

    override val ignoreMethods: Array<OcxMethods> = arrayOf(
        OcxMethods.SET_RECORD_PAUSE,
        OcxMethods.SET_RECORD_RESUME,
    )

    override fun map(params: JSONObject): String? = params.run {
        val command: OcxCommand =
            optString(IrWirelessMapKey.TYPE).toIrWirelessMethod() ?: return@run null
        if (ignoreMethods.contains(command)) {
            return@run null
        }
        return@run when (command) {
            OcxMethods.CREATE_DEVICE -> mapCreateDevice(
                code = optString(IrWirelessMapKey.CODE)
            )
            OcxMethods.CLOSE_DEVICE -> mapCloseDevice()
            OcxMethods.SET_HOOK_MODE -> mapSetHookMode(
                mode = optString(IrWirelessMapKey.N_MODE)
            )
            OcxMethods.DIAL -> mapSetDialStr(
                phoneNumber = optString(IrWirelessMapKey.NUMBER)
            )
            OcxMethods.SET_VOLUME -> mapSetVolume(
                volume = optString(IrWirelessMapKey.VOLUME)
            )
            OcxMethods.GET_VOLUME -> mapGetVolume()
            OcxMethods.GET_MAX_VOLUME -> mapGetMaxVolume()
            OcxMethods.MUTE -> mapSetMicMute(
                isMute = optString(IrWirelessMapKey.N_MODE)
            )
            OcxMethods.USER_INPUT -> mapSetUserInput(
                isInputBlock = optString(IrWirelessMapKey.N_MODE)
            )
            OcxMethods.SET_RECORD -> mapSetRecord(
                isRecord = optString(IrWirelessMapKey.N_MODE)
            )
            OcxMethods.SET_RECORD_FOLDER -> mapSetRecordFolder(
                folderName = optString(IrWirelessMapKey.PATH)
            )
            OcxMethods.GET_RECORD_FOLDER -> mapGetRecordFolder()

            OcxMethods.SET_RECORD_FILE_NAME -> mapSetRecordFileName(
                recordFileName = optString(IrWirelessMapKey.FILE_NAME)
            )
            OcxMethods.SET_UPLOAD_FILE -> mapSetUploadFile(
                localFileName = optString(IrWirelessMapKey.LOCAL_FILE_NAME),
                remoteFileName = optString(IrWirelessMapKey.SERVER_FILE_NAME),
                remoteHost = optString(IrWirelessMapKey.SERVER_URL),
                remotePath = optString(IrWirelessMapKey.FILE_SAVE_PATH)
            )
            OcxMethods.GET_CALL_STATE -> mapGetCallState()
            OcxMethods.CUT_CURRENT_RECORD -> mapCutCurrentRecord(
                fileNameSuffix = optString(IrWirelessMapKey.POSTFIX)
            )
            OcxMethods.GET_FILES -> mapGetFileList()
            OcxMethods.LISTEN_FILE -> mapListenFile(
                fileUrl = optString(IrWirelessMapKey.FILE_NAME)
            )
            OcxMethods.GET_PHONE_BOOK -> mapGetPhoneBook()
            OcxMethods.SEND_SMS -> mapSendSMS(
                sendNumber = optString(IrWirelessMapKey.SZ_SEND_NUMBER),
                receiveNumber = optString(IrWirelessMapKey.SZ_RECV_NUMBER),
                title = optString(IrWirelessMapKey.SZ_TITLE),
                content = optString(IrWirelessMapKey.SZ_CONTENT)
            )
            OcxMethods.SEND_MESSAGE -> mapSendMessage(
                remoteNumbers = optString(IrWirelessMapKey.REMOTE_NUMBERS),
                content = optString(IrWirelessMapKey.CONTENT),
                parts = optString(IrWirelessMapKey.PARTS),
                externalKey = optString(IrWirelessMapKey.EXTERNAL_KEY)
            )
            OcxMethods.SET_ENC -> mapSetEnc(
                isEnc = optString(IrWirelessMapKey.N_USE)
            )
            OcxMethods.GET_SMS_COUNT -> mapGetSmsCount(
                date = optString(IrWirelessMapKey.SZ_DATE)
            )
            OcxMethods.SET_SAVE_PATH -> mapSetSavePath(
                saveState = optString(IrWirelessMapKey.N_MODE),
                uploadUrl = optString(IrWirelessMapKey.SZ_URL),
                uploadPath = optString(IrWirelessMapKey.SZ_PATH),
                isIncludeDate = optString(IrWirelessMapKey.N_SORT)
            )
            OcxMethods.GET_SAVE_PATH -> mapGetSavePath()
            OcxMethods.SET_KCT -> mapSetKCT(
                title = optString(IrWirelessMapKey.SZ_TITLE),
                content = optString(IrWirelessMapKey.SZ_CONTENT),
                number = optString(IrWirelessMapKey.SZ_RECV_NUMBER),
                size = optString(IrWirelessMapKey.N_LIMIT_FILE_SIZE)
            )
            OcxMethods.SET_REC_PARTIAL -> mapSetRecPartial(
                isStartPartial = optString(IrWirelessMapKey.N_START_END),
                fileName = optString(IrWirelessMapKey.SZ_FILE_NAME)
            )
            OcxMethods.GET_BATTERY_INFO -> mapGetBatteryInfo()
            OcxMethods.SET_EXTRA -> mapSetExtra(
                extra = optString(IrWirelessMapKey.SZ_EXTRA)
            )
            OcxMethods.GET_RECORD_FILE_NAME -> mapGetRecordFileName()
            OcxMethods.KEEP_CONNECTION -> mapKeepConnection(
                code = optString(IrWirelessMapKey.CODE)
            )
            OcxMethods.SET_RECORD_RESUME -> mapRecordResume()
            OcxMethods.SET_RECORD_PAUSE -> mapRecordPause()
            OcxMethods.GET_BATTERY_STATE -> mapGetBatteryState()
            OcxMethods.SET_DND -> mapSetDnd(
                code = optString(IrWirelessMapKey.CODE)
            )
            else -> null

        }?.also { ocxParams ->
            ocxParams.command = command
        }?.data
    }

    private fun mapCreateDevice(code: String): OcxParams = OcxParams(
        first = code
    )

    private fun mapCloseDevice(): OcxParams = OcxParams()

    private fun mapSetHookMode(mode: String): OcxParams = OcxParams(
        first = mode
    )

    private fun mapSetDialStr(phoneNumber: String): OcxParams = OcxParams(
        first = phoneNumber
    )

    private fun mapSetVolume(volume: String): OcxParams = OcxParams(
        first = volume
    )

    private fun mapGetVolume(): OcxParams = OcxParams(
        first = false.toOcxBoolean()
    )

    private fun mapGetMaxVolume(): OcxParams = OcxParams()

    private fun mapSetMicMute(isMute: String): OcxParams = OcxParams(
        first = isMute
    )

    private fun mapSetUserInput(isInputBlock: String): OcxParams = OcxParams(
        first = isInputBlock
    )

    private fun mapSetRecord(isRecord: String): OcxParams = OcxParams(
        first = isRecord
    )

    private fun mapSetRecordFolder(folderName: String): OcxParams = OcxParams(
        first = folderName
    )

    private fun mapGetRecordFolder(): OcxParams = OcxParams()

    private fun mapSetRecordFileName(recordFileName: String): OcxParams = OcxParams(
        first = recordFileName
    )

    private fun mapSetUploadFile(
        localFileName: String,
        remoteFileName: String,
        remoteHost: String,
        remotePath: String
    ): OcxParams = OcxParams(
        first = localFileName,
        second = remoteFileName,
        third = remoteHost,
        fourth = remotePath
    )

    private fun mapGetCallState(): OcxParams = OcxParams()

    private fun mapCutCurrentRecord(fileNameSuffix: String): OcxParams = OcxParams(
        first = fileNameSuffix
    )

    private fun mapGetFileList(): OcxParams = OcxParams()

    private fun mapListenFile(fileUrl: String): OcxParams = OcxParams(
        first = fileUrl
    )

    private fun mapGetPhoneBook(): OcxParams = OcxParams()

    private fun mapSendSMS(
        sendNumber: String,
        receiveNumber: String,
        title: String,
        content: String
    ): OcxParams = OcxParams(
        first = sendNumber,
        second = receiveNumber,
        third = title,
        fourth = content
    )

    private fun mapSendMessage(
        remoteNumbers: String?,
        content: String,
        parts: String?,
        externalKey: String
    ): OcxParams = OcxParams(
        first = remoteNumbers,
        second = content,
        third = parts,
        fourth = externalKey
    )

    private fun mapSetEnc(isEnc: String): OcxParams = OcxParams(
        first = isEnc
    )

    private fun mapGetSmsCount(date: String): OcxParams = OcxParams(
        first = date
    )

    private fun mapSetSavePath(
        saveState: String,
        uploadUrl: String,
        uploadPath: String,
        isIncludeDate: String
    ): OcxParams = OcxParams(
        first = saveState,
        second = uploadUrl,
        third = uploadPath,
        fourth = isIncludeDate
    )

    private fun mapGetSavePath(): OcxParams = OcxParams()

    private fun mapSetKCT(
        title: String,
        content: String,
        number: String,
        size: String
    ): OcxParams = OcxParams(
        first = title,
        second = content,
        third = number,
        fourth = size
    )

    private fun mapSetRecPartial(isStartPartial: String, fileName: String): OcxParams = OcxParams(
        first = isStartPartial,
        second = fileName
    )

    private fun mapGetBatteryInfo(): OcxParams = OcxParams()

    private fun mapSetExtra(extra: String): OcxParams = OcxParams(
        first = extra
    )

    private fun mapGetRecordFileName(): OcxParams = OcxParams()

    private fun mapKeepConnection(code: String): OcxParams = OcxParams(
        first = code
    )

    private fun mapRecordPause(): OcxParams = OcxParams()

    private fun mapGetBatteryState(): OcxParams = OcxParams()

    private fun mapRecordResume(): OcxParams = OcxParams()

    private fun mapSetDnd(code: String): OcxParams = OcxParams(
        first = code
    )

    fun JSONObject.toMap(): Map<String, Any>? = try {
        val map = mutableMapOf<String, Any>()
        val keysItr: Iterator<String> = this.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            var value: Any? = this.get(key)
            when (value) {
                is JSONArray -> value = value.toList()
                is JSONObject -> value = value.toMap()
            }
            value?.let {
                map[key] = value
            }
        }
        map
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        null
    }

    fun JSONArray.toList(): List<Any>? = try {
        val list = mutableListOf<Any>()
        for (i in 0 until this.length()) {
            var value: Any? = this[i]
            when (value) {
                is JSONArray -> value = value.toList()
                is JSONObject -> value = value.toMap()
            }
            value?.let {
                list.add(value)
            }
        }
        list
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        null
    }

}