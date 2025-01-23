package com.irlink.meritz.di

import com.irlink.meritz.domain.etc.GetGenerate204UseCase
import com.irlink.meritz.domain.firebase.UpdateFcmTokenUseCase
import com.irlink.meritz.domain.firebase.UploadLogsUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

object UseCaseModule {

    @JvmStatic
    val INSTANCE: Module = module {
        factory {
            UpdateFcmTokenUseCase(
                get(),
                get(),
                get()
            )
        }
        factory {
            UploadLogsUseCase(
                get(),
                get(),
                get(),
                get()
            )
        }
        factory {
            GetGenerate204UseCase(
                get()
            )
        }
    }

}