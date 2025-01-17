package com.irlink.meritz.util.extension

import android.content.SharedPreferences
import java.lang.Double.*

fun SharedPreferences.putInt(key: String, value: Int) {
    edit().putInt(key, value).apply()
}

fun SharedPreferences.putLong(key: String, value: Long) {
    edit().putLong(key, value).apply()
}

fun SharedPreferences.putFloat(key: String, value: Float) {
    edit().putFloat(key, value).apply()
}

fun SharedPreferences.putBoolean(key: String, value: Boolean) {
    edit().putBoolean(key, value).apply()
}

fun SharedPreferences.putString(key: String, value: String) {
    edit().putString(key, value).apply()
}

fun SharedPreferences.putStringSet(key: String, value: Set<String>) {
    edit().putStringSet(key, value).apply()
}

fun SharedPreferences.getDouble(key: String, default: Double) =
    longBitsToDouble(getLong(key, doubleToLongBits(default)))

fun SharedPreferences.putDouble(key: String, value: Double) =
    edit().putLong(key, doubleToRawLongBits(value)).apply()


fun SharedPreferences.clear() = edit().clear().apply()