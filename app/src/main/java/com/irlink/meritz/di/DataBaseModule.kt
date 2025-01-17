package com.irlink.meritz.di

import android.text.Editable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.repositories.databases.MeritzDatabase
import com.irlink.meritz.repositories.repository.CallRepository
import com.irlink.meritz.repositories.repository.CallRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object DataBaseModule {

    /**
     * 데이터 베이스 파일명.
     */
    private const val DATABASE_NAME: String = "mobile-meritz-db.db"

    /**
     * 데이터 베이스 로컬 비밀번호.
     */
    private const val DATABASE_FACTORY_KEY: String = "test"

    const val TAG: String = "DataBaseModule"

    private val safeHelperFactory: SafeHelperFactory by lazy {
        SafeHelperFactory.fromUser(
            Editable.Factory.getInstance().newEditable(DATABASE_FACTORY_KEY)
        )
    }

    @JvmStatic
    val INSTANCE: Module = module {
        single {
            Room.databaseBuilder(
                get(),
                MeritzDatabase::class.java,
                "${get<DirectoryManager>().rootDir}/$DATABASE_NAME"
            ).setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4
                )
                .openHelperFactory(safeHelperFactory)
                .build()
        }

        single {
            get<MeritzDatabase>().callHistoryDao()
        }

        single {
            CallRepositoryImpl(
                get()
            )
        } bind CallRepository::class
    }

    private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE tb_call ADD COLUMN cust_name TEXT")
        }
    }

    private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE tb_call  ADD COLUMN call_type TEXT")
        }
    }

    private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE tb_call ADD COLUMN msg_body TEXT")
        }
    }
}