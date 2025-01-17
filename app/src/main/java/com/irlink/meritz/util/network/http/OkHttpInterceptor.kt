package com.irlink.meritz.util.network.http

import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.toUrlDecode
import okhttp3.*
import okhttp3.internal.Util
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject

class OkHttpInterceptor : Interceptor {

    companion object {
        const val TAG: String = "OkHttpInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // rRequest
        val request: Request = chain.request()
            .addHeaders()

        // response
        val response: Response = chain.proceed(request)
            .decodeBody()

        // print network.
        printNetworking(request, response)

        // return response.
        return response
    }

    private fun Request.addHeaders(vararg headers: Pair<String, String>): Request = newBuilder().apply {
        for (header in headers) {
            addHeader(header.first, header.second)
        }
    }.build()

    private fun Response.decodeBody(): Response = newBuilder().body(
        ResponseBody.create(
            body()?.contentType()?.toUTF8(),
            body()?.string().toUrlDecode()
        )
    ).build()

    private fun MediaType.toUTF8(): MediaType? = MediaType.parse(
        "${charset()};${Charsets.UTF_8}"
    )

    private fun printNetworking(request: Request, response: Response) {
        JSONObject().apply {
            put("url", request.url())
            put("method", request.method())
            put("status", response.code())
            put("isSuccessful", response.isSuccessful)
            put("message", response.message())
            put("header", request.headers().asJson)
            put("query", request.url().queryAsJson)
            put("body", request.body()?.asData)
            put("response", response.body()?.asData)

        }.let {
            LogUtil.d(TAG, it.toString(4))
        }
    }

    private val Headers.asJson: JSONObject
        get() = JSONObject().also {
            for (index in 0 until size()) {
                it.put(name(index), value(index))
            }
        }

    private val HttpUrl.queryAsJson: JSONObject
        get() = JSONObject().also {
            for (index in 0 until querySize()) {
                it.put(queryParameterName(index), queryParameterValue(index))
            }
        }

    private val RequestBody.asData: Any
        get() {
            val contentType = contentType().toString()
            return when {
                contentType.contains("multipart/form-data") -> asFormDataJson
                contentType.contains("application/json") -> asApplicationJson
                contentType.contains("application/x-www-form-urlencoded") -> asUrlEncodedJson
                else -> asString
            }
        }

    private val RequestBody.asString: String
        get() = Buffer().also { writeTo(it) }.readUtf8()

    private val RequestBody.asApplicationJson: JSONObject
        get() = JSONObject(asString)

    private val RequestBody.asUrlEncodedJson: JSONObject
        get() = JSONObject().also {
            val formBody = this as FormBody
            for (i: Int in 0 until formBody.size()) {
                it.put(formBody.encodedName(i), formBody.encodedValue(i))
            }
        }

    private val RequestBody.asFormDataJson: JSONObject
        get() = JSONObject().also {
            val multipartBody = this as MultipartBody
            for (part: MultipartBody.Part in multipartBody.parts()) {
                when (part.body().contentType()?.toString()) {
                    "multipart/form-data" -> {
                        val key: String = part.getKey()
                        val value: String = part.getFileName()

                        when (val array = it.optJSONArray(key)) {
                            null -> it.put(key, JSONArray().apply { put(value) })
                            else -> array.put(value)
                        }
                    }
                    else -> {
                        val key: String = part.getKey()
                        val value: String = part.body().asString

                        try {
                            it.put(key, JSONObject(value))
                        } catch (e: Exception) {
                            it.put(key, value)
                        }
                    }
                }
            }
        }

    private fun MultipartBody.Part.getKey(): String = headers()
        ?.value(0)
        ?.split(";")
        ?.get(1)
        ?.split("=")
        ?.get(1)
        ?.replace("\"", "")
        ?.trim() ?: ""

    private fun MultipartBody.Part.getFileName(): String = headers()
        ?.value(0)
        ?.split(";")
        ?.get(2)
        ?.split("=")
        ?.get(1)
        ?.replace("\"", "")
        ?.trim() ?: ""

    private val ResponseBody.asData: Any
        get() {
            val source = source().apply {
                request(Long.MAX_VALUE)
            }
            val buffer = source.buffer().clone()
            val charset = Util.bomAwareCharset(source, charset("UTF-8"))
            val body: String = buffer.readString(charset).toUrlDecode()

            return try {
                JSONObject(body)
            } catch (e: Exception) {
                body
            }
        }
}