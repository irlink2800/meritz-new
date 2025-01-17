package com.example

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response(@Json(name = "param1") val param1: String,
                    @field:Json(name = "MyValue") val myValue: String? = null,
                    @Json(name = "param3") val param3: String) {
    val param3Int
        get() = param3.toIntOrNull()
}
