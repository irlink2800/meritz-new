package com.irlink.meritz.di

import android.content.Context
import com.irlink.meritz.App
import com.irlink.meritz.BuildConfig
import com.irlink.meritz.R
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppModule {

    object Named {
        const val APP_ICON: String = "app_icon"
        const val PACKAGE_NAME: String = "package_name"
        const val VERSION_NAME: String = "version_name"
        const val VERSION_CODE: String = "version_code"
    }

    val INSTANCE: Module = module {
        single {
            get<Context>() as App
        }
        single(named(Named.PACKAGE_NAME)) {
            get<Context>().packageName
        }
        single(named(Named.VERSION_NAME)) {
            BuildConfig.VERSION_NAME
        }
        single(named(Named.VERSION_CODE)) {
            BuildConfig.VERSION_CODE
        }
        single(named(Named.APP_ICON)) {
            R.mipmap.ic_launcher
        }
    }

    fun getModules(): List<Module> = mutableListOf(
        INSTANCE,
        CallModule.INSTANCE,
        DataBaseModule.INSTANCE,
        DeviceModule.INSTANCE,
        NetworkModule.INSTANCE,
        ObserverModule.INSTANCE,
        OcxModule.INSTANCE,
        PreferenceModule.INSTANCE,
        UtilModule.INSTANCE,
        ViewModelModule.INSTANCE
    )

}