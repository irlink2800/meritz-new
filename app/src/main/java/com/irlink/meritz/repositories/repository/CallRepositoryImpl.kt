package com.irlink.meritz.repositories.repository

import com.irlink.meritz.data.local.entity.CallEntity
import com.irlink.meritz.repositories.daos.CallDao
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.extension.toV3
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class CallRepositoryImpl(

    private val callHistoryDao: CallDao

) : CallRepository {

    companion object {
        const val TAG: String = "CallHistoryRepositoryImpl"
    }

    override fun insert(callHistory: CallEntity): Disposable =
        callHistoryDao.insert(callHistory)
            .toV3()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { id ->
                    LogUtil.d(TAG, "insert. success. id: $id")
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )

    override fun update(callHistory: CallEntity): Disposable {
        TODO("Not yet implemented")
    }
}