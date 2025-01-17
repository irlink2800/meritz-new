package com.irlink.meritz.repositories.repository

import com.irlink.meritz.data.local.entity.CallEntity
import io.reactivex.rxjava3.disposables.Disposable

interface CallRepository {

    fun insert(callHistory: CallEntity): Disposable

    fun update(callHistory: CallEntity): Disposable

}