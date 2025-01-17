package com.irlink.meritz.util.extension

fun wait(millis: Long, condition: () -> Boolean = { false }) {
    val startMillis = System.currentTimeMillis()
    while (true) {
        val now = System.currentTimeMillis()
        if (now - startMillis >= millis) {
            break
        }
        if (!condition()) continue
        break
    }
}