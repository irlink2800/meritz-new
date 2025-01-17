package com.irlink.meritz.ui.screen.login

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.meritz.BuildConfig
import com.irlink.meritz.Constant
import com.irlink.meritz.R
import com.irlink.meritz.manager.DeviceManager
import com.irlink.meritz.ui.base.viewmodel.BaseViewModel
import com.irlink.meritz.util.DeviceUtil
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.ResourceProvider
import com.irlink.meritz.util.base.livedata.EmptyEvent
import com.irlink.meritz.util.extension.notify

class LoginViewModel(

    private val deviceUtil: DeviceUtil,
    private val deviceManager: DeviceManager,
    private val resourceProvider: ResourceProvider

) : BaseViewModel() {

    companion object {
        const val TAG: String = "LoginViewModel"
    }

    /**
     * 앱 버전 텍스트.
     */
    val appVersionText: LiveData<String> =
        MutableLiveData("v${BuildConfig.VERSION_NAME}")

    /**
     * 안드로이드 os 버전 텍스트.
     */
    val osVersionText: LiveData<String> =
        MutableLiveData("${resourceProvider.getString(R.string.os_version)}: ${deviceUtil.os}")

    /**
     * 전화 모드 텍스트
     */
    val callModeText: LiveData<String> =
        MutableLiveData("${resourceProvider.getString(R.string.call_mode)}: ${deviceUtil.deviceCallApp}")

    /**
     * 디바이스 아이디 텍스트.
     */
    val deviceIdText: LiveData<String> =
        MutableLiveData("${resourceProvider.getString(R.string.device_id)}: ${deviceUtil.deviceId}")

    /**
     * 로그인 여부.
     */
    val isLogin: LiveData<Boolean> = deviceManager.isLogin

    /**
     * 사용자 아이디.
     */
    val userId: MutableLiveData<String> = MutableLiveData()

    /**
     * 사용자 비밀번호.
     */
    val userPassword: MutableLiveData<String> = MutableLiveData()

    /**
     * 사용자 생년월일.
     */
    val userBirth: MutableLiveData<String> = MutableLiveData()

    /**
     * Meritz 시작.
     */
    private val _startService: MutableLiveData<EmptyEvent> = MutableLiveData()
    val startService: MutableLiveData<EmptyEvent> = _startService

    /**
     * Meritz 종료.
     */
    private val _stopService: MutableLiveData<EmptyEvent> = MutableLiveData()
    val stopService: MutableLiveData<EmptyEvent> = _stopService

    /**
     * 사번 입력 요청 다이얼로그.
     */
    private val _showInputUserIdDialog: MutableLiveData<EmptyEvent> = MutableLiveData()
    val showInputUserIdDialog: LiveData<EmptyEvent> = _showInputUserIdDialog

    /**
     * 비밀번호 입력 요청 다이얼로그.
     */
    private val _showInputPasswordDialog: MutableLiveData<EmptyEvent> = MutableLiveData()
    val showInputPasswordDialog: LiveData<EmptyEvent> = _showInputPasswordDialog

    /**
     * 생년월일 입력 요청 다이얼로그.
     */
    private val _showInputBirthDialog: MutableLiveData<EmptyEvent> = MutableLiveData()
    val showInputBirthDialog: LiveData<EmptyEvent> = _showInputBirthDialog

    /**
     * boot sleep dialog.
     */
    private val _showBootSleepDialog: MutableLiveData<EmptyEvent> = MutableLiveData()
    val showBootSleepDialog: LiveData<EmptyEvent> = _showBootSleepDialog

    /**
     * 초기화.
     */
    fun init() {
        LogUtil.d(TAG, "init.")
        startService()
    }

    /**
     * 로그인.
     */
    fun login() {
        if (Constant.IS_TEST_MODE) {
            deviceManager.setTestMode()
        } else {
            loginProcess()
        }
    }

    /**
     * MeritzService 시작.
     */
    fun startService() {
        _startService.notify()
    }

    /**
     * MeritzService 종료.
     */
    fun stopService() {
        _stopService.notify()
    }

    /**
     * 사번 입력 요청 다이얼로그.
     */
    fun showInputUserIdDialog() = _showInputUserIdDialog.notify()

    /**
     * 비밀번호 입력 요청 다이얼로그.
     */
    fun showInputPasswordDialog() = _showInputPasswordDialog.notify()

    /**
     * 생년월일 입력 요청 다이얼로그.
     */
    fun showInputBirthDialog() = _showInputBirthDialog.notify()

    /**
     * boot sleep dialog.
     */
    fun showBootSleepDialog() = _showBootSleepDialog.notify()

    /**
     * 로그인 프로세스
     */
    private fun loginProcess() {
        // 사번 입력 검사.
        if (userId.value.isNullOrEmpty() || userId.value?.length != 9) {
            showInputUserIdDialog()
            return
        }
        // 비밀번호 입력 검사.
        if (userPassword.value.isNullOrEmpty() || userPassword.value?.length == 0) {
            showInputPasswordDialog()
            return
        }
        // 생년월일 입력 검사.
        if (userBirth.value.isNullOrEmpty() || userBirth.value?.length != 6) {
            showInputBirthDialog()
            return
        }
        // 폰 부팅 후, 일정시간 동안 OFFHOOK EVENT가 늦는 현상으로 200초 delay.
        try {
            val bootSleepTime = 200_000L
            val sleepTime = bootSleepTime - SystemClock.elapsedRealtime()
            if (sleepTime > 0) {
                LogUtil.d(TAG, "loginProcess. sleepTime: $sleepTime")
                showBootSleepDialog()
                return
            }
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
        }
        deviceManager.setLogin(true)
    }

}