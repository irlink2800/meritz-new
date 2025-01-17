package com.irlink.meritz.ocx

import com.irlink.meritz.util.network.socket.SocketState
import io.reactivex.rxjava3.core.Flowable


interface Ocx {

    val ocxMode: OcxMode

    val event: OcxEvent

    fun open(): Flowable<SocketState>

    fun close(): Flowable<Unit>

    fun sendData(params: OcxParams): Flowable<Unit>

}