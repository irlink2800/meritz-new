package com.irlink.meritz.di

import com.irlink.meritz.observer.HeadSetPlugReceiver
import org.koin.core.module.Module
import org.koin.dsl.module

object ObserverModule {

    val INSTANCE: Module = module {
        single {
            HeadSetPlugReceiver()
        }
    }

}