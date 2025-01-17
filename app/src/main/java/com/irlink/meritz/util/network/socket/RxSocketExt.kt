package com.irlink.meritz.util.network.socket

import io.reactivex.rxjava3.annotations.BackpressureKind
import io.reactivex.rxjava3.annotations.BackpressureSupport
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.annotations.SchedulerSupport
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * set subscribe socket
 */
@CheckReturnValue
@BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
@SchedulerSupport(SchedulerSupport.NONE)
fun Flowable<SocketState>.subscribeSocket(socketCallback: SocketCallback): Disposable = subscribe(socketCallback)

/**
 * Kotlin Support
 */
@CheckReturnValue
@BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
@SchedulerSupport(SchedulerSupport.NONE)
fun Flowable<SocketState>.subscribeSocket(

    onOpened: (() -> Unit)? = null,
    onConnected: (() -> Unit)? = null,
    onResponse: ((data: String) -> Unit)? = null,
    onDisconnected: (() -> Unit)? = null,
    onClosed: (() -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null

): Disposable = subscribeSocket(object : SocketCallback {
    override fun onOpened() {
        onOpened?.let {
            it()
        }
    }

    override fun onConnected() {
        onConnected?.let {
            it()
        }
    }

    override fun onResponse(response: String) {
        onResponse?.let {
            it(response)
        }
    }

    override fun onDisconnected() {
        onDisconnected?.let {
            it()
        }
    }

    override fun onClosed() {
        onClosed?.let {
            it()
        }
    }

    override fun onError(throwable: Throwable) {
        onError?.let {
            it(throwable)
        }
    }
})