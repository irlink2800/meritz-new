package com.irlink.meritz.manager

import com.irlink.meritz.data.local.entity.CallEntity
import com.irlink.meritz.repositories.repository.CallRepository
import com.irlink.meritz.util.LogUtil

class CallManager(
    private val callRepository: CallRepository
) {

    companion object {
        const val TAG: String = "CallHistoryManager"
    }

    /**
     * 현재 진행중인 통화 정보가 저장되는 객체.
     */
    var currentCall: CallEntity? = null
        private set

    /**
     * 새로운 통화 객체 생성 및 기본 값 초기화.
     */
    fun newCall(): CallEntity = CallEntity(
        sabun = "",
        callId = "",
        custId = "",
        custName = "",
        phoneNumber = "",
        callStartDate = "",
        talkStartDate = "",
        callEndDate = "",
        callDuration = 999,
        talkDuration = 999,
        inOutFlag = "",
        fileName = "",
        recSendFlag = "",
        msgBody = ""
    ).also { newCallEntity ->
        LogUtil.d(TAG, "newCall.")
        currentCall = newCallEntity
        callRepository.insert(newCallEntity)
    }

}