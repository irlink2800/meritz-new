package com.irlink.meritz.util.message

enum class MessageType(

    val tag: String

) {

    /**
     * 수신.
     */
    RECEIVE("1"),

    /**
     * 발신.
     */
    SEND("0"),

    /**
     * 확인 불가.
     */
    UNKNOWN("-1")

}