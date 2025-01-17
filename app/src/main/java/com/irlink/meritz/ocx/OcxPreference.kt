package com.irlink.meritz.ocx

import android.content.Context
import android.content.SharedPreferences
import com.irlink.meritz.ocx.wireless.WirelessPreference
import com.irlink.meritz.util.extension.*

open class OcxPreferenceImpl(

    private val applicationContext: Context,
    private val wirelessPreference: WirelessPreference

) : OcxPreference {

    companion object {
        const val PREF_NAME: String = "ocx_pref"
    }

    object Key {
        const val IS_SACALL: String = "IS_SACALL"
        const val IS_RECORD: String = "IS_RECORD"
        const val IS_ENC: String = "IS_ENC"
        const val IS_CALL_ACTIVE: String = "IS_CALL_ACTIVE"
        const val IS_RECORD_BACKUP: String = "IS_RECORD_BACKUP"
        const val IS_RECORD_INCLUDE_DATE: String = "IS_RECORD_INCLUDE_DATE"
        const val RECORD_FOLDER_NAME: String = "RECORD_FOLDER_NAME"
        const val RECORD_FOLDER_PATH: String = "RECORD_FOLDER_PATH"
        const val SAVE_STATE: String = "SAVE_STATE"
        const val UPLOAD_HOST: String = "UPLOAD_HOST"
        const val UPLOAD_PATH: String = "UPLOAD_PATH"
        const val KCT_TITLE: String = "KCT_TITLE"
        const val KCT_CONTENT: String = "KCT_CONTENT"
        const val KCT_NUMBER: String = "KCT_NUMBER"
        const val KCT_SIZE: String = "KCT_SIZE"
        const val KCT_DAILY_COUNT: String = "KCT_DAILY_COUNT"
        const val KCT_DAILY_LIMIT: String = "KCT_DAILY_LIMIT"
        const val EXTRA: String = "EXTRA"
        const val POOL_NAME: String = "POOL_NAME"
        const val USER_ID: String = "USER_ID"
        const val AGENCY_CODE: String = "AGENCY_CODE"
        const val AGENCY_NAME: String = "AGENCY_NAME"
        const val SMART_DM_ID: String = "SMART_DM_ID"
        const val SMART_DM_NUMBER: String = "SMART_DM_NUMBER"
        const val IS_MISSED_CALL_MESSAGE: String = "IS_MISSED_CALL_MESSAGE"
        const val KEEP_SET_DIAL_STR: String = "KEEP_SET_DIAL_STR"

        // 라이나 추가.
        const val DND_CODE: String = "DND_CODE"
    }

    object Default {
        const val RECORD_FOLDER_NAME: String = "record"

        const val UPLOAD_HOST: String = ""
        const val UPLOAD_PATH: String = ""

        const val AGENCY_NAME: String = ""
        const val SMART_DM_ID: String = ""
    }

    protected val pref: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     *  사콜 여부.
     */
    override var isSaCall: Boolean
        get() = pref.getBoolean(Key.IS_SACALL, true)
        set(value) {
            pref.putBoolean(Key.IS_SACALL, value)
        }

    /**
     *  녹취 사용 여부.
     */
    override var isRecord: Boolean
        get() = pref.getBoolean(Key.IS_RECORD, true)
        set(value) {
            pref.putBoolean(Key.IS_RECORD, value)
        }

    /**
     *  암호화 여부.
     */
    override var isEnc: Boolean
        get() = pref.getBoolean(Key.IS_ENC, true)
        set(value) {
            pref.putBoolean(Key.IS_ENC, value)
        }

    /**
     *  통화 진행 여부.
     */
    override var isCallActive: Boolean
        get() = pref.getBoolean(Key.IS_CALL_ACTIVE, false)
        set(value) {
            pref.putBoolean(Key.IS_CALL_ACTIVE, value)
        }

    /**
     *  녹취 파일 로컬 저장 폴더명.
     */
    override var recordFolderName: String
        get() = pref.getString(Key.RECORD_FOLDER_NAME, Default.RECORD_FOLDER_NAME).toString()
        set(value) {
            pref.putString(Key.RECORD_FOLDER_NAME, value)
        }

    /**
     *  녹취 파일 로컬 저장 폴더 경로.
     */
    override var recordFolderPath: String
        get() = pref.getString(Key.RECORD_FOLDER_PATH, "").toString()
        set(value) {
            pref.putString(Key.RECORD_FOLDER_PATH, value)
        }


    /**
     *  녹취 파일 로컬 저장 경로.
     */
    override var saveState: Int
        get() = pref.getInt(Key.SAVE_STATE, SaveState.HTTP.toInt())
        set(value) {
            pref.putInt(Key.SAVE_STATE, value)
        }

    /**
     *  녹취 파일 업로드 서버 도메인.
     */
    override var uploadHost: String
        get() {
            var uploadHost = pref.getString(Key.UPLOAD_HOST, Default.UPLOAD_HOST)
            if (uploadHost.isNullOrEmpty()) {
                uploadHost = "${wirelessPreference.serverUrl}httpup/RefHome.jsp"
            }
            return uploadHost
        }
        set(value) {
            pref.putString(Key.UPLOAD_HOST, value)
        }

    /**
     *  녹취 파일 업로드 경로.
     */
    override var uploadPath: String
        get() = pref.getString(Key.UPLOAD_PATH, Default.UPLOAD_PATH).toString()
        set(value) {
            pref.putString(Key.UPLOAD_PATH, value)
        }

    /**
     *  녹취 파일 생성일 추가 여부.
     */
    override var isRecordIncludeDate: Boolean
        get() = pref.getBoolean(Key.IS_RECORD_INCLUDE_DATE, true)
        set(value) {
            pref.putBoolean(Key.IS_RECORD_INCLUDE_DATE, value)
        }

    /**
     *  녹취 파일 백업 여부.
     */
    override var isRecordBackup: Boolean
        get() = pref.getBoolean(Key.IS_RECORD_BACKUP, true)
        set(value) {
            pref.putBoolean(Key.IS_RECORD_BACKUP, value)
        }

    /**
     *  지능형 SMS 제목.
     */
    override var kctTitle: String
        get() = pref.getString(Key.KCT_TITLE, "").toString()
        set(value) {
            pref.putString(Key.KCT_TITLE, value)
        }

    /**
     *  지능형 SMS 내용.
     */
    override var kctContent: String
        get() = pref.getString(Key.KCT_CONTENT, "").toString()
        set(value) {
            pref.putString(Key.KCT_CONTENT, value)
        }

    /**
     *  지능형 SMS 전화번호.
     */
    override var kctNumber: String
        get() = pref.getString(Key.KCT_NUMBER, "").toString()
        set(value) {
            pref.putString(Key.KCT_NUMBER, value)
        }

    /**
     *  지능형 SMS 내용 크기.
     */
    override var kctSize: Long
        get() = pref.getLong(Key.KCT_SIZE, 0)
        set(value) {
            pref.putLong(Key.KCT_SIZE, value)
        }

    /**
     *  지능형 SMS 당일 건수.
     */
    override var kctDailyCount: Int
        get() = pref.getInt(Key.KCT_DAILY_COUNT, 0)
        set(value) {
            pref.putInt(Key.KCT_DAILY_COUNT, value)
        }

    /**
     *  지능형 SMS 당일 전송 가능한 최대 건수.
     */
    override var kctDailyLimit: Int
        get() = pref.getInt(Key.KCT_DAILY_LIMIT, 0)
        set(value) {
            pref.putInt(Key.KCT_DAILY_LIMIT, value)
        }

    /**
     *  EXTRA.
     */
    override var extra: String
        get() = pref.getString(Key.EXTRA, "").toString()
        set(value) {
            pref.putString(Key.EXTRA, value)
        }

    /**
     * 풀 네임.
     */
    override var poolName: String
        get() = pref.getString(Key.POOL_NAME, "").toString()
        set(value) {
            pref.putString(Key.POOL_NAME, value)
        }

    /**
     * 유저 아이디.
     */
    override var userId: String
        get() = pref.getString(Key.USER_ID, "").toString()
        set(value) {
            pref.putString(Key.USER_ID, value)
        }

    /**
     * 대리점 코드.
     */
    override var agencyCode: Long
        get() = pref.getLong(Key.AGENCY_CODE, 0L)
        set(value) {
            pref.putLong(Key.AGENCY_CODE, value)
        }

    /**
     * 대리점 명.
     */
    override var agencyName: String
        get() = Default.AGENCY_NAME
        set(value) {
            pref.putString(Key.AGENCY_NAME, value)
        }

    /**
     * Smart DM 아이디.
     */
    override var smartDmId: String
        get() = Default.SMART_DM_ID
        set(value) {
            pref.putString(Key.SMART_DM_ID, value)
        }

    /**
     * Smart DM 번호.
     */
    override var smartDmNumber: String
        get() = pref.getString(Key.SMART_DM_NUMBER, "").toString()
        set(value) {
            pref.putString(Key.SMART_DM_NUMBER, value)
        }

    /**
     * 부재콜 문자 발송 여부.
     */
    override var isMissedCallMessage: Boolean
        get() = pref.getBoolean(Key.IS_MISSED_CALL_MESSAGE, false)
        set(value) {
            pref.putBoolean(Key.IS_MISSED_CALL_MESSAGE, value)
        }

    /**
     * 발신 번호 저장.
     */
    override var keepSetDialStr: String
        get() = pref.getString(Key.KEEP_SET_DIAL_STR, "") ?: ""
        set(value) {
            pref.putString(Key.KEEP_SET_DIAL_STR, value)
        }

    /**
     * 법인폰 콜백 코드 저장.
     */
    override var dndValue: Int
        get() = pref.getInt(Key.DND_CODE, 0)
        set(value) {
            pref.putInt(Key.DND_CODE, value)
        }
}

interface OcxPreference {
    var isSaCall: Boolean
    var isRecord: Boolean
    var isEnc: Boolean
    var isCallActive: Boolean
    var isRecordBackup: Boolean
    var isRecordIncludeDate: Boolean
    var recordFolderName: String
    var recordFolderPath: String
    var saveState: Int
    var uploadHost: String
    var uploadPath: String
    var kctTitle: String
    var kctContent: String
    var kctNumber: String
    var kctSize: Long
    var kctDailyCount: Int
    var kctDailyLimit: Int
    var extra: String
    var poolName: String
    var userId: String
    var agencyCode: Long
    var agencyName: String
    var smartDmId: String
    var smartDmNumber: String
    var isMissedCallMessage: Boolean
    var keepSetDialStr: String
    var dndValue: Int
}