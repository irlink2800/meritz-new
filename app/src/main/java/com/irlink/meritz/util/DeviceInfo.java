//package com.irlink.meritz.util;
//
//
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.databinding.ObservableField;
//import android.media.AudioManager;
//import android.os.Build;
//import android.provider.Settings;
//import android.telecom.TelecomManager;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import com.irlink.meritz.App;
//import com.irlink.meritz.MainActivity;
//
///**
// * Created by iklee on 2018-05-02.
// */
//public class DeviceInfo {
//
//    private static String TAG = "DeviceInfo";
//
//    Context context;
//
//    private String phone;
//    private String model;
//    private String osVer;
//    private String appVer;
//    private String deviceId;
//
//
//    private String cnslrOrgId;
//
//    // 인콜 처리 ( 1 - 받기 끊기 가능, 2 - 콜백처리 )
//    public ObservableField<String> incallRciMdCd = new ObservableField<>();
//    // 상담원명
//    public ObservableField<String> cnslrNm = new ObservableField<>();
//    // 소켓 연결 상태
//    public ObservableField<String> socketState = new ObservableField<>("N");
//
//    private String sabun;
//    private String pass;
//    private String birth;
//
//    public boolean isLogin = false;
//    public String lastLoginDate = "";
//
//    TelephonyManager telephonyManager;
//    AudioManager audioManager;
//
//    public TelecomManager telecomManager;
//
//    static int STATE_OK = 1;
//    static int STATE_NOK = 2;
//    static int STATE_ERROR = 3;
//
//    static int CODE_SHOULD_NOT_UPDATE = 0;
//    static int CODE_SHOULD_UPDATE = 1;
//    static String DOWNLOAD_URL = "";
//
//    private boolean certState = false;
//
//    // 재전송 횟수
//    public int resendCnt = 0;
//    // 녹취 저장 기간
//    public int rcdgCstdPrd = 14;
//    // 관리자 구분 값 ( "ADMIN" = 관리자, "" = 일반 )
//    String slzBizAthoCd = "";
//    // 잠금 시간 ( 분 )
//    int crpMchyScrnLckHr = 3;
//
//    public Context topActivity;
//    // 상담결과명, 상담결과코드
//    public JSONArray crpMchyTcRslCdBcVo = new JSONArray();
//
//    public DeviceInfo(Context context) {
//        this.context = context;
//
//        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        this.telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
//
//        Log.d("DeviceInfo", "constructor");
//
//        try {
//            phone = telephonyManager.getLine1Number().replace("+82", "0");
//            //202102 Android 10 지원
//            if (Build.VERSION.SDK_INT >= 29) {
//                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//            } else {
//                deviceId = telephonyManager.getDeviceId();
//            }
//        } catch (Exception e) {
//            phone = "";
//            deviceId = "0";
//        }
//
//        model = Build.MODEL;
//        osVer = Build.VERSION.RELEASE;
//        try {
//            appVer = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            appVer = "0";
//            e.printStackTrace();
//        }
//    }
//
//    public TelephonyManager getTelephonyManager() {
//        return telephonyManager;
//    }
//
//    public TelecomManager getTelecomManager() {
//        return telecomManager;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public String getModel() {
//        return model;
//    }
//
//    public String getOsVer() {
//        return osVer;
//    }
//
//    public String getAppVer() {
//        return appVer;
//    }
//
//    public String getDeviceId() {
//        return deviceId;
//    }
//
//    public AudioManager getAudioManager() {
//        return audioManager;
//    }
//
//    public void setPhone() {
//        try {
//            phone = telephonyManager.getLine1Number().replace("+82", "0");
//        } catch (Exception e) {
//            LogWrapper.addLog(TAG, "setPhone. e : " + e.toString());
//            phone = null;
//        }
//    }
//
//    public void setDeviceId() {
//        try
//        //202102 Android 10 지원
//        {
//            if (Build.VERSION.SDK_INT >= 29) {
//                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//            } else {
//                deviceId = telephonyManager.getDeviceId();
//            }
//        } catch (Exception e) {
//            LogWrapper.addLog(TAG, "setDeviceId. e : " + e.toString());
//            deviceId = "";
//        }
//    }
//
//    public boolean isCertState() {
//        return certState;
//    }
//
//    public void setCertState(boolean certState) {
//        this.certState = certState;
//    }
//
//    public String getCnslrOrgId() {
//        return cnslrOrgId;
//    }
//
//    public void setCnslrOrgId(String cnslrOrgId) {
//        this.cnslrOrgId = cnslrOrgId;
//    }
//
//    public String getSabun() {
//        return sabun;
//    }
//
//    public void setSabun(String sabun) {
//        this.sabun = sabun;
//    }
//
//    public String getBirth() {
//        return birth;
//    }
//
//    public void setBirth(String birth) {
//        this.birth = birth;
//    }
//
//    public String getPass() {
//        return pass;
//    }
//
//    public void setPass(String pass) {
//        this.pass = pass;
//    }
//
//    public String getSlzBizAthoCd() {
//        return slzBizAthoCd;
//    }
//
//    public void setSlzBizAthoCd(String slzBizAthoCd) {
//        this.slzBizAthoCd = slzBizAthoCd;
//    }
//
//    public int getCrpMchyScrnLckHr() {
//        return crpMchyScrnLckHr;
//    }
//
//    public void setCrpMchyScrnLckHr(int crpMchyScrnLckHr) {
//        this.crpMchyScrnLckHr = crpMchyScrnLckHr;
//    }
//
//    public void saveCrpMchyTcRslCdBcVo(JSONArray v) {
//        crpMchyTcRslCdBcVo = App.getCommonUtil().sort(v, (a, b) -> {
//            JSONObject ja = (JSONObject) a;
//            JSONObject jb = (JSONObject) b;
//            int valA = ja.optInt("srtSq", 999);
//            int valB = jb.optInt("srtSq", 999);
//            if (valA > valB)
//                return 1;
//            if (valA < valB)
//                return -1;
//            return 0;
//        });
//    }
//
//    public String getDeviceCallApp(boolean isType) {
//        String defaultAppPkg = App.getDeviceInfo().telecomManager.getDefaultDialerPackage();
//
//        Log.d("defaultAppPkg", defaultAppPkg);
//
//        if (defaultAppPkg == null) return "X";
//
//        switch (defaultAppPkg) {
//            case "com.samsung.android.dialer":
//            case "com.samsung.android.contacts":
//                if (isType) return "1";
//                else return "삼성 전화";
//            case "com.skt.prod.dialer":
//                if (isType) return "2";
//                else return "T 전화";
//            case "com.irlink.meritz":
//                if (isType) return "3";
//                else return "메리츠 모바일";
//            default:
//                if (isType) return "4";
//                else return "알수없음";
//        }
//    }
//}
