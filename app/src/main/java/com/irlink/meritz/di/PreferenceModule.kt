package com.irlink.meritz.di

import com.irlink.meritz.ocx.wireless.WirelessPreference
import com.irlink.meritz.ocx.wireless.WirelessPreferenceImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object PreferenceModule {

    val INSTANCE: Module = module {
        single {
            WirelessPreferenceImpl(
                get()
            )
        } bind WirelessPreference::class

    }

}