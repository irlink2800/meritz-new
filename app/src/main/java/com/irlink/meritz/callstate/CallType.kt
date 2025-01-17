package com.irlink.meritz.callstate

enum class CallType(

    val tag: String,
    val value: Int

) {

    /**
     * 기본값.
     */
    UNKNOWN("U", 0),

    /**
     * 인바운드.
     */
    INBOUND("I", 1),

    /**
     * 아웃바운드.
     */
    OUTBOUND("O", 2),

    /**
     * 아웃바운드 사콜.
     */
    OUTBOUND_SACALL("OS", 3),

    /**
     * 인바운드 사콜.
     */
    INBOUND_SACALL("IS", 6)

}