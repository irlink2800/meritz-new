package com.irlink.meritz.ocx

class OcxParams(

    var command: OcxCommand? = null,
    var first: String? = null,
    var second: String? = null,
    var third: String? = null,
    var fourth: String? = null

) {

    companion object {
        const val END: String = ";"
        const val SEPARATOR: String = "||"
        const val SUB_SEPARATOR: String = "^"

        const val TRUE: String = "1"
        const val FALSE: String = "0"
    }

    object Index {
        const val COMMAND: Int = 0
        const val FIRST: Int = 1
        const val SECOND: Int = 2
        const val THIRD: Int = 3
        const val FOURTH: Int = 4
    }

    object SubIndex {
        const val FIRST: Int = 0
        const val SECOND: Int = 1
        const val THIRD: Int = 2
        const val FOURTH: Int = 3
        const val FIFTH: Int = 4
        const val SIXTH: Int = 5
        const val SEVENTH: Int = 6
        const val EIGHTH: Int = 7
    }

    val isAvailable: Boolean
        get() = command != null

    val data: String
        get() {
            var data = ""
            if (isAvailable) {
                data += this.command?.usb
            }
            data += "$SEPARATOR${first ?: ""}"
            data += "$SEPARATOR${second ?: ""}"
            data += "$SEPARATOR${third ?: ""}"
            data += "$SEPARATOR${fourth ?: ""}"
            return "$data$END"
        }

    val logData: String
        get() {
            var log: String = ""
            if (isAvailable) {
                log += this.command?.tag
            }
            if (!first.isNullOrEmpty()) {
                log += "$SEPARATOR$first"
            }
            if (!second.isNullOrEmpty()) {
                log += "$SEPARATOR$second"
            }
            if (!third.isNullOrEmpty()) {
                log += "$SEPARATOR$third"
            }
            if (!fourth.isNullOrEmpty()) {
                log += "$SEPARATOR$fourth"
            }
            return "$log$END"
        }

    constructor(ocxData: String?, type: OcxCommand.Type) : this(
        ocxData?.split(END).let {
            return@let when (it.isNullOrEmpty()) {
                true -> listOf()
                false -> it[0].split(SEPARATOR)
            }
        }, type
    )

    constructor(ocxData: List<String>, type: OcxCommand.Type) : this(
        command = ocxData.getOrNull(Index.COMMAND)?.takeIf {
            it.isNotEmpty()
        }?.toIntOrNull()?.let { code ->
            when (type) {
                OcxCommand.Type.EVENT -> code.toIrUsbEvent()
                OcxCommand.Type.METHOD -> code.toIrUsbMethod()
            }
        },
        first = ocxData.getOrNull(Index.FIRST),
        second = ocxData.getOrNull(Index.SECOND),
        third = ocxData.getOrNull(Index.THIRD),
        fourth = ocxData.getOrNull(Index.FOURTH)
    )
}