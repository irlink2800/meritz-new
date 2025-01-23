package com.irlink.meritz.util.moshi

import com.irlink.meritz.util.message.MessageType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class MessageTypeAdapter {

    object Value {
        const val RECEIVE: String = "RECEIVE"
        const val SEND: String = "SEND"
        const val UNKNOWN: String = "UNKNOWN"
    }

    @ToJson
    fun toJson(messageType: MessageType?): String = when (messageType) {
        MessageType.RECEIVE -> Value.RECEIVE
        MessageType.SEND -> Value.SEND
        else -> Value.UNKNOWN
    }

    @FromJson
    fun fromJson(messageType: String?): MessageType = when (messageType) {
        Value.RECEIVE -> MessageType.RECEIVE
        Value.SEND -> MessageType.SEND
        else -> MessageType.UNKNOWN
    }

}