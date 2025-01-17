package com.irlink.meritz.util.network.socket.server

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import com.irlink.meritz.util.network.socket.RxSocketEventEmitUtil
import com.irlink.meritz.util.network.socket.SocketState
import com.irlink.meritz.util.network.socket.client.ClientSocket
import org.json.JSONObject

class RxServerSocket(

    /**
     * 포트 넘버.
     */
    private val port: Int

) {

    companion object {
        const val TAG: String = "RxServerSocket"
    }

    /**
     * 서버 소켓.
     */
    private val serverSocket: ServerSocket by lazy {
        ServerSocket(port)
    }

    /**
     * 이벤트 발행 유틸.
     */
    private val rxSocketEventEmitUtil: RxSocketEventEmitUtil by lazy {
        RxSocketEventEmitUtil()
    }

    /**
     * 서버 소켓 생성.
     */
    fun open(): Flowable<SocketState> = Flowable.create<SocketState>({ emitter ->
        synchronized(this) {
            // 이미 생성된 상태인지 체크
            if (serverSocket.isOpened) {
                rxSocketEventEmitUtil.emitAlreadyOpenedError(emitter)
                return@create
            }
            // 서버 소켓 생성
            serverSocket.open()
            rxSocketEventEmitUtil.emitOpened(emitter)
        }
        try {
            while (serverSocket.isOpened) {
                // 클라이언트 연결 대기
                val clientSocket: ClientSocket = serverSocket.accept() ?: continue

                // 클라이언트 연결 완료
                rxSocketEventEmitUtil.emitConnected(emitter)

                // 연결 종료 시 까지 통신
                clientSocket.readData { data ->
                    rxSocketEventEmitUtil.emitResponse(emitter, data)
                }

                // 클라이언트 연결 종료
                if (serverSocket.isOpenedClientSocket) {
                    serverSocket.closeClientSocket()
                }
                rxSocketEventEmitUtil.emitDisconnected(emitter)
            }

        } catch (e: Exception) {
            // 에러 발생
            rxSocketEventEmitUtil.emitError(emitter, e)

        } finally {
            // 연결을 끊고 종료
            if (serverSocket.isOpened) {
                serverSocket.close()
            }
            rxSocketEventEmitUtil.emitClosed(emitter)
        }
    }, BackpressureStrategy.BUFFER)

    /**
     * Json 데이터 전송.
     */
    fun sendData(data: JSONObject): Flowable<Unit> = sendData(data.toString())

    /**
     * String 데이터 전송.
     */
    fun sendData(data: String): Flowable<Unit> = Flowable.fromCallable {
        synchronized(this) {
            serverSocket.sendData(data)
        }
    }

    /**
     * 연결 해제.
     */
    fun close(): Flowable<Unit> = Flowable.fromCallable<Unit> {
        synchronized(this) {
            serverSocket.close()
        }
    }

}