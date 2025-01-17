package com.irlink.meritz.record

import android.content.Context
import android.content.SharedPreferences
import com.irlink.meritz.util.extension.fromJson
import com.irlink.meritz.util.extension.putString
import com.irlink.meritz.util.extension.toJson
import com.squareup.moshi.Moshi
import org.json.JSONObject

open class RecordPreferenceImpl(

    private val applicationContext: Context,
    private val moshi: Moshi

) : RecordPreference {

    companion object {
        const val PREF_NAME: String = "record_pref"
    }

    object Key {
        const val RECORDS: String = "records"
    }

    protected val pref: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Map 타입의 records 정보.
     */
    override var records: Map<String, Record>
        get() = jsonToRecords(recordsJson)
        set(value) {
            recordsJson = recordsToJson(value)
        }

    /**
     * Json(String) 타입의 records 정보.
     */
    override var recordsJson: String
        get() = pref.getString(Key.RECORDS, JSONObject().toString())!!
        set(value) {
            pref.putString(Key.RECORDS, value)
        }

    /**
     * records 객체를 Json(String)으로 리턴
     */
    private fun recordsToJson(records: Map<String, Record>): String = JSONObject().apply {
        for (entry: Map.Entry<String, Record> in records) {
            moshi.toJson(entry.value)?.let {
                put(entry.key, JSONObject(it))
            }
        }
    }.toString()

    /**
     * Json(String)으로 된 records 정보를 Map 구성해서 리턴
     */
    private fun jsonToRecords(json: String): Map<String, Record> = mutableMapOf<String, Record>().apply {
        val jsonObject: JSONObject = JSONObject(json)
        val keys: MutableIterator<String> = jsonObject.keys()

        for (key: String in keys) {
            moshi.fromJson<Record>(jsonObject.getJSONObject(key).toString())?.let {
                put(key, it)
            }
        }
    }
}

interface RecordPreference {

    var records: Map<String, Record>

    var recordsJson: String

}