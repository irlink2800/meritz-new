package com.irlink.meritz.util.network.socket

import java.io.InputStream
import java.io.OutputStream
import java.net.SocketException

const val READ_END: Int = -1

/**
 * 읽을 스트림이 있는 경우 true.
 */
fun InputStream?.isAvailable(): Boolean = this?.available() ?: READ_END > 0

/**
 * 연결이 끊긴 상태면 true.
 */
fun InputStream?.isReadEnd(): Boolean = this?.read() ?: READ_END == READ_END

/**
 * 데이터를 전부 읽어서 String 타입으로 리턴.
 */
fun InputStream.readAll(): String? {
    try {
        val read: Int = read()
        if (read == READ_END) {
            return null
        }
        val bytes: ByteArray = ByteArray(available()).also {
            read(it)
        }
        return "${read.toChar()}${bytes.toString(Charsets.UTF_8)}"

    } catch (e: SocketException) {
        return null
    }
}

/**
 * String 타입으로 쓰기.
 */
fun OutputStream?.write(string: String, isFlush: Boolean = true) {
    if (this == null) {
        return
    }
    write(string.toByteArray())
    if (isFlush) {
        flush()
    }
}