package com.irlink.meritz.data.local.property

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BuildProperty(

    @Json(name = Field.CIPHER_KEY)
    val cipherKey: String,

    @Json(name = Field.CIPHER_ALGORITHM)
    val cipherAlgorithm: String,

    @Json(name = Field.TRANSFORMATION)
    val transformation: String

) {

    object Field {
        const val CIPHER_KEY: String = "cipher_key"
        const val CIPHER_ALGORITHM: String = "cipher_algorithm"
        const val TRANSFORMATION: String = "transformation"

        const val PRODUCTION: Int = 1
        const val DEVELOPMENT: Int = 2
    }

}