package com.irlink.meritz.util.network.socket

sealed class SocketState {

    /**
     * 열린 상태.
     */
    object Opened : SocketState()

    /**
     * 연결된 상태.
     */
    object Connected : SocketState()

    /**
     * 리스폰스를 받은 상태.
     */
    data class Response(val data: String) : SocketState()

    /**
     * 연결이 해제된 상태.
     */
    object Disconnected : SocketState()

    /**
     * 닫힌 상태.
     */
    object Closed : SocketState()

    /**
     * 에러가 발생한 상태.
     */
    data class Error(val throwable: Throwable) : SocketState()

}
