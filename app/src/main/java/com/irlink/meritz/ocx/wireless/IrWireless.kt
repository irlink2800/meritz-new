package com.irlink.meritz.ocx.wireless

import com.irlink.meritz.ocx.OcxMode
import com.irlink.meritz.ocx.OcxParams
import com.irlink.meritz.ocx.OcxSocket
import com.irlink.meritz.ocx.mapper.OcxEventMapper
import io.reactivex.rxjava3.core.Flowable
import com.irlink.meritz.ocx.mapper.OcxMethodMapper

import okhttp3.OkHttpClient
import java.net.ConnectException

class IrWireless(

    okHttpClient: OkHttpClient,
    wirelessPref: WirelessPreference

) : OcxSocket(
    host = wirelessPref.serverUrl,
    okHttpClient = okHttpClient
) {

    companion object {
        const val TAG: String = "IrWireless"
        const val EVENT_DATA_RECEIVE: String = "message"
    }

    override val ocxMode: OcxMode
        get() = OcxMode.WIRELESS

    override val receiveEvent: String
        get() = EVENT_DATA_RECEIVE

    override val eventMapper: OcxEventMapper by lazy {
        IrWirelessEventMapper()
    }

    override val methodMapper: OcxMethodMapper by lazy {
        IrWirelessMethodMapper()
    }

    private val irWirelessEventMapper: IrWirelessEventMapper
        get() = eventMapper as IrWirelessEventMapper

    var key: String?
        set(value) {
            irWirelessEventMapper.key = value
        }
        get() = irWirelessEventMapper.key

    override fun sendData(params: OcxParams): Flowable<Unit> = Flowable.fromCallable {
        synchronized(this) {
            val data = eventMapper.map(params) ?: return@fromCallable
            socket?.send(data.toString()) ?: throw ConnectException("is not connected.")
        }
    }

}