package com.irlink.meritz.ocx.mapper

import com.irlink.meritz.ocx.OcxMethods
import org.json.JSONObject

abstract class OcxMethodMapper {

    companion object {
        const val TAG: String = "OcxMethodMapper"
    }

    protected abstract val ignoreMethods: Array<OcxMethods>

    abstract fun map(params: JSONObject): String?

}