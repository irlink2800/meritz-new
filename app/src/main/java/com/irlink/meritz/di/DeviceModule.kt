package com.irlink.meritz.di

import com.irlink.meritz.manager.DeviceManager
import org.koin.core.module.Module
import org.koin.dsl.module

object DeviceModule {
    val INSTANCE: Module = module {
        single {
            DeviceManager()
        }
    }
}