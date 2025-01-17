package com.irlink.meritz.util.base.network

import io.reactivex.rxjava3.disposables.Disposable

abstract class UseCase<Request, Response> {

    abstract fun request(
        request: Request,
        onResponse: (Response) -> Unit
    ): Disposable

}