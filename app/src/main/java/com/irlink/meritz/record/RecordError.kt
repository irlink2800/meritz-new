package com.irlink.meritz.record

data class RecordError(

    val record: Record?,

    val code: Int,

    val message: String?

)