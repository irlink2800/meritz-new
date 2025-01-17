package com.irlink.meritz.repositories.converters

import androidx.room.TypeConverter
import com.irlink.meritz.record.RecordType

class RecordTypeConverter {

    object Value {
        const val INBOUND: String = "INBOUND"
        const val OUTBOUND: String = "OUTBOUND"
        const val FTF: String = "FTF"
        const val UNKNOWN: String = "UNKNOWN"
    }

    @TypeConverter
    fun toString(recordType: RecordType?): String = when (recordType) {
        RecordType.INBOUND -> Value.INBOUND
        RecordType.OUTBOUND -> Value.OUTBOUND
        RecordType.FTF -> Value.FTF
        else -> Value.UNKNOWN
    }

    @TypeConverter
    fun fromString(recordType: String?): RecordType = when (recordType) {
        Value.INBOUND -> RecordType.INBOUND
        Value.OUTBOUND -> RecordType.OUTBOUND
        Value.FTF -> RecordType.FTF
        else -> RecordType.UNKNOWN
    }
}