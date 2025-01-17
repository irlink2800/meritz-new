package com.irlink.meritz.util.network.socket

import io.reactivex.rxjava3.functions.Consumer

interface SocketCallback : Consumer<SocketState> {

    /**
     * 이곳에서 각 State에 맞는 메소드를 호출해준다.
     */
    override fun accept(state: SocketState) {
        when (state) {
            is SocketState.Opened -> onOpened()
            is SocketState.Connected -> onConnected()
            is SocketState.Response -> onResponse(state.data)
            is SocketState.Disconnected -> onDisconnected()
            is SocketState.Closed -> onClosed()
            is SocketState.Error -> onError(state.throwable)
        }
    }

    /**
     * 소켓이 열린 경우 호출.
     */
    fun onOpened()

    /**
     * 연결된 경우 호출.
     */
    fun onConnected()

    /**
     * 리스폰스를 받은 경우 호출.
     */
    fun onResponse(response: String)

    /**
     * 연결이 해제된 경우 호출.
     */
    fun onDisconnected()

    /**
     * 소켓이 닫힌 경우 호출.
     */
    fun onClosed()

    /**
     * 에러가 발생한 경우 호출.
     */
    fun onError(throwable: Throwable)

}
