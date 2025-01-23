package com.irlink.meritz.di

import com.irlink.meritz.manager.IrRecordManager
import com.irlink.meritz.record.RecordManager
import com.irlink.meritz.record.RecordPreference
import com.irlink.meritz.record.RecordPreferenceImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object RecordModule {

    val INSTANCE: Module = module {
        single {
            IrRecordManager(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        } bind RecordManager::class
        single {
            RecordPreferenceImpl(
                get(),
                get()
            )
        } bind RecordPreference::class
    }

}