package com.irlink.meritz.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tb_config")
data class ConfigEntity(
    @PrimaryKey
    @ColumnInfo(name = "config_name")
    val configName: String,

    @ColumnInfo(name = "config_value")
    val configValue: String
)