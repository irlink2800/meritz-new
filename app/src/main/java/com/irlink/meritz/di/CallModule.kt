package com.irlink.meritz.di

import com.irlink.meritz.manager.CallManager
import org.koin.core.module.Module
import org.koin.dsl.module

object CallModule {

    val INSTANCE: Module = module {
        single {
            CallManager(
                get(),
            )
        }
    }

}