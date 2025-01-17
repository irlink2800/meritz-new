package com.irlink.meritz.record

enum class RecordType(

    val tag: String

) {

    /**
     * 대면 녹취.
     */
    FTF("F"),

    /**
     * 인바운드.
     */
    INBOUND("I"),

    /**
     * 아웃바운드.
     */
    OUTBOUND("O"),

    /**
     * 확인 불가.
     */
    UNKNOWN("UNKNOWN")

}