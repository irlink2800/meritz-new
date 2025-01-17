package com.irlink.meritz

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.irlink.meritz.di.AppModule
import com.irlink.meritz.util.notification.NotificationUtil
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject

class App : Application() {

    companion object : KoinComponent {
        const val TAG: String = "App"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    private val notificationUtil: NotificationUtil by inject()

    override fun onCreate() {
        super.onCreate()
        initApp()
        initKoin()
        initNotifications()
    }

    /**
     * 앱 초기화.
     */
    private fun initApp() {
        context = this
    }

    /**
     * 코인 초기화.
     */
    private fun initKoin() = startKoin {
        androidLogger()
        androidContext(this@App)
        fragmentFactory()
        koin.loadModules(AppModule.getModules())
        koin.createRootScope()
    }

    /**
     * 노티피케이션 초기화.
     */
    private fun initNotifications() = notificationUtil.init()

}
