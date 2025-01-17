package com.irlink.meritz.repositories.converters

import androidx.room.TypeConverter
import java.io.File

class FileConverter {

    @TypeConverter
    fun toString(file: File?): String = file?.absolutePath ?: ""

    @TypeConverter
    fun fromString(path: String): File = File(path)
}