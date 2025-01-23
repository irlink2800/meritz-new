package com.irlink.meritz.di

import android.annotation.SuppressLint
import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.util.network.http.OkHttpInterceptor
import com.irlink.meritz.util.network.http.OkHttpUtil
import com.irlink.meritz.util.LogUtil
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.koin.core.KoinComponent
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object NetworkModule : KoinComponent {

    const val TAG: String = "NetworkModule"

    enum class Server(
        val tag: String,
        val url: String
    ) {
        SERVER("SERVER", "https://mjtm.meritzfire.com/"),
        NODE("NODE", "https://dtmcall.meritzfire.com:3000/"),
        REC("REC", "https://mjtmrec.meritzfire.com/"),
        ETC("ETC", "http://etc/")
    }

    private const val TIME_OUT: Long = 1000 * 60

    private const val CACHE_SIZE: Long = 10 * 1024 * 1024

    private val hostNameVerifier: HostnameVerifier = HostnameVerifier { _, _ ->
        true
    }

    private val trustManager: X509TrustManager = object : X509TrustManager {

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // ..
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // ..
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

    }

    private val sslSocketFactory: SSLSocketFactory = SSLContext.getInstance("TLS").run {
        init(null, arrayOf(trustManager), null)
        socketFactory
    }.apply {
        HttpsURLConnection.setDefaultHostnameVerifier(hostNameVerifier)
        HttpsURLConnection.setDefaultSSLSocketFactory(this)
    }

    @JvmStatic
    val INSTANCE: Module = module {
        single {
            OkHttpUtil()
        }
        single {
            OkHttpInterceptor()
        }
        single {
            createOkHttp(
                get<DirectoryManager>().okHttpDir,
                get()
            )
        }
        single(named(Server.SERVER.tag)) {
            createRetrofit(
                Server.SERVER.url,
                get(),
                get()
            )
        }
        single(named(Server.NODE.tag)) {
            createRetrofit(
                Server.NODE.url,
                get(),
                get()
            )
        }
        single(named(Server.REC.tag)) {
            createRetrofit(
                Server.REC.url,
                get(),
                get()
            )
        }
        single(named(Server.ETC.tag)) {
            createRetrofit(
                Server.ETC.url,
                get(),
                get()
            )
        }
    }

    @JvmStatic
    private fun createOkHttp(cacheDir: File, interceptor: OkHttpInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .hostnameVerifier(hostNameVerifier)
            .sslSocketFactory(sslSocketFactory, trustManager)
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .cache(Cache(cacheDir, CACHE_SIZE))
            .addInterceptor(interceptor)
            .build()

    @JvmStatic
    private fun createRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(okHttpClient)
        .build()

}