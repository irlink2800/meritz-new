package com.irlink.meritz.ocx

import com.irlink.meritz.util.network.socket.RxSocketEventEmitUtil
import com.irlink.meritz.util.network.socket.SocketState
import com.irlink.meritz.ocx.mapper.OcxEventMapper
import com.irlink.meritz.util.LogUtil
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableEmitter
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import com.irlink.meritz.ocx.mapper.OcxMethodMapper
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.net.ConnectException

abstract class OcxSocket(

    private val host: String,
    private val port: Int = 0,

    okHttpClient: OkHttpClient

) : Ocx {

    companion object {
        const val TAG: String = "OcxSocket"
    }

    object Event {
        const val PING: String = "ocx_ping"
        const val PONG: String = "ocx_pong"
    }

    abstract override val ocxMode: OcxMode

    protected var socket: Socket? = null

    open val socketAddress: String
        get() = when {
            port <= 0 -> host
            else -> "$host:$port"
        }

    protected open val options: IO.Options = IO.Options().apply {
        reconnection = true
        reconnectionAttempts = Int.MAX_VALUE
        reconnectionDelay = 500L
        reconnectionDelayMax = reconnectionDelay
        transports = arrayOf(WebSocket.NAME)
    }

    protected abstract val receiveEvent: String

    override val event: OcxEvent by lazy {
        OcxEvent(this)
    }

    protected abstract val eventMapper: OcxEventMapper

    protected abstract val methodMapper: OcxMethodMapper

    protected val rxSocketEventEmitUtil: RxSocketEventEmitUtil by lazy {
        RxSocketEventEmitUtil()
    }

    init {
        IO.setDefaultOkHttpCallFactory(okHttpClient)
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
    }

    override fun open(): Flowable<SocketState> = Flowable.create({ emitter ->
        synchronized(this) {
            if (socket == null) {
                socket = IO.socket(socketAddress, options).apply {
                    on(Socket.EVENT_CONNECT) {
                        onConnected(emitter)
                    }
                    on(Socket.EVENT_DISCONNECT) {
                        onDisconnected(emitter)
                    }
                    on(receiveEvent) { args ->
                        onResponse(emitter, methodMapper.map(args.toResponse()))
                    }
                    on(Socket.EVENT_CONNECT_ERROR) { args ->
                        onError(emitter, args.toError())
                    }
                    on(Socket.EVENT_CONNECT_ERROR) { args ->
                        onError(emitter, args.toError())
                    }
                }
            }
            when (socket?.connected()) {
                true -> rxSocketEventEmitUtil.emitAlreadyOpenedError(emitter)
                else -> socket?.connect()
            }
        }
    }, BackpressureStrategy.BUFFER)

    protected open fun Array<Any>.toResponse(): JSONObject = when (val response = firstOrNull()) {
        is JSONObject -> response
        is String -> try {
            JSONObject(response)
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            JSONObject()
        }
        else -> JSONObject()
    }

    protected open fun Array<Any>.toError(): Exception =
        Exception((firstOrNull() as? JSONObject)?.toString(4))

//    protected fun Socket.setReconnecting(isReconnecting: Boolean) = try {
//        val ioClass = io().javaClass
//        val reconnecting = ioClass.getDeclaredField("reconnecting").apply {
//            isAccessible = true
//        }
//        reconnecting.setBoolean(io(), isReconnecting)
//    } catch (e: Exception) {
//        LogUtil.exception(TAG, e)
//    }

    protected open fun onConnected(emitter: FlowableEmitter<SocketState>) {
//        socket?.setReconnecting(false)
        rxSocketEventEmitUtil.emitOpened(emitter)
        rxSocketEventEmitUtil.emitConnected(emitter)
    }

    protected open fun onDisconnected(emitter: FlowableEmitter<SocketState>) {
        rxSocketEventEmitUtil.emitDisconnected(emitter)
        rxSocketEventEmitUtil.emitClosed(emitter)
    }

    protected open fun onResponse(emitter: FlowableEmitter<SocketState>, response: String?) {
        rxSocketEventEmitUtil.emitResponse(emitter, response)
    }

    protected open fun onError(emitter: FlowableEmitter<SocketState>, error: Exception) {
        rxSocketEventEmitUtil.emitError(emitter, error)
    }

    abstract override fun sendData(params: OcxParams): Flowable<Unit>

    override fun close(): Flowable<Unit> = Flowable.fromCallable<Unit> {
        synchronized(this) {
            socket?.disconnect() ?: throw ConnectException("is not connected.")
            socket = null
        }
    }

}