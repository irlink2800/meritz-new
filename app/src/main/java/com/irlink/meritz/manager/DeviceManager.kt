package com.irlink.meritz.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DeviceManager {

    companion object {
        const val TAG: String = "DeviceManager"
    }

    /**
     * 로그인 상태 여부.
     */
    private val _isLogin: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isLogin: LiveData<Boolean> = _isLogin

    /**
     * 마지막 로그인 시간.
     */
    var lastLoginDate: String = ""

    /**
     * 사번.
     */
    var sabun: String = ""

    /**
     * 비밀번호.
     */
    var password: String = ""

    /**
     * 생년월일.
     */
    var birth: String = ""

    /**
     * 상담원 명.
     */
    var cnslrNm: String = ""

    /**
     * ?.
     */
    var cnslrOrgId: String = ""

    /**
     * 인콜 처리 여부.
     * 1: 받기 끊기 가능.
     * 2: 콜백 처리.
     */
    var inCallRciMdCd: String = ""

    /**
     * 관리자 구분 값.
     * "Admin": 관리자.
     * "": 일반.
     */
    var slzBizAthoCd: String = ""

    /**
     * 재전송 횟수.
     */
    var resendCnt: Int = 0

    /**
     * 녹취 저장 기간.
     */
    var rcdgCstdPrd: Int = 4

    /**
     * 잠금 화면 시간 (분)
     */
    var crpMchyScrnLckHr: Int = 3

    /**
     * 테스트 모드 설정.
     */
    fun setTestMode() {
        lastLoginDate = "2019-12-01"
        sabun = "119203776"
        password = ""
        birth = ""
        cnslrNm = "테스트"
        cnslrOrgId = "TEST"
        inCallRciMdCd = "1"
        slzBizAthoCd = "ADMIN"
        resendCnt = 3
        rcdgCstdPrd = 30
        crpMchyScrnLckHr = 7

        setLogin(true)
    }

    /**
     * 로그인 여부 설정.
     */
    fun setLogin(isLogin: Boolean) {
        _isLogin.value = isLogin
    }

}