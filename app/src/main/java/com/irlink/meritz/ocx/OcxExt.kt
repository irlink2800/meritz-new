package com.irlink.meritz.ocx

import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.extension.emptyDisposable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

private const val TAG: String = "OcxExt"

/**
 * String to Boolean.
 */
fun String.toOcxBoolean(isReverse: Boolean = false): Boolean = (this == OcxParams.TRUE).let {
    when (isReverse) {
        true -> !it
        else -> it
    }
}

/**
 * Boolean to String.
 */
fun Boolean.toOcxBoolean(isReverse: Boolean = false): String = when (isReverse) {
    true -> !this
    else -> this
}.let {
    when (it) {
        true -> OcxParams.TRUE
        else -> OcxParams.FALSE
    }
}

/**
 * File List to String Data
 */
fun List<File>.toOcxData(): String = StringBuilder().also {
    for (file: File in this) {
        if (!file.isFile) {
            continue
        }
        it.append(file.name)
        it.append(OcxParams.SUB_SEPARATOR)
    }
    if (it.isNotEmpty()) {
        it.deleteCharAt(it.length - 1)
    }
}.toString()

/**
 * Background 스레드 세팅.
 */
fun Flowable<Unit>.doBackground(): Flowable<Unit> = this
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.io())
    .onBackpressureBuffer()

/**
 * ocx event 실행 메소드.
 */
fun Flowable<Unit>?.ocxExecute(
    onNext: ((Unit) -> Unit) = {
        // empty.
    },
    onError: ((Throwable) -> Unit) = {
        LogUtil.exception(TAG, it)
    }
): Disposable = this
    ?.doBackground()
    ?.subscribeBy(
        onNext = onNext,
        onError = onError
    ) ?: emptyDisposable()