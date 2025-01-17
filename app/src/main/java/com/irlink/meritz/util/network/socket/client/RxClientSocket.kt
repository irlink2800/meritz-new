package com.irlink.meritz.util.network.socket.client

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import com.irlink.meritz.util.network.socket.RxSocketEventEmitUtil
import com.irlink.meritz.util.network.socket.SocketState
import org.json.JSONObject

class RxClientSocket(

    /**
     * 연결할 서버의 호스트
     */
    private val host: String,

    /**
     * 연결할 서버의 포트 넘버
     */
    private val port: Int

) {

    companion object {
        const val TAG: String = "RxClientSocket"
    }

    /**
     * 클라이언트 소켓
     */
    private val clientSocket: ClientSocket by lazy {
        ClientSocket(host, port)
    }

    /**
     * 이벤트 발행 유틸.
     */
    private val rxSocketEventEmitUtil: RxSocketEventEmitUtil by lazy {
        RxSocketEventEmitUtil()
    }

    /**
     * 연결.
     */
    fun open(): Flowable<SocketState> = Flowable.create<SocketState>({ emitter ->
        synchronized(this) {
            // 이미 연결된 상태인지 체크
            if (clientSocket.isOpened) {
                rxSocketEventEmitUtil.emitAlreadyOpenedError(emitter)
                return@create
            }
            // 소켓 생성
            clientSocket.open()
            rxSocketEventEmitUtil.emitOpened(emitter)
            rxSocketEventEmitUtil.emitConnected(emitter)
        }
        try {
            // 연결 종료 시 까지 통신
            clientSocket.readData { data ->
                rxSocketEventEmitUtil.emitResponse(emitter, data)
            }

        } catch (e: Exception) {
            // 에러 처리.
            rxSocketEventEmitUtil.emitError(emitter, e)

        } finally {
            // 연결을 끊고 종료
            if (clientSocket.isOpened) {
                clientSocket.close()
            }
            rxSocketEventEmitUtil.emitDisconnected(emitter)
            rxSocketEventEmitUtil.emitClosed(emitter)
        }
    }, BackpressureStrategy.BUFFER)

    /**
     * Json 데이터 전송
     */
    fun sendData(data: JSONObject): Flowable<Unit> = sendData(data.toString())

    /**
     * String 데이터 전송
     */
    fun sendData(data: String): Flowable<Unit> = Flowable.fromCallable {
        synchronized(this) {
            clientSocket.sendData(data)
        }
    }

    /**
     * 연결 해제
     */
    fun close(): Flowable<Unit> = Flowable.fromCallable<Unit> {
        synchronized(this) {
            clientSocket.close()
        }
    }

}

