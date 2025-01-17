package com.irlink.meritz.ocx

interface OcxCommand {

    companion object {
        const val NONE: Int = -1
    }

    enum class Type {
        METHOD,
        EVENT
    }

    val usb: Int

    val wireless: String?

    val tag: String

}
