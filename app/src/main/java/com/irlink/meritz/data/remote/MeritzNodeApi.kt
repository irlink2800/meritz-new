package com.irlink.meritz.data.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MeritzNodeApi {

    companion object {
        private const val SYS_DIV_CD: String = "TM"

        private const val SERVICE_ID_HANDLECRPMCHYUSEATH: String = "FTMTMSOO0096"
    }

    /**
     * 법인폰 사용 인증.
     * @param apchDivCd 접근 구분 코드 ( 1: 로그인, 2: 로그아웃).
     */
    @POST("handleCrpMchyUseAth")
    @FormUrlEncoded
    fun handleCrpMchyUseAth(
        @Field("sysDivCd") sysDivCd: String = SYS_DIV_CD,
        @Field("svcId") svcId: String = SERVICE_ID_HANDLECRPMCHYUSEATH,
        @Field("appVer") appVersion: String,
        @Field("mdelNm") model: String,
        @Field("apchDivCd") apchDivCd: String,
        @Field("bdt") birth: String
    )

}