package com.irlink.meritz.util.moshi

import com.irlink.meritz.record.RecordType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class RecordTypeAdapter {

    object Value {
        const val INBOUND: String = "INBOUND"
        const val OUTBOUND: String = "OUTBOUND"
        const val FTF: String = "FTF"
        const val UNKNOWN: String = "UNKNOWN"
    }

    @ToJson
    fun toJson(recordType: RecordType?): String = when (recordType) {
        RecordType.INBOUND -> Value.INBOUND
        RecordType.OUTBOUND -> Value.OUTBOUND
        RecordType.FTF -> Value.FTF
        else -> Value.UNKNOWN
    }

    @FromJson
    fun fromJson(recordType: String?): RecordType = when (recordType) {
        Value.INBOUND -> RecordType.INBOUND
        Value.OUTBOUND -> RecordType.OUTBOUND
        Value.FTF -> RecordType.FTF
        else -> RecordType.UNKNOWN
    }

}