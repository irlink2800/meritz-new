package com.irlink.meritz.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.meritz.R
import com.irlink.meritz.data.remote.MeritzServerApi
import com.irlink.meritz.data.remote.dto.User
import com.irlink.meritz.util.base.network.UseCase
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.ResourceProvider
import com.irlink.meritz.util.base.network.IrResponse
import com.irlink.meritz.util.extension.emptyDisposable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginUseCase(

    private val meritzServerApi: MeritzServerApi,
    private val resourceProvider: ResourceProvider

) : UseCase<LoginUseCase.Request, LoginUseCase.Response>() {
    companion object {
        const val TAG: String = "LoginUseCase"
    }

    /**
     * 사용자 정보.
     */
    val user: MediatorLiveData<User> = MediatorLiveData<User>()

    /**
     * 로그인 상태 여부.
     */
    private val _isLogin: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isLogin: LiveData<Boolean> = _isLogin

    /**
     * 사용자 로그인 정보 유효성 체크.
     */
    private fun validationUserInfo(user: User): Pair<Boolean, String> {
        if (user.id.isNullOrEmpty()) {
            return false to resourceProvider.getString(R.string.plz_input_sabun)
        }
        if (user.password.isNullOrEmpty()) {
            return false to resourceProvider.getString(R.string.plz_input_password)
        }
        if (user.birth.isNullOrEmpty()) {
            return false to resourceProvider.getString(R.string.plz_input_birth_date)
        }
        return true to ""
    }

    override fun request(request: Request, onResponse: (Response) -> Unit): Disposable {
        val resultValidation = validationUserInfo(request.user)
        if (!resultValidation.first) {
            val response = Response(
                code = IrResponse.Code.FAIL,
                message = resultValidation.second,
                isLogin = false
            )
            onResponse(response)
            return emptyDisposable()
        }
        return meritzServerApi.doLogin(
            uid = request.user.id ?: "",
            pwd = request.user.password ?: ""
        ).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
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

    data class Request(
        var user: User
    )

    @JsonClass(generateAdapter = true)
    data class Response(

        override var code: Int? = Code.FAIL,

        override var message: String? = null,

        /**
         * 로그인 여부.
         */
        val isLogin: Boolean,

        @Json(name = Field.MEAP_LOGIN_RES_CD)
        var meapLoginResCd: String? = null

    ) : IrResponse(code, message) {

        object Field {
            const val MEAP_LOGIN_RES_CD = "meapLoginResCd"
        }
    }

}