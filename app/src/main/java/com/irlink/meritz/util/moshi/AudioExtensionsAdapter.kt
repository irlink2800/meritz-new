package com.irlink.meritz.util.moshi

import com.irlink.irrecorder.AudioExtensions
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class AudioExtensionsAdapter {

    object Value {
        const val AMR: String = "AMR"
        const val M4A: String = "M4A"
        const val WAV: String = "WAV"
        const val AAC: String = "AAC"
        const val FLAC: String = "FLAC"
        const val MP3: String = "MP3"
        const val OGG: String = "OGG"
        const val _3GP: String = "_3GP"
        const val UNKNOWN: String = "UNKNOWN"
    }

    @ToJson
    fun toJson(audioExtensions: AudioExtensions?): String = when (audioExtensions) {
        AudioExtensions.AMR -> Value.AMR
        AudioExtensions.M4A -> Value.M4A
        AudioExtensions.WAV -> Value.WAV
        AudioExtensions.AAC -> Value.AAC
        AudioExtensions.FLAC -> Value.FLAC
        AudioExtensions.MP3 -> Value.MP3
        AudioExtensions.OGG -> Value.OGG
        AudioExtensions._3GP -> Value._3GP
        else -> Value.UNKNOWN
    }

    @FromJson
    fun fromJson(audioExtensions: String?): AudioExtensions = when (audioExtensions) {
        Value.AMR -> AudioExtensions.AMR
        Value.M4A -> AudioExtensions.M4A
        Value.WAV -> AudioExtensions.WAV
        Value.AAC -> AudioExtensions.AAC
        Value.FLAC -> AudioExtensions.FLAC
        Value.MP3 -> AudioExtensions.MP3
        Value.OGG -> AudioExtensions.OGG
        else -> AudioExtensions._3GP
    }

}