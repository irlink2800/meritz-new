package com.irlink.meritz.callstate

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.irlink.irrecorder.detect.RcsDetector
import com.irlink.meritz.util.FormatUtil
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.call.CallUtil
import com.irlink.meritz.util.extension.wait
import io.reactivex.rxjava3.disposables.CompositeDisposable
import com.irlink.meritz.util.call.CallHistory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class CallStateListener(

    protected val applicationContext: Context,
    protected val callUtil: CallUtil,
    protected val formatUtil: FormatUtil,

    ) : PhoneStateListener() {

    companion object {
        const val TAG: String = "CallStateListener"
    }

    /**
     * 현재 상태의 콜 스테이트.
     */
    @Volatile
    var currentCallState: CallState = CallState.IDLE
        @Synchronized get
        @Synchronized private set

    /**
     * 이전 상태의 콜 스테이트.
     */
    @Volatile
    var prevCallState: CallState = CallState.IDLE
        @Synchronized get
        @Synchronized private set

    /**
     * 현재 통화의 타입.
     */
    @Volatile
    var currentCallType: CallType = CallType.UNKNOWN
        @Synchronized get
        @Synchronized private set

    /**
     * CallStateListener 등록을 위한 TelephonyManager.
     */
    protected val telephonyManager: TelephonyManager by lazy {
        applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    /**
     * 현재 통화중인 상대방의 번호 / 캐치콜 여부 맵.
     */
    val remoteNumbers: ConcurrentHashMap<String, Boolean> by lazy {
        ConcurrentHashMap<String, Boolean>()
    }

    /**
     * 호출 시점에 통화중인 번호가 있는 경우.
     */
    private val isAlreadyCall: Boolean
        get() = remoteNumbers.isNotEmpty()


    /**
     * RCS Detector.
     * 전화 연결 시점 (Call Connected)를 감지하기 위해 사용.
     */
    protected val rcsDetector: RcsDetector by lazy {
        object : RcsDetector() {
            override fun onOffHook(phoneNumber: String?) {
                onCallStateChanged(CallState.CONNECTED.value, phoneNumber)
            }
        }
    }

    /**
     * CallState 변경 처리 스레드.
     */
    protected val callStateExecutor: Executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    /**
     * disposable 관리를 위한 CompositeDisposable.
     */
    protected val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    /**
     * 통화 기록의 개수이며, 전화 시작과 종료때 갱신한다.
     * onIdle 직후 통화 기록을 검색하면 아직 생성되지 않은 경우가 있기 때문에 개수 비교를 위해 사용됨.
     */
    @Volatile
    private var lastCallHistoryId: Long? = null

    /**
     * 정상 콜 하나가 진행되는 동안 발생한 캐치콜의 수.
     */
    @Volatile
    var catchCallCount: Int = 0

    /**
     * 리스너 등록.
     * 아웃 바운드 시, 상대방의 전화번호를 얻기 위해 리시버 또는 서비스를 추가로 등록한다.
     */
    open fun register() {
        LogUtil.d(TAG, "register.")
        telephonyManager.listen(this, LISTEN_CALL_STATE)
        applicationContext.registerReceiver(rcsDetector, RcsDetector.createIntentFilter())
    }

    /**
     * 리스너 해제.
     * 등록할 때 추가로 등록했던 리시버 또는 서비스를 함께 해제한다.
     */
    open fun unregister() {
        LogUtil.d(TAG, "unregister.")
        telephonyManager.listen(this, LISTEN_NONE)
        applicationContext.unregisterReceiver(rcsDetector)
        clear()
    }

    /**
     * 디바이스의 통화 상태값이 변경되면 호출되며, 이 값을 통해 각 상태에 맞는 메소드로 매핑함.
     */
    final override fun onCallStateChanged(state: Int, phoneNumber: String?) =
        callStateExecutor.execute {
            if (prevCallState.value == state) {
                return@execute
            }
            currentCallState = state.toCallState()

            val localPhoneNumber = formatUtil.toLocalPhoneNumber(phoneNumber)

            LogUtil.d(
                TAG,
                "onCallStateChanged.${currentCallState.tag}, localPhoneNumber: $localPhoneNumber"
            )

            if (localPhoneNumber.isNotEmpty()) when (currentCallState) {
                CallState.RINGING -> {
                    currentCallType = CallType.INBOUND
                    startCall(localPhoneNumber)
                }

                CallState.OFFHOOK -> if (prevCallState != CallState.CONNECTED) {
                    currentCallType = CallType.OUTBOUND
                    startCall(localPhoneNumber)
                } else {
                    return@execute
                }

                CallState.CONNECTED -> {
                    connectCall(localPhoneNumber)
                }

                CallState.IDLE -> {
                    endCall(localPhoneNumber)
                }

                CallState.ERROR -> {
                    return@execute
                }
            }
            prevCallState = currentCallState
        }

    /**
     * 전화가 시작되면 호출. (아웃바운드: OffHook, 인바운드: Ringing)
     */
    private fun startCall(remoteNumber: String) {
        addRemoteNumber(remoteNumber)
        catchCallCount = 0
        lastCallHistoryId = callUtil.getLastCallHistory().id

        onCallStarted(remoteNumber, currentCallType)
    }

    /**
     * startCall이 완료되면 호출.
     */
    abstract fun onCallStarted(remoteNumber: String, callType: CallType)

    /**
     * 전화가 연결되면 호출. (OffHook)
     */
    private fun connectCall(remoteNumber: String) {
        onCallConnected(remoteNumber, currentCallType)
    }

    /**
     * connectCall이 완료되면 호출.
     */
    abstract fun onCallConnected(remoteNumber: String, callType: CallType)

    /**
     * 전화가 종료되면 호출. (Idle)
     */
    private fun endCall(phoneNumber: String) {
        var lastCallHistory: CallHistory? = null

        // 최대 3초간 통화 이력이 저장될 때 까지 대기.
        wait(3_000) {
            lastCallHistory = callUtil.getLastCallHistory(catchCallCount)
            lastCallHistory?.id != lastCallHistoryId
        }
        onCallEnded(
            lastCallHistory ?: CallHistory(
                duration = 0,
                remoteNumber = phoneNumber
            ),
            currentCallType,
            when (currentCallType) {
                CallType.INBOUND, CallType.INBOUND_SACALL -> {
                    prevCallState == CallState.RINGING
                }

                CallType.OUTBOUND, CallType.OUTBOUND_SACALL -> {
                    prevCallState == CallState.OFFHOOK
                }

                else -> true
            }
        )
        // 통화가 종료되었으므로 remoteNumbers에서 삭제.
        removeRemoteNumber(phoneNumber)

        // 콜 타입 초기화
        currentCallType = CallType.UNKNOWN
    }

    /**
     * endCall이 완료되면 호출.
     */
    abstract fun onCallEnded(callHistory: CallHistory, callType: CallType, isMissedCall: Boolean)

    /**
     * 캐치콜이거나, 비정상적인 동작이 발생하면 true.
     */
    private fun String?.isCatchCall(): Boolean = when (isNullOrEmpty()) {
        true -> true
        else -> remoteNumbers[this] ?: true
    }

    /**
     * 상대방 번호를 맵에 추가.
     * 이미 통화중인 상태에서 걸려온 전화는 캐치콜로 판단하여 true로 저장.
     */
    private fun addRemoteNumber(remoteNumber: String) {
        remoteNumbers += remoteNumber to isAlreadyCall
        LogUtil.d(TAG, "addRemoteNumber. remoteNumbers: $remoteNumbers")
    }

    /**
     * 맵 상대방 번호를 삭제.
     * 통화가 종료된 상대의 번호를 지울 때 사용.
     */
    private fun removeRemoteNumber(remoteNumber: String?) {
        if (remoteNumber.isNullOrEmpty()) {
            return
        }
        remoteNumbers.remove(remoteNumber)
        LogUtil.d(TAG, "removeRemoteNumber. remoteNumbers: $remoteNumbers")
    }

    /**
     * 캐치콜 발생 이벤트.
     */
    open fun onRingCatchCall(remoteNumber: String) {
        LogUtil.d(TAG, "onRingCatchCall. remoteNumber: $remoteNumber")
        catchCallCount++
        if (callUtil.disconnectCall()) {
            endCall(remoteNumber)
        }
    }

    /**
     * clear.
     */
    open fun clear() {
        LogUtil.d(TAG, "clear.")
        remoteNumbers.clear()
        compositeDisposable.clear()
    }

    abstract fun notifyCallError()

}