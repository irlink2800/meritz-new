package com.irlink.meritz.repositories.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.irlink.meritz.data.local.entity.CallEntity
import com.irlink.meritz.data.local.entity.ConfigEntity
import com.irlink.meritz.repositories.daos.CallDao

@Database(entities = [CallEntity::class, ConfigEntity::class], exportSchema = false, version = 4)
abstract class MeritzDatabase : RoomDatabase() {
    abstract fun callHistoryDao(): CallDao
}