package com.irlink.meritz.util.network.socket.client

import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.network.socket.readAll
import com.irlink.meritz.util.network.socket.write
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.ConnectException
import java.net.Socket

class ClientSocket(

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
        const val TAG: String = "ClientSocket"
    }

    constructor(socket: Socket) : this("", 0) {
        initSocket(socket)
    }

    /**
     * 클라이언트 소켓
     */
    private var socket: Socket? = null

    /**
     * BufferedInputStream을 사용 함.
     */
    private var inputStream: InputStream? = null

    /**
     * BufferedOutputStream을 사용 함.
     */
    private var outputStream: OutputStream? = null

    /**
     * 소켓이 연결된 상태면 true를 리턴함.
     */
    val isOpened: Boolean
        get() = socket != null && (socket!!.isConnected && !socket!!.isClosed)

    /**
     * 세팅된 host와 port로 소켓 연결 시도
     */
    fun open() = try {
        Socket(host, port).also { socket ->
            initSocket(socket)
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
    }

    private fun initSocket(socket: Socket) {
        this.socket = socket
        this.inputStream = BufferedInputStream(socket.getInputStream())
        this.outputStream = BufferedOutputStream(socket.getOutputStream())
    }

    /**
     * 연결을 해제하고 리소스 해제함.
     */
    fun close() {
        socket = socket?.run {
            close()
            return@run null
        }
        inputStream = inputStream?.run {
            close()
            return@run null
        }
        outputStream = outputStream?.run {
            flush()
            close()
            return@run null
        }
    }

    /**
     * 서버와의 연결이 끊길때 까지 데이터를 지속적으로 읽어서 콜백을 통해 리턴함.
     */
    fun readData(callback: (data: String) -> Unit) {
        while (isOpened) {
            callback(inputStream?.readAll() ?: break)
        }
    }

    /**
     * Json 데이터 전송
     */
    fun sendData(data: JSONObject) = sendData(data.toString())

    /**
     * String 데이터 전송
     */
    fun sendData(data: String) {
        outputStream?.write(data) ?: throw ConnectException("is not connected.")
    }

}