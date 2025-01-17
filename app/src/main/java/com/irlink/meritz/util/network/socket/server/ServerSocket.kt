package com.irlink.meritz.util.network.socket.server

import com.irlink.meritz.util.network.socket.client.ClientSocket
import com.irlink.meritz.util.LogUtil
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketException
import java.net.ServerSocket as JavaServerSocket

class ServerSocket(

    /**
     * 포트 넘버.
     */
    private val port: Int

) {

    companion object {
        const val TAG: String = "ServerSocket"
    }

    /**
     * 서버 소켓.
     */
    private var serverSocket: JavaServerSocket? = null

    /**
     * 연결된 클라이언트 소켓.
     */
    var clientSocket: ClientSocket? = null
        set(value) {
            field = when (value?.isOpened == true) {
                true -> value
                false -> null
            }
        }

    /**
     * 서버 소켓이 열린 상태면 true를 리턴함.
     */
    val isOpened: Boolean
        get() = serverSocket != null && !serverSocket!!.isClosed

    /**
     * 클라이언트 소켓이 연결된 상태면 true를 리턴함.
     */
    val isOpenedClientSocket: Boolean
        get() = clientSocket != null && clientSocket!!.isOpened

    /**
     * 세팅된 port 번호로 소켓 생성.
     */
    fun open() = try {
        JavaServerSocket(port).apply {
            reuseAddress = true
        }.also {
            serverSocket = it
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
    }

    /**
     * 서버 및 연결된 모든 클라이언트의 연결 종료.
     */
    fun close() {
        closeClientSocket()
        serverSocket = serverSocket?.run {
            close()
            return@run null
        }
    }

    /**
     * 클라이언트의 연결을 대기
     */
    fun accept(): ClientSocket? = try {
        when (isOpened) {
            true -> ClientSocket(
                socket = serverSocket!!.accept()
            )
            false -> null

        }?.also { clientSocket ->
            this.clientSocket = clientSocket
        }

    } catch (e: SocketException) {
        null
    }

    /**
     * 클라이언트의 연결을 종료함.
     */
    fun closeClientSocket() {
        if (clientSocket == null) {
            return
        }
        try {
            if (clientSocket!!.isOpened) {
                clientSocket!!.close()
            }
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
        }
        clientSocket = null
    }

    /**
     * 클라이언트와의 연결이 끊길때 까지 데이터를 지속적으로 읽어서 콜백을 통해 리턴함.
     */
    fun readData(callback: (data: String) -> Unit) {
        if (clientSocket == null) {
            return
        }
        clientSocket!!.readData {
            callback(it)
        }
    }

    /**
     * 클라이언트에 Json 데이터 전송
     */
    fun sendData(data: JSONObject) = sendData(data.toString())

    /**
     * 클라이언트에 String 데이터 전송
     */
    fun sendData(data: String) {
        clientSocket?.sendData(data) ?: throw ConnectException("is not connected.")
    }

}