package com.irlink.meritz.data.remote

import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET
import retrofit2.http.Url

interface EtcApi {

    @GET
    fun generate204(
        @Url url: String = "http://clients3.google.com/generate_204"
    ): Flowable<String>

}