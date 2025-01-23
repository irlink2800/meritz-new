package com.irlink.meritz.domain.etc

import com.irlink.meritz.data.remote.EtcApi
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.base.network.UseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class GetGenerate204UseCase(

    private val etcApi: EtcApi

) : UseCase<Unit, String>() {

    companion object {
        const val TAG: String = "GetGenerate204UseCase"
    }

    override fun request(request: Unit, onResponse: (String) -> Unit): Disposable =
        etcApi.generate204()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onBackpressureBuffer()
            .subscribeBy(
                onNext = {
                    onResponse(it)
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )

}