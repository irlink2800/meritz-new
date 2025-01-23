package com.irlink.meritz.observer.call


import android.content.Context
import android.os.Build
import android.widget.Toast
import com.irlink.meritz.R
import com.irlink.meritz.callstate.CallState
import com.irlink.meritz.callstate.CallStateListener
import com.irlink.meritz.manager.IrRecordManager
import com.irlink.meritz.ocx.OcxPreference
import com.irlink.meritz.record.Record
import com.irlink.meritz.record.RecordType
import com.irlink.meritz.util.FormatUtil
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.OuterActivities
import com.irlink.meritz.util.call.CallUtil
import com.irlink.meritz.util.extension.MySchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import com.irlink.meritz.callstate.CallType
import com.irlink.meritz.callstate.CallType.*
import com.irlink.meritz.util.call.CallHistory
import com.irlink.meritz.observer.call.consumer.CallStateConsumerImpl
import com.irlink.meritz.util.extension.wait
import java.util.*

class IrCallStateListener(

    applicationContext: Context,
    callUtil: CallUtil,
    formatUtil: FormatUtil,
    private val ocxPref: OcxPreference,
    private val irRecordManager: IrRecordManager,
    private val callStateConsumer: CallStateConsumerImpl,

    ) : CallStateListener(applicationContext, callUtil, formatUtil) {

    companion object {
        const val TAG: String = "IrCallStateListener"
    }

    /**
     * 통화 시작 이벤트.
     */
    private val _onCallStarted: PublishSubject<Pair<String, CallType>> = PublishSubject.create()
    val onCallStarted: Observable<Pair<String, CallType>> = _onCallStarted

    /**
     * 통화 연결 이벤트.
     */
    private val _onCallConnected: PublishSubject<Pair<String, CallType>> = PublishSubject.create()
    val onCallConnected: Observable<Pair<String, CallType>> = _onCallConnected

    /**
     * 통화 종료 이벤트.
     */
    private val _onCallEnded: PublishSubject<CallHistory> = PublishSubject.create()
    val onCallEnded: Observable<CallHistory> = _onCallEnded

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            irRecordManager.onRecordStart.observeForever {
                wait(2000) {
                    remoteNumbers.entries.firstOrNull() != null
                }
                val phoneNumber = remoteNumbers.entries.firstOrNull()?.key

                if (phoneNumber.isNullOrEmpty()) {
                    if (ocxPref.keepSetDialStr.isNotEmpty()) {
                        onCallStateChanged(CallState.CONNECTED.value, ocxPref.keepSetDialStr)
                    } else {
                        notifyCallError()
                    }
                } else {
                    onCallStateChanged(CallState.CONNECTED.value, phoneNumber)
                }
            }
        }
    }

    override fun onCallStarted(remoteNumber: String, callType: CallType) {
        LogUtil.d(TAG, "\n\n")
        LogUtil.d(TAG, "================================================")
        LogUtil.d(TAG, "=================== NEW CALL ===================")
        LogUtil.d(TAG, "================================================")
        LogUtil.d(TAG, "onCallStarted. remoteNumber: $remoteNumber, callType: $callType")

        this.ocxPref.isCallActive = true
        this._onCallStarted.onNext(remoteNumber to callType)

        val recordType: RecordType = when (callType) {
            OUTBOUND, OUTBOUND_SACALL -> RecordType.OUTBOUND
            INBOUND, INBOUND_SACALL -> RecordType.INBOUND
            UNKNOWN -> throw NullPointerException("is recordType error.")
        }
        val currentRecord: Record = irRecordManager.newRecord(
            type = recordType,
            remoteNumber = remoteNumber
        )
        this.irRecordManager.setIrRecorderFileName(
            recordFileName = currentRecord.initializeFileName
        )
        this.irRecordManager.startCallRecord()
        this.compositeDisposable += callStateConsumer.onCallStarted(currentRecord)
            .subscribeOn(MySchedulers.current())
            .observeOn(MySchedulers.current())
            .onBackpressureBuffer()
            .subscribeBy(
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
        if (recordType == RecordType.OUTBOUND) {
            if (ocxPref.keepSetDialStr == remoteNumber) {
                LogUtil.d(TAG, "clear keepSetDialStr: ${ocxPref.keepSetDialStr}")
                ocxPref.keepSetDialStr = ""
            }
        }
    }


    override fun onCallConnected(remoteNumber: String, callType: CallType) {
        LogUtil.d(TAG, "onCallConnected. remoteNumber: $remoteNumber, callType: $callType")
        _onCallConnected.onNext(remoteNumber to callType)

        val currentRecord: Record = irRecordManager.currentCallRecord ?: return
        this.irRecordManager.setRecordConnected(
            connectedDate = formatUtil.toDate(Date()),
            connectedTime = formatUtil.toTime(Date())
        )
        this.compositeDisposable += callStateConsumer.onCallConnected(currentRecord)
            .subscribeOn(MySchedulers.current())
            .observeOn(MySchedulers.current())
            .onBackpressureBuffer()
            .subscribeBy(
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
    }

    override fun onCallEnded(callHistory: CallHistory, callType: CallType, isMissedCall: Boolean) {
        LogUtil.d(
            TAG,
            "onCallEnded. callHistory: $callHistory, callType: $callType, isMissedCall: $isMissedCall"
        )

        // 통화 기록 삭제.
        callUtil.deleteCallHistories()

        val currentRecord: Record = irRecordManager.currentCallRecord ?: return

        this.irRecordManager.setRecordEnded(
            talkTime = (callHistory.duration ?: 0).toString(),
            callEndDate = formatUtil.toDate(Date()),
            callEndTime = formatUtil.toTime(Date())
        )

        this.irRecordManager.endCallRecord()
        this._onCallEnded.onNext(callHistory)

        this.compositeDisposable += callStateConsumer.onCallEnded(currentRecord, isMissedCall)
            .subscribeOn(MySchedulers.current())
            .observeOn(MySchedulers.current())
            .onBackpressureBuffer()
            .subscribeBy(
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
        this.ocxPref.isCallActive = false

        if (ocxPref.keepSetDialStr.isNotEmpty()) {
            LogUtil.d(TAG, "start keepSetDialStr: ${ocxPref.keepSetDialStr}")
            OuterActivities.intentCall(applicationContext, ocxPref.keepSetDialStr)
        }
    }

    override fun notifyCallError() {
        LogUtil.d(TAG, "notifyCallError.")
        Toast.makeText(
            applicationContext,
            R.string.error_occurred_re_call_required,
            Toast.LENGTH_LONG
        ).show()
    }

}