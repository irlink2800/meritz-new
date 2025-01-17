package com.irlink.meritz.di

import com.irlink.meritz.ocx.OcxManager
import com.irlink.meritz.ocx.OcxMode
import com.irlink.meritz.ocx.OcxPreference
import com.irlink.meritz.ocx.OcxPreferenceImpl
import com.irlink.meritz.ocx.wireless.WirelessPreference
import org.koin.core.KoinComponent
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

object OcxModule : KoinComponent {

    val ocxMode: OcxMode
        get() = OcxMode.WIRELESS

    val INSTANCE: Module = module {
        factory {
            ocxMode
        }

        single {
            OcxManager(
                get(),
                get(named(AppModule.Named.VERSION_CODE)),
                get(),
                get(),
                get<WirelessPreference>().apply {
                    serverUrl = NetworkModule.Server.NODE.url
                },
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }

        single {
            OcxPreferenceImpl(
                get(),
                get()
            )
        } bind OcxPreference::class
    }

}