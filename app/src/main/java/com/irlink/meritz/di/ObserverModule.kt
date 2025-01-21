package com.irlink.meritz.di

import com.irlink.meritz.observer.HeadSetPlugReceiver
import com.irlink.meritz.observer.VolumeObserver
import org.koin.core.module.Module
import org.koin.dsl.module

object ObserverModule {

    val INSTANCE: Module = module {
        single {
            HeadSetPlugReceiver()
        }

        single {
            VolumeObserver(
                get(),
                get()
            )
        }
    }

}