package com.irlink.meritz.util.message

import com.irlink.meritz.util.base.model.Model


data class MessageHistory(

    override var id: Long?,

    val threadId: Long? = null,

    val type: Type,

    var body: String? = null,

    var senderNumber: String? = null,

    var readState: Int? = null,

    var timestamp: Long? = null

) : Model {

    enum class Type {
        SMS,
        MMS
    }

    object ReadState {
        const val READ: Int = 1
        const val UN_READ: Int = 0
    }

    val isRead: Boolean
        get() = readState == ReadState.READ

}