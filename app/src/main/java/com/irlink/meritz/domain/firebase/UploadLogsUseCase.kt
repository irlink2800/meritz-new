package com.irlink.meritz.domain.firebase

import com.google.firebase.storage.UploadTask
import com.irlink.meritz.manager.DirectoryManager
import com.irlink.meritz.util.DeviceUtil
import com.irlink.meritz.util.FormatUtil
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.base.network.IrResponse
import com.irlink.meritz.util.base.network.UseCase
import com.irlink.meritz.util.extension.emptyDisposable
import com.irlink.meritz.util.extension.format
import com.irlink.meritz.util.firebase.RemoteStorageManager
import com.irlink.meritz.util.firebase.upload
import com.squareup.moshi.JsonClass
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.util.Date

class UploadLogsUseCase(

    private val deviceUtil: DeviceUtil,
    private val formatUtil: FormatUtil,
    private val directoryManager: DirectoryManager,
    private val remoteStorageManager: RemoteStorageManager

) : UseCase<UploadLogsUseCase.Request, UploadLogsUseCase.Response>() {

    companion object {
        const val TAG: String = "UploadLogsUseCase"
    }

    @JsonClass(generateAdapter = true)
    data class Request(
        val date: Date
    )

    @JsonClass(generateAdapter = true)
    data class Response(
        override var code: Int? = Code.FAIL,
        override var message: String? = null
    ) : IrResponse(code, message)

    private val phoneNumber: String
        get() = formatUtil.toLocalPhoneNumber(
            deviceUtil.phoneNumber, true
        )

    override fun request(request: Request, onResponse: (Response) -> Unit): Disposable {
        val strDate = request.date format "yyyyMMdd"
        val uploadFiles: MutableList<File> = mutableListOf()

        // 앱 로그 파일.
        uploadFiles += directoryManager.logDir.listFiles { _, name ->
            return@listFiles name.contains(strDate)
        } ?: arrayOf<File>()

        // 소켓 로그 파일.
        uploadFiles += directoryManager.socketLogDir.listFiles { _, name ->
            return@listFiles name.contains(strDate)
        } ?: arrayOf<File>()

        // 레코드 로그 파일.
        uploadFiles += directoryManager.recordLogDir.listFiles { _, name ->
            return@listFiles name.contains(strDate)
        } ?: arrayOf<File>()

        val uploadTask: MutableList<Flowable<UploadTask.TaskSnapshot>> = mutableListOf()
        for (file: File in uploadFiles) {
            uploadTask += remoteStorageManager.logStorage.upload(
                file, listOf(phoneNumber, request.date format "yyyy-MM-dd")
            )
        }
        if (uploadTask.isEmpty()) {
            onResponse(Response(message = "empty files."))
            return emptyDisposable()
        }
        return Flowable.zip(uploadTask) {
            return@zip it
        }.onBackpressureBuffer()
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