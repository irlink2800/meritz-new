package com.irlink.meritz.di

import com.irlink.meritz.data.remote.EtcApi
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

object ApiModule {

    val INSTANCE: Module = module {
        single {
            createApi<EtcApi>(
                get(named(NetworkModule.Server.ETC.tag))
            )
        }
    }

    private inline fun <reified T> createApi(retrofit: Retrofit): T =
        retrofit.create(T::class.java)

}