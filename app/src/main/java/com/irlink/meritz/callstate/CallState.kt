package com.irlink.meritz.callstate

enum class CallState(

    val value: Int,
    val tag: String

) {

    /**
     * 비정상적인 콜 스테이트.
     */
    ERROR(-1, "ERROR"),

    /**
     * 기본 상태.
     */
    IDLE(0, "IDLE"),

    /**
     * 전화 벨 울림.
     */
    RINGING(1, "RINGING"),

    /**
     * 전화 연결.
     */
    OFFHOOK(2, "OFFHOOK"),

    /**
     * 전화 연결.
     */
    CONNECTED(3, "CONNECTED"),
}

/**
 * Int to CallState.
 */
fun Int.toCallState(): CallState = when (this) {
    CallState.RINGING.value -> CallState.RINGING
    CallState.OFFHOOK.value -> CallState.OFFHOOK
    CallState.CONNECTED.value -> CallState.CONNECTED
    CallState.IDLE.value -> CallState.IDLE
    else -> CallState.ERROR
}