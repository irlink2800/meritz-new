package com.irlink.meritz.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_call")
data class CallEntity(

    /**
     * 아이디.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * 사번.
     */
    @ColumnInfo(name = "sabun")
    var sabun: String,

    /**
     * 통화 아이디.
     */
    @ColumnInfo(name = "call_id")
    var callId: String,

    /**
     * 고객 아이디.
     */
    @ColumnInfo(name = "cust_id")
    var custId: String,

    /**
     * 고객명.
     */
    @ColumnInfo(name = "cust_name")
    var custName: String,

    /**
     *  고객 전화번호.
     */
    @ColumnInfo(name = "phone_no")
    var phoneNumber: String,

    /**
     * 통화 시작 날짜.
     */
    @ColumnInfo(name = "call_start_date")
    var callStartDate: String,

    /**
     * 통화 연결 날짜.
     */
    @ColumnInfo(name = "talk_start_date")
    var talkStartDate: String,

    /**
     * 통화 종료 날짜.
     */
    @ColumnInfo(name = "call_end_date")
    var callEndDate: String,

    /**
     * 통화 길이.
     */
    @ColumnInfo(name = "call_duration")
    var callDuration: Int,

    /**
     * 순수 통화 길이.
     */
    @ColumnInfo(name = "talk_duration")
    var talkDuration: Int,

    /**
     * 수&발신 여부.
     */
    @ColumnInfo(name = "in_out_flag")
    var inOutFlag: String,

    /**
     * 녹취 파일 명.
     */
    @ColumnInfo(name = "file_name")
    var fileName: String,

    /**
     * 녹취 전송 여부.
     */
    @ColumnInfo(name = "rec_send_flag")
    var recSendFlag: String,

    /**
     * 메시지.
     */
    @ColumnInfo(name = "msg_body")
    var msgBody: String
)
