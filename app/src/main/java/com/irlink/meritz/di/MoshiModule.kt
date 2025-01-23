package com.irlink.meritz.di

import com.irlink.meritz.util.moshi.AudioExtensionsAdapter
import com.irlink.meritz.util.moshi.FileAdapter
import com.irlink.meritz.util.moshi.MessageTypeAdapter
import com.irlink.meritz.util.moshi.RecordTypeAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.core.module.Module
import org.koin.dsl.module

object MoshiModule {

    @JvmStatic
    val INSTANCE: Module = module {
        single {
            createMoshi()
        }
    }

    @JvmStatic
    private fun createMoshi(): Moshi = Moshi.Builder()
        .add(FileAdapter())
        .add(RecordTypeAdapter())
        .add(MessageTypeAdapter())
        .add(AudioExtensionsAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

}