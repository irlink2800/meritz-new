package com.irlink.meritz.data.local.preference

import android.content.Context
import android.content.SharedPreferences
import com.irlink.meritz.data.remote.dto.User
import com.irlink.meritz.util.extension.clear
import com.irlink.meritz.util.extension.putString

class UserPreferenceImpl(

    applicationContext: Context

) : UserPreference {

    companion object {
        const val PREF_NAME = "user_pref"
    }

    object Key {
        const val AGENCY_NAME = "AGENCY_NAME"
        const val AGENCY_CODE = "AGENCY_CODE"
        const val ID = "ID"
        const val PASSWORD = "PASSWORD"
    }

    private val pref: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 저장된 사용자 정보를 가져온다.
     */
    override val user: User
        get() = User(
            agencyName = agencyName,
            agencyCode = agencyCode,
            id = id,
            password = password,
        )

    /**
     * 대리점명. (default = "")
     */
    override var agencyName: String
        get() = pref.getString(Key.AGENCY_NAME, "").toString()
        set(value) {
            pref.putString(Key.AGENCY_NAME, value)
        }

    /**
     * 대리점 코드. (default = "0")
     */
    override var agencyCode: String
        get() = pref.getString(Key.AGENCY_CODE, "").toString()
        set(value) {
            pref.putString(Key.AGENCY_CODE, value)
        }

    /**
     * 아이디. (default = "")
     */
    override var id: String
        get() = pref.getString(Key.ID, "").toString()
        set(value) {
            pref.putString(Key.ID, value)
        }

    /**
     * 비밀번호. (default = "")
     */
    override var password: String
        get() = pref.getString(Key.PASSWORD, "").toString()
        set(value) {
            pref.putString(Key.PASSWORD, value)
        }

    /**
     * 초기화.
     */
    override fun clear() = pref.clear()

}

interface UserPreference {

    val user: User

    var agencyName: String
    var agencyCode: String
    var id: String
    var password: String

    fun clear()
}