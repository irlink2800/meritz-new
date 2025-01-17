package com.irlink.meritz.ocx.mapper

import com.irlink.meritz.ocx.OcxEvents
import com.irlink.meritz.ocx.OcxParams
import org.json.JSONObject

abstract class OcxEventMapper {

    companion object {
        const val TAG: String = "OcxEventMapper"
    }

    protected abstract val ignoreEvents: Array<OcxEvents>

    abstract fun map(params: OcxParams): JSONObject?

    protected abstract fun printData(data: JSONObject)

}