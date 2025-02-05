package com.irlink.meritz.util.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.io.File

class FileAdapter {

    @ToJson
    fun toJson(file: File?): String = file?.path ?: ""

    @FromJson
    fun fromJson(path: String?): File = File(path ?: "")

}