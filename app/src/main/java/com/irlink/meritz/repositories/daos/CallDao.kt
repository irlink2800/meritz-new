package com.irlink.meritz.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.irlink.meritz.data.local.entity.CallEntity
import io.reactivex.Completable
import io.reactivex.Single


@Dao
interface CallDao {

    /**
     * 녹취 내역 삽입.
     */
    @Insert
    fun insert(recordHistory: CallEntity): Single<Long>

    /**
     * 녹취 내역 업데이트.
     */
    @Update
    fun update(vararg recordHistory: CallEntity): Completable
}