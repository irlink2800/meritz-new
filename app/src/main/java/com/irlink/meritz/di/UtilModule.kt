package com.irlink.meritz.di

import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.manager.IrDirectoryManager
import com.irlink.meritz.util.*
import com.irlink.meritz.util.call.CallUtil
import com.irlink.meritz.util.contact.ContactUtil
import com.irlink.meritz.util.message.MessageUtil
import com.irlink.meritz.util.notification.NotificationUtil
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object UtilModule {

    val INSTANCE: Module = module {

        single {
            FormatUtil()
        }

        single {
            FileUtil()
        }

        single {
            IrDirectoryManager(
                get(),
                get(),
                get()
            )
        } bind DirectoryManager::class

        single {
            BatteryUtil(
                get()
            )
        }

        single {
            NotificationUtil(
                get()
            )
        }

        single {
            DeviceUtil(
                get(),
                get()
            )
        }

        single {
            CallUtil(
                get()
            )
        }

        single {
            RegexUtil()
        }

        single {
            DisplayUtil(
                get()
            )
        }

        single {
            WindowUtil(
                get(),
                get()
            )
        }

        single {
            AudioUtil(
                get()
            )
        }

        single {
            ContactUtil(
                get(),
                get(),
                get()
            )
        }

        single {
            MessageUtil(
                get()
            )
        }

        single {
            PermissionUtil(
                get(),
                get(),
                get()
            )
        }

        single {
            ResourceProviderImpl(
                get()
            )
        } bind ResourceProvider::class

        single {
            AudioFileUtil()
        }

    }

}