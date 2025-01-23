package com.irlink.meritz.domain.firebase

import com.irlink.meritz.manager.DeviceManager
import com.irlink.meritz.util.DeviceUtil
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.base.network.IrResponse
import com.irlink.meritz.util.base.network.UseCase
import com.irlink.meritz.util.extension.format
import com.irlink.meritz.util.firebase.RemoteDbManager
import com.irlink.meritz.util.firebase.write
import com.squareup.moshi.JsonClass
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Date

class UpdateFcmTokenUseCase(

    private val deviceManager: DeviceManager,
    private val deviceUtil: DeviceUtil,
    private val remoteDbManager: RemoteDbManager

) : UseCase<UpdateFcmTokenUseCase.Request, UpdateFcmTokenUseCase.Response>() {

    companion object {
        const val TAG: String = "UpdateFcmToken"
    }

    @JsonClass(generateAdapter = true)
    data class Request(
        val token: String
    )

    @JsonClass(generateAdapter = true)
    data class Response(
        override var code: Int? = Code.FAIL,
        override var message: String? = null
    ) : IrResponse(code, message)

    val phoneNumber: String
        get() = deviceUtil.phoneNumber

    override fun request(request: Request, onResponse: (Response) -> Unit): Disposable {
        val updateDate = Date() format "yyyy-MM-dd HH:mm:ss"
        val data = mapOf(
            "sabun" to deviceManager.sabun,
            "token" to request.token,
            "updateDate" to updateDate
        )
        return remoteDbManager.users.write(
            value = data,
            paths = listOf(phoneNumber)
        ).onBackpressureBuffer()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    onResponse(Response(code = IrResponse.Code.SUCCESS, message = "success."))
                },
                onError = {
                    LogUtil.exception(TAG, it)
                    onResponse(Response(message = it.message))
                }
            )
    }

}