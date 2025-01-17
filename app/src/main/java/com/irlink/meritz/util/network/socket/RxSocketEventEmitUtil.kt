package com.irlink.meritz.util.network.socket

import io.reactivex.rxjava3.core.FlowableEmitter
import java.net.ConnectException

open class RxSocketEventEmitUtil {

    /**
     * 소켓 열림 이벤트 발행.
     */
    open fun emitOpened(emitter: FlowableEmitter<SocketState>) {
        emitter.onNext(SocketState.Opened)
    }

    /**
     * 연결 이벤트 발행.
     */
    open fun emitConnected(emitter: FlowableEmitter<SocketState>) {
        emitter.onNext(SocketState.Connected)
    }

    /**
     * 리스폰스 데이터 발행.
     */
    open fun emitResponse(emitter: FlowableEmitter<SocketState>, data: String?) {
        val response: SocketState = SocketState.Response(
            data = data ?: ""
        )
        emitter.onNext(response)
    }

    /**
     * 연결 닫힘 이벤트 발행.
     */
    open fun emitDisconnected(emitter: FlowableEmitter<SocketState>) {
        emitter.onNext(SocketState.Disconnected)
    }

    /**
     * 소켓 종료 이벤트 발행.
     */
    open fun emitClosed(emitter: FlowableEmitter<SocketState>) {
        emitter.onNext(SocketState.Closed)
    }

    /**
     * 에러 발행.
     */
    open fun emitError(emitter: FlowableEmitter<SocketState>, throwable: Throwable) {
        val error: SocketState.Error = SocketState.Error(
            throwable = throwable
        )
        emitter.onNext(error)
    }

    /**
     * 이미 연결된 소켓 에러 이벤트 발행.
     */
    open fun emitAlreadyOpenedError(emitter: FlowableEmitter<SocketState>) =
        emitError(emitter, ConnectException("is already opened."))

}