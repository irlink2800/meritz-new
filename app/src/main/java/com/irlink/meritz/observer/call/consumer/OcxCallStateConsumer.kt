package com.irlink.meritz.observer.call.consumer

import com.irlink.irrecorder.IRRecorderv2
import com.irlink.meritz.manager.IrRecordManager
import com.irlink.meritz.ocx.OcxEvent
import com.irlink.meritz.ocx.OcxManager
import com.irlink.meritz.record.Record
import io.reactivex.rxjava3.core.Flowable
import com.irlink.meritz.callstate.CallType

class OcxCallStateConsumer(

    private val ocxManager: OcxManager,
    private val irRecordManager: IrRecordManager,

    ) : CallStateConsumer {

    companion object {
        const val TAG: String = "OcxCallStateConsumer"
    }

    private val ocxEvent: OcxEvent
        get() = ocxManager.event

    override fun onCallStarted(record: Record): Flowable<Unit> {
        return when (record.callType) {
            CallType.OUTBOUND, CallType.OUTBOUND_SACALL -> ocxEvent.outgoing(record)
                .flatMap {
                    return@flatMap ocxEvent.hookOff()
                }.flatMap {
                    return@flatMap ocxEvent.startCall(record)
                }.flatMap {
                    return@flatMap when (record.isRecord) {
                        true -> ocxEvent.startRecord(record)
                        else -> Flowable.just(Unit)
                    }
                }
            CallType.INBOUND, CallType.INBOUND_SACALL -> ocxEvent.bellOn()
                .flatMap {
                    return@flatMap ocxEvent.sendCidData(record)
                }
            CallType.UNKNOWN -> Flowable.just(Unit)
        }
    }

    override fun onCallConnected(record: Record): Flowable<Unit> = ocxEvent.hookOff()
        .flatMap {
            return@flatMap ocxEvent.bellOff()
        }.flatMap {
            return@flatMap ocxEvent.startCall(record)
        }.flatMap {
            return@flatMap when (record.isRecord) {
                true -> ocxEvent.startRecord(record)
                else -> Flowable.just(Unit)
            }
        }

    override fun onCallEnded(record: Record, isMissedInbound: Boolean): Flowable<Unit> {
        if (record.isPartialRecord && IRRecorderv2.isACRRecordSet()) irRecordManager.endPartialRecord(
            record
        ) {
            record.partialRecord()?.let { partialRecord ->
                irRecordManager.addRecord(partialRecord)
                irRecordManager.uploadRecord(partialRecord)
            }
        }
        return ocxEvent.hookOn()
            .flatMap {
                return@flatMap when {
                    isMissedInbound -> ocxEvent.bellOff()
                    else -> Flowable.just(Unit)
                }
            }.flatMap {
                return@flatMap ocxEvent.endCall(record)
            }.flatMap {
                return@flatMap when (record.isStartedRecord) {
                    true -> ocxEvent.endRecord(record)
                    else -> Flowable.just(Unit)
                }
            }
    }
}