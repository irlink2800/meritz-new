package com.irlink.meritz.observer.call.consumer

import com.irlink.meritz.record.Record
import io.reactivex.rxjava3.core.Flowable

interface CallStateConsumer {

    fun onCallStarted(record: Record): Flowable<Unit>

    fun onCallConnected(record: Record): Flowable<Unit>

    fun onCallEnded(record: Record, isMissedCall: Boolean): Flowable<Unit>

}