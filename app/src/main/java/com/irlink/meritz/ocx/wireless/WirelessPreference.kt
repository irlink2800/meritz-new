package com.irlink.meritz.ocx.wireless

import android.content.Context
import android.content.SharedPreferences
import com.irlink.meritz.util.extension.putString

class WirelessPreferenceImpl(

    applicationContext: Context

) : WirelessPreference {

    companion object {
        const val PREF_NAME = "wireless_pref"
    }

    object Key {
        const val SERVER_URL = "SERVER_URL"
    }

    private val pref: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override var serverUrl: String
        get() = pref.getString(Key.SERVER_URL, "") ?: ""
        set(value) {
            if (value.isEmpty()) return
            pref.putString(Key.SERVER_URL, value.let {
                if (it.endsWith("/")) it else "$it/"
            })
        }

}

interface WirelessPreference {
    var serverUrl: String
}