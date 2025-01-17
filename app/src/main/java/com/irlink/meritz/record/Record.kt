package com.irlink.meritz.record

import android.os.Parcelable
import com.irlink.irrecorder.AudioExtensions
import com.irlink.meritz.ocx.OcxMode
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import com.irlink.meritz.callstate.CallType
import org.json.JSONObject
import java.io.File
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
open class Record constructor(

    /**
     * 녹취 타입.
     */
    @Json(name = Field.RECORD_TYPE)
    open var recordType: RecordType = RecordType.UNKNOWN,

    /**
     * 암호화 필요 여부.
     */
    @Json(name = Field.SHOULD_ENC)
    open var shouldEnc: Boolean,

    /**
     * 저장될 녹취 파일의 폴더
     */
    @Json(name = Field.SAVE_DIR)
    open var saveDir: File,

    /**
     * 업로드 할 서버의 패스
     */
    @Json(name = Field.UPLOAD_PATH)
    open var uploadPath: String,

    /**
     * 확장자 타입
     */
    @Json(name = Field.EXTENSION)
    open var extension: AudioExtensions = AudioExtensions.AMR,

    /**
     * 암호화 여부.
     */
    @Json(name = Field.IS_ENC)
    open var isEnc: Boolean = false,

    /**
     * 사콜 여부.
     */
    @Json(name = Field.IS_SACALL)
    open var isSaCall: Boolean = true,

    /**
     * 녹취 여부.
     */
    @Json(name = Field.IS_RECORD)
    open var isRecord: Boolean = true,

    /**
     * 대리점 정보.
     */
    @Json(name = Field.EXTRA)
    open var extra: String = "",

    /**
     * 통화 시작 일시.
     */
    @Json(name = Field.CALL_DATE)
    open var callDate: String = DEFAULT_VALUE,

    /**
     * 통화 시작 시간.
     */
    @Json(name = Field.CALL_START_TIME)
    open var callStartTime: String = DEFAULT_VALUE,

    /**
     * 통화 종료 일시.
     */
    @Json(name = Field.CALL_END_DATE)
    open var callEndDate: String = DEFAULT_VALUE,

    /**
     * 통화 종료 시간.
     */
    @Json(name = Field.CALL_END_TIME)
    open var callEndTime: String = DEFAULT_VALUE,

    /**
     * 통화 연결된 일시.
     */
    @Json(name = Field.CALL_CONNECTED_DATE)
    open var callConnectedDate: String = DEFAULT_VALUE,

    /**
     * 통화가 연결된 시간.
     */
    @Json(name = Field.CALL_CONNECTED_TIME)
    open var callConnectedTime: String = DEFAULT_VALUE,

    /**
     * 유저 전화번호.
     */
    @Json(name = Field.USER_NUMBER)
    open var userNumber: String = "",

    /**
     * 고객 전화번호.
     */
    @Json(name = Field.REMOTE_NUMBER)
    open var remoteNumber: String = "",

    /**
     * 초기 파일명. (확장자 미포함.)
     */
    @Json(name = Field.INITIALIZE_FILE_NAME)
    open var initializeFileName: String = "",

    /**
     * ocx를 통해 설정되는 최종 파일명. (확장자 미포함)
     */
    @Json(name = Field.OCX_FILE_NAME)
    open var ocxFileName: String? = null,

    /**
     * 부분녹취 플래그.
     */
    @Volatile
    @Json(name = Field.IS_PARTIAL_RECORD)
    open var isPartialRecord: Boolean = false,

    /**
     * 부분 녹취 시작 인덱스.
     */
    @Json(name = Field.PARTIAL_START_INDEX)
    open var partialStartIndex: Long = 0,

    /**
     * 부분 녹취 파일명.
     */
    @Json(name = Field.PARTIAL_FILE_NAME)
    open var partialFileName: String? = null,

    /**
     * 순수 통화 시간.
     */
    @Json(name = Field.TALK_TIME)
    open var talkTime: String = DEFAULT_VALUE,

    /**
     * 링 타임 시간. (아웃바운드는 상대방이 전화를 받을 때까지, 인바운드는 전화를 받을 때까지.)
     */
    @Json(name = Field.RING_TIME)
    open var ringTime: String = DEFAULT_VALUE,

    /**
     * 녹취 파일 사이즈.
     */
    @Json(name = Field.FILE_SIZE)
    open var fileSize: Long = 0L,

    /**
     * 녹취 중 여부.
     */
    @Json(name = Field.IS_STARTED_RECORD)
    open var isStartedRecord: Boolean = false,

    /**
     * 정상 녹취 여부.
     */
    @Json(name = Field.IS_MISSED_RECORD)
    open var isMissedRecord: Boolean = true,

    /**
     * 자동 통화 종료 여부.
     */
    @Json(name = Field.IS_AUTO_DISCONNECTED)
    open var isAutoDisconnected: Boolean = false,

    /**
     * 모렉스 업로드 여부.
     */
    @Volatile
    @Json(name = Field.IS_MORECX_UPLOADED)
    open var isMorecxUploaded: Boolean = false,

    /**
     * 재전송 시도 횟수
     */
    @Volatile
    @Json(name = Field.TRY_UPLOAD_COUNT)
    open var tryUploadCount: Int = 0,

    /**
     * 레거시 녹취 파일 경로.
     */
    @Json(name = Field.LEGACY_FILE_PATH)
    open var legacyFilePath: String = ""

) : Parcelable {

    companion object {
        const val SEPARATOR: String = "_"
        const val DEFAULT_VALUE: String = "0"
    }

    object Field {
        const val RECORD_TYPE: String = "record_type"
        const val IS_ENC: String = "is_enc"
        const val SHOULD_ENC: String = "should_enc"
        const val SAVE_DIR: String = "save_dir"
        const val UPLOAD_PATH: String = "upload_path"
        const val EXTENSION: String = "extension"
        const val IS_SACALL: String = "is_sacall"
        const val IS_RECORD: String = "is_record"
        const val EXTRA: String = "extra"
        const val CALL_DATE: String = "call_date"
        const val CALL_START_TIME: String = "call_start_time"
        const val CALL_END_DATE: String = "call_end_date"
        const val CALL_END_TIME: String = "call_end_time"
        const val CALL_CONNECTED_DATE: String = "call_connected_date"
        const val CALL_CONNECTED_TIME: String = "call_connected_time"
        const val USER_NUMBER: String = "user_number"
        const val REMOTE_NUMBER: String = "remote_number"
        const val INITIALIZE_FILE_NAME: String = "initialize_file_name"
        const val OCX_FILE_NAME: String = "ocx_file_name"
        const val IS_PARTIAL_RECORD: String = "is_partial_record"
        const val PARTIAL_START_INDEX: String = "partial_start_index"
        const val PARTIAL_FILE_NAME: String = "partial_file_name"
        const val TALK_TIME: String = "talk_time"
        const val RING_TIME: String = "ring_time"
        const val FILE_SIZE: String = "file_size"
        const val IS_STARTED_RECORD: String = "is_started_record"
        const val IS_MISSED_RECORD: String = "is_missed_record"
        const val IS_AUTO_DISCONNECTED: String = "is_auto_disconnected"
        const val IS_MORECX_UPLOADED: String = "is_morecx_uploaded"
        const val TRY_UPLOAD_COUNT: String = "try_upload_count"
        const val LEGACY_FILE_PATH: String = "legacy_file_path"
    }

    /**
     * 아이디. (셀러링)
     */
    open val id: Long
        get() = initializeFileName.hashCode().toLong()

    /**
     * 콜 타입.
     */
    open val callType: CallType
        get() = when (isSaCall) {
            true -> when (recordType) {
                RecordType.INBOUND -> CallType.INBOUND_SACALL
                RecordType.OUTBOUND -> CallType.OUTBOUND_SACALL
                else -> CallType.UNKNOWN
            }

            false -> when (recordType) {
                RecordType.INBOUND -> CallType.INBOUND
                RecordType.OUTBOUND -> CallType.OUTBOUND
                else -> CallType.UNKNOWN
            }
        }

    /**
     * 해당 녹취에 대한 통화가 종료됐으면 true.
     */
    open val isCallEnd: Boolean
        get() = callEndTime != DEFAULT_VALUE

    /**
     * 통화 시작 날짜 + 시간.
     */
    open val callStartDateTime: String
        get() = when (callStartTime != DEFAULT_VALUE) {
            true -> callDate + callStartTime
            else -> DEFAULT_VALUE
        }

    /**
     * 통화 종료 날짜 + 시간.
     */
    open val callEndDateTime: String
        get() = when (callEndTime != DEFAULT_VALUE) {
            true -> when (callEndDate != DEFAULT_VALUE) {
                true -> callEndDate + callEndTime
                else -> callDate + callEndTime
            }

            else -> DEFAULT_VALUE
        }

    /**
     * 통화 연결 날짜 + 시간.
     */
    open val callConnectedDateTime: String
        get() = when (callConnectedTime != DEFAULT_VALUE) {
            true -> when (callConnectedDate != DEFAULT_VALUE) {
                true -> callConnectedDate + callConnectedTime
                else -> callDate + callEndTime
            }

            else -> DEFAULT_VALUE
        }

    /**
     * 통화 시간 (순수 통화 시간 + 링타임)
     */
    open val callTime: String
        get() = (talkTime.toLong() + ringTime.toLong()).toString()

    /**
     * 레코드 정보 - IR-WIRELESS
     */
    open val wirelessInfo: String
        get() = JSONObject().apply {

            // 필수값
            put("io", recordType.tag)                           // 인콜 아웃콜 여부
            put("tcStDtm", callStartDateTime)                   // 통화 시작 시간
            put("cllStDtm", callConnectedDateTime)              // 고객과 통화가 연결된 시간
            put("tcEdDtm", callEndDateTime)                     // 통화 종료 시간
            put("cnsEdDtm", callEndDateTime)                    // 상담 종료 시간
            put("callTime", callTime)                           // 링 타임 포함 통화 시간
            put("tcHr", callTime)                               // 통화 시간
            put("cnsHr", talkTime)                              // 상담 시간
            put("rcdgFileNm", finalFileName)                    // 녹취 파일명
            put("rcdgFilePthNm", uploadPath)                    // 녹취 파일 경로
            put("cusTelNo", remoteNumber)                       // 고객 전화번호

            // 고정값
            put("cllEqupKdCd", "COR")   // 법인콜장비

            // 미사용 정보 공백 처리
            put("cnsCusId", "")         // 상담고객 ID
            put("cusCnsRespId", "")     // 고객상담응대 ID
            put("cusNm", "")            // 고객명
            put("infwPthCd", "")        // 유입경로코드
            put("tlmkCnsCon", "")       // 상담내용
            put("tlmkTcRslCd", "")      // 통화 결과 코드

            // KB 손보 전용 옵션.
            put("nFlag", if (callEndDateTime == DEFAULT_VALUE) "1" else "0")  // 통화 상태 플래그

        }.toString()

    /**
     * 초기 파일.
     */
    open val initializeFile: File
        get() = File(
            saveDir, "$initializeFileName.$extensionName"
        )

    /**
     * ocx 파일.
     */
    open val ocxFile: File
        get() = File(
            saveDir, "$ocxFileName.$extensionName"
        )

    /**
     * 최종 파일명. (확장자 제외)
     */
    open val finalFileName: String
        get() = when (ocxFileName.isNullOrEmpty()) {
            true -> initializeFileName
            else -> ocxFileName!!
        }

    /**
     * 최종 파일. (ocxFileName이 있는 경우 OcxFile, 없는 경우 initializeFile)
     */
    open val finalFile: File
        get() = File(
            saveDir, "$finalFileName.$extensionName"
        )

    /**
     * 선택 녹취 파일.
     */
    open val partialFile: File
        get() = File(
            saveDir, "${partialFileName}.${extensionName}"
        )

    /**
     * 확장자명.
     */
    open val extensionName: String
        get() = extension.extensionName.toLowerCase(Locale.getDefault())

    /**
     * 레거시 녹취 파일.
     */
    open val legacyFile: File
        get() = File(legacyFilePath)

    /**
     * 레거시 녹취 파일 존재 여부.
     */
    open val isLegacyFileExist: Boolean
        get() = legacyFile.exists()


    init {
        initializeFileName()
    }

    /**
     * 최초 파일명 지정.
     */
    private fun initializeFileName() {
        initializeFileName = StringBuilder()
            .append(userNumber)
            .append(callStartDateTime)
            .toString()
    }

    /**
     * 레코드 정보. (OCX 통신에 사용)
     */
    open fun getRecordInfo(ocxMode: OcxMode): String = when (ocxMode) {
        OcxMode.WIRELESS -> wirelessInfo
    }

    /**1``
     * 자르기 관련 새로운 레코드 객체 생성.
     */
    open fun cutRecord(fileName: String?): Record? = when (fileName.isNullOrEmpty()) {
        true -> null
        else -> copy().apply {
            initializeFileName = fileName
            ocxFileName = fileName
            partialFileName = null
        }
    }

    /**
     * 새로운 부분녹취 레코드 객체 생성.
     */
    open fun partialRecord(): Record? = cutRecord(partialFileName)

    /**
     * equals.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (recordType != other.recordType) return false
        if (shouldEnc != other.shouldEnc) return false
        if (saveDir != other.saveDir) return false
        if (uploadPath != other.uploadPath) return false
        if (extension != other.extension) return false
        if (isEnc != other.isEnc) return false
        if (isSaCall != other.isSaCall) return false
        if (isRecord != other.isRecord) return false
        if (extra != other.extra) return false
        if (callDate != other.callDate) return false
        if (callStartTime != other.callStartTime) return false
        if (callEndDate != other.callEndDate) return false
        if (callEndTime != other.callEndTime) return false
        if (callConnectedDate != other.callConnectedDate) return false
        if (callConnectedTime != other.callConnectedTime) return false
        if (userNumber != other.userNumber) return false
        if (remoteNumber != other.remoteNumber) return false
        if (initializeFileName != other.initializeFileName) return false
        if (ocxFileName != other.ocxFileName) return false
        if (isPartialRecord != other.isPartialRecord) return false
        if (partialStartIndex != other.partialStartIndex) return false
        if (partialFileName != other.partialFileName) return false
        if (talkTime != other.talkTime) return false
        if (ringTime != other.ringTime) return false
        if (fileSize != other.fileSize) return false
        if (isStartedRecord != other.isStartedRecord) return false
        if (isMissedRecord != other.isMissedRecord) return false
        if (isAutoDisconnected != other.isAutoDisconnected) return false
        if (isMorecxUploaded != other.isMorecxUploaded) return false
        if (tryUploadCount != other.tryUploadCount) return false

        return true
    }

    /**
     * hashcode.
     */
    override fun hashCode(): Int {
        var result = recordType.hashCode()
        result = 31 * result + shouldEnc.hashCode()
        result = 31 * result + saveDir.hashCode()
        result = 31 * result + uploadPath.hashCode()
        result = 31 * result + extension.hashCode()
        result = 31 * result + isEnc.hashCode()
        result = 31 * result + isSaCall.hashCode()
        result = 31 * result + isRecord.hashCode()
        result = 31 * result + extra.hashCode()
        result = 31 * result + callDate.hashCode()
        result = 31 * result + callStartTime.hashCode()
        result = 31 * result + callEndDate.hashCode()
        result = 31 * result + callEndTime.hashCode()
        result = 31 * result + callConnectedDate.hashCode()
        result = 31 * result + callConnectedTime.hashCode()
        result = 31 * result + userNumber.hashCode()
        result = 31 * result + remoteNumber.hashCode()
        result = 31 * result + initializeFileName.hashCode()
        result = 31 * result + (ocxFileName?.hashCode() ?: 0)
        result = 31 * result + isPartialRecord.hashCode()
        result = 31 * result + partialStartIndex.hashCode()
        result = 31 * result + (partialFileName?.hashCode() ?: 0)
        result = 31 * result + talkTime.hashCode()
        result = 31 * result + ringTime.hashCode()
        result = 31 * result + fileSize.hashCode()
        result = 31 * result + isStartedRecord.hashCode()
        result = 31 * result + isMissedRecord.hashCode()
        result = 31 * result + isAutoDisconnected.hashCode()
        result = 31 * result + isMorecxUploaded.hashCode()
        result = 31 * result + tryUploadCount
        return result
    }

    /**
     * copy.
     */
    open fun copy(
        recordType: RecordType = this.recordType,
        shouldEnc: Boolean = this.shouldEnc,
        saveDir: File = this.saveDir,
        uploadPath: String = this.uploadPath,
        extension: AudioExtensions = this.extension,
        isEnc: Boolean = this.isEnc,
        isSaCall: Boolean = this.isSaCall,
        isRecord: Boolean = this.isRecord,
        extra: String = this.extra,
        callDate: String = this.callDate,
        callStartTime: String = this.callStartTime,
        callEndDate: String = this.callEndDate,
        callEndTime: String = this.callEndTime,
        callConnectedDate: String = this.callConnectedDate,
        callConnectedTime: String = this.callConnectedTime,
        userNumber: String = this.userNumber,
        remoteNumber: String = this.remoteNumber,
        initializeFileName: String = this.initializeFileName,
        ocxFileName: String? = this.ocxFileName,
        isPartialRecord: Boolean = this.isPartialRecord,
        partialStartIndex: Long = this.partialStartIndex,
        partialFileName: String? = this.partialFileName,
        talkTime: String = this.talkTime,
        ringTime: String = this.ringTime,
        fileSize: Long = this.fileSize,
        isStartedRecord: Boolean = this.isStartedRecord,
        isMissedRecord: Boolean = this.isMissedRecord,
        isAutoDisconnected: Boolean = this.isAutoDisconnected,
        isMorecxUploaded: Boolean = this.isMorecxUploaded,
        tryUploadCount: Int = this.tryUploadCount
    ): Record = Record(
        recordType = recordType,
        shouldEnc = shouldEnc,
        saveDir = saveDir,
        uploadPath = uploadPath,
        extension = extension,
        isEnc = isEnc,
        isSaCall = isSaCall,
        isRecord = isRecord,
        extra = extra,
        callDate = callDate,
        callStartTime = callStartTime,
        callEndDate = callEndDate,
        callEndTime = callEndTime,
        callConnectedDate = callConnectedDate,
        callConnectedTime = callConnectedTime,
        userNumber = userNumber,
        remoteNumber = remoteNumber,
        initializeFileName = initializeFileName,
        ocxFileName = ocxFileName,
        isPartialRecord = isPartialRecord,
        partialStartIndex = partialStartIndex,
        partialFileName = partialFileName,
        talkTime = talkTime,
        ringTime = ringTime,
        fileSize = fileSize,
        isStartedRecord = isStartedRecord,
        isMissedRecord = isMissedRecord,
        isAutoDisconnected = isAutoDisconnected,
        isMorecxUploaded = isMorecxUploaded,
        tryUploadCount = tryUploadCount
    )

}
