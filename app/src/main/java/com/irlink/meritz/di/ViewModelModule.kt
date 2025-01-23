package com.irlink.meritz.di

import com.irlink.meritz.ui.screen.login.LoginViewModel
import com.irlink.meritz.ui.screen.main.MainViewModel
import com.irlink.meritz.ui.screen.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object ViewModelModule {

    @JvmStatic
    val INSTANCE: Module = module {
        viewModel {
            SplashViewModel()
        }

        viewModel {
            LoginViewModel(
                get(),
                get(),
                get(),
                get()
            )
        }

        viewModel {
            MainViewModel()
        }
    }
}