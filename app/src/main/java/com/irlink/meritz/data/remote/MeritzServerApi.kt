package com.irlink.meritz.data.remote

import com.irlink.meritz.domain.LoginUseCase
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

interface MeritzServerApi {

    companion object {
        private const val SYS_DIV_CD: String = "TM"

        private const val SERVICE_ID_RETRIEVECRPMCHYCNSCUSINF: String = "FTMTMSOO0097"
        private const val SERVICE_ID_RETRIEVECRPMCHYASGCUSLST: String = "FTMTMSOO0098"
        private const val SERVICE_ID_RETRIEVECRPMCHYRSRVCUSLST: String = "FTMTMSOO0099"
        private const val SERVICE_ID_SEARCHCRPMCHYCNSCUS: String = "FTMTMSOO0100"
        private const val SERVICE_ID_SAVECRPMCHYTCRSL: String = "FTMTMSOO0101"
        private const val SERVICE_ID_SENDCRPMCHYCLLPKMSG: String = "FTMTMSOO0102"
        private const val SERVICE_ID_SAVECRPMCHYABSETCDETL: String = "FTMTMSOO0103"
        private const val SERVICE_ID_REGISTCRPMCHYLCKCNCELPWD: String = "FTMTMSOO0104"
        private const val SERVICE_ID_HANDLECRPMCHYLCKCNCEL: String = "FTMTMSOO0105"
        private const val SERVICE_ID_SAVECRPMCHYCLLSTAT: String = "FTMTMSOO0106"
        private const val SERVICE_ID_RETRIEVECRPMCHYSTUPINF: String = "FTMTMSOO0107"
        private const val SERVICE_ID_RETRIEVECUSCNSRESPID: String = "FTMTMSOO0115"
        private const val SERVICE_ID_RETRIEVECRPMCHYRNSMSGINF: String = "FTMTMSOO0126"
        private const val SERVICE_ID_RETRIEVECRPMCHYCTCPSBYN: String = "FTMTMSOO0127"
    }

    /**
     * 로그인.
     */
    @POST("login.do")
    @FormUrlEncoded
    fun doLogin(
//        @Body body: RequestBody,
        @Field("uid") uid: String,
        @Field("pwd") pwd: String
    ): Flowable<LoginUseCase.Response>

    /**
     * 법인폰 상담 고객 정보 조회.
     */
    @POST("retrieveCrpMchyCnsCusInf.do")
    @FormUrlEncoded
    fun retrieveCrpMchyCnsCusInf(
        @Field("cnsCusId") cnsCusId: String,
        @Field("rciTelNo") rciTelNo: String
    )

    /**
     * 법인폰 배정 고객 리스트 조회.
     */
    @POST("retrieveCrpMchyAsgCusLst.do")
    @FormUrlEncoded
    fun retrieveCrpMchyAsgCusLst(
        @Field("inqStdDt") inqStdDt: String,
        @Field("nextInqCnsCusId") nextInqCnsCusId: String,
        @Field("nextInqAsgDtm") nextInqAsgDtm: String,
    )

    /**
     * 법인폰 예약 고객 리스트 조회.
     */
    @POST("retrieveCrpMchyRsrvCusLst.do")
    @FormUrlEncoded
    fun retrieveCrpMchyRsrvCusLst(
        @Field("inqStdDt") inqStdDt: String,
        @Field("nextInqCnsCusId") nextInqCnsCusId: String,
        @Field("nextInqRsrvDtm") nextInqRsrvDtm: String
    )

    /**
     * 법인폰 상담 고객 검색.
     */
    @POST("searchCrpMchyCnsCus.do")
    @FormUrlEncoded
    fun searchCrpMchyCnsCus(
        @Field("srchCnd") srchCnd: String
    )

    /**
     * 통화 결과 저장.
     */
    @POST("saveCrpMchyTcRsl.do")
    @FormUrlEncoded
    fun saveCrpMchyTcRsl(
        @Field("cusCnsRespId") cusCnsRespId: String,
        @Field("cnsCusId") cnsCusId: String,
        @Field("tcStDtm") tcStDtm: String,
        @Field("cllStDtm") cllStDtm: String,
        @Field("tcEdDtm") tcEdDtm: String,
        @Field("cnsEdDtm") cnsEdDtm: String,
        @Field("tcHr") tcHr: String,
        @Field("callTime") callTime: String,
        @Field("cnsHr") cnsHr: String,
        @Field("cusTelNo") cusTelNo: String,
        @Field("tlmkTcRslCd") tlmkTcRslCd: String,
        @Field("cllEqupKdCd") cllEqupKdCd: String,
        @Field("rcdgFileNm") rcdgFileNm: String,
        @Field("rcdgFilePthNm") rcdgFilePthNm: String,
        @Field("infwPthCd") infwPthCd: String,
        @Field("tlmkCnsCon") tlmkCnsCon: String,
    )

    /**
     * 법인폰 콜백 메세지 발송.
     */
    @POST("sendCrpMchyCllbkMsg.do")
    @FormUrlEncoded
    fun sendCrpMchyCllbkMsg(
        @Field("cnsCusId") cnsCusId: String,
        @Field("rciTelNo") rciTelNo: String,
        @Field("trnsDivCd") trnsDivCd: String,
        @Field("smsMsgCon") smsMsgCon: String,
    )

    /**
     * 법인폰 부재 통화 저장.
     */
    @POST("saveCrpMchyAbseTcDetl.do")
    @FormUrlEncoded
    fun saveCrpMchyAbseTcDetl(
        @Field("rciTelNo") rciTelNo: String,
        @Field("cnsCusId") cnsCusId: String,
        @Field("rciDtm") rciDtm: String,
        @Field("cllbkDivCd") cllbkDivCd: String,
        @Field("smsMsgCon") smsMsgCon: String,
    )

    /**
     * 법인폰 잠금 화면 비밀번호 등록.
     */
    @POST("registCrpMchyLckCncelPwd.do")
    @FormUrlEncoded
    fun registCrpMchyLckCncelPwd(
        @Field("lckScrnPwd") lckScrnPwd: String,
    )

    /**
     * 법인폰 잠금 화면 해제.
     */
    @POST("handleCrpMchyLckCncel.do")
    @FormUrlEncoded
    fun handleCrpMchyLckCncel(
        @Field("lckScrnPwd") lckScrnPwd: String
    )

    /**
     * 법인폰 상태 저장.
     * @param crpMchyStatCd 0: 대기.
     * 1 - 통화중.
     * 2 - 인콜 수신 방법이 콜백일 경우 고정.
     */
    @POST("saveCrpMchyCllStat.do")
    @FormUrlEncoded
    fun saveCrpMchyCllStat(
        @Field("crpMchyStatCd") crpMchyStatCd: String
    )

    /**
     * 법인폰 설정 조회.
     */
    @POST("retrieveCrpMchyStupInf.do")
    @FormUrlEncoded
    fun retrieveCrpMchyStupInf()

    /**
     * 고객 상담 응대 ID 조회.
     */
    @POST("retrieveCusCnsRespId.do")
    @FormUrlEncoded
    fun retrieveCusCnsRespId(
        @Field("cllId") cllId: String
    )

    /**
     * 발송 리스트 조회.
     */
    @POST("retrieveCrpMchyRnsMsgInf.do")
    @FormUrlEncoded
    fun retrieveCrpMchyRnsMsgInf(
        @Field("cnsCusId") cnsCusId: String,
        @Field("nextCnsCusId") nextCnsCusId: String,
        @Field("nextRespRegDtm") nextRespRegDtm: String,
    )

    /**
     * 전화, 문자 발송 가능 여부 검증.
     */
    @POST("retrieveCrpMchyCtcPsbYn.do")
    @FormUrlEncoded
    fun retrieveCrpMchyCtcPsbYn(
        @Field("cnsCusId") cnsCusId: String,
        @Field("ctcDivCd") ctcDivCd: String
    )

}