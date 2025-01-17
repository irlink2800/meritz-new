package com.irlink.meritz.observer.call.consumer

import com.irlink.meritz.record.Record
import io.reactivex.rxjava3.core.Flowable


class CallStateConsumerImpl(

    private val ocxConsumer: CallStateConsumer,

) : CallStateConsumer {

    companion object {
        const val TAG: String = "CallStateConsumerImpl"
    }

    /**
     * 콜 시작 시.
     */
    override fun onCallStarted(record: Record): Flowable<Unit> {
        return ocxConsumer.onCallStarted(record)
    }

    /**
     * 콜 연결 시.
     */
    override fun onCallConnected(record: Record): Flowable<Unit> {
        return ocxConsumer.onCallConnected(record)
    }

    /**
     * 콜 종료 시.
     */
    override fun onCallEnded(record: Record, isMissedCall: Boolean): Flowable<Unit> {
        return ocxConsumer.onCallEnded(record, isMissedCall)
    }
}