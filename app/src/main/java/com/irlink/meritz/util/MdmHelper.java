package com.irlink.meritz.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.irlink.meritz.R;
import com.sktelecom.ssm.lib.SSMLib;
import com.sktelecom.ssm.lib.SSMLibListener;
import com.sktelecom.ssm.lib.constants.SSMProtocolParam;
import com.sktelecom.ssm.lib.util.StringUtil;
import com.sktelecom.ssm.remoteprotocols.DeviceInfoResponse;
import com.sktelecom.ssm.remoteprotocols.ResultCode;

import java.util.Iterator;
import java.util.Map;

public class MdmHelper implements SSMLibListener, SSMProtocolParam, ResultCode {

    private SSMLib ssmLib;
    private String TAG = "TSSM";
    private String SSM_APP_NAME = "SSM";
    private String V3_APP_NAME = "V3";
    private AlertDialog dialog;
    private int trySecond = 10;

    Context context;

    public MdmHelper() {

    }

    public void mdmInit(Context context) {
        this.context = context;
        ssmLib = SSMLib.getInstance(context);
        ssmLib.registerSSMListener(this);
        /* SSMLib 초기화 */
        int code = ssmLib.initialize();
        if (code != OK) {
            Toast.makeText(context, getMessage(ssmLib.checkSSMValidation(), SSM_APP_NAME),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setLogin() {
        int result = FAILED;
        for (int i = 0; i < trySecond; i++) {
            result = ssmLib.setLoginStatus(LOGIN);
            // 정책 수신중일경우
            if (result == SSMLib.FAILED) {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            } else {
                break;
            }
        }

        String str = getMessage(result, SSM_APP_NAME);
        Toast.makeText(context, "Login Status - " + str , Toast.LENGTH_SHORT).show();
    }

    public void mdmStart() {
        if(((Activity)context).isFinishing()) return;
        if (ssmLib.isInstalledSSM()) {
            if (ssmLib.isInstalledOldSSM()) {
                // SSM을 삭제
                ssmLib.requestRemoveSSM();
            } else {
                if (!ssmLib.isRemote() && !ssmLib.doingBind()) {
                    int result = ssmLib.checkSSMValidation();
                    switch (result) {
                        case SSMLib.OK:
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            // 로그인을 위한 처리
                            setLogin();
                            break;
                        case SSMLib.UNREGISTERED:
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            dialog = new AlertDialog.Builder(context)
                                    .setTitle(R.string.common_dialog_title_notice)
                                    .setMessage(R.string.unregistered_ssm)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.common_button_ok,
                                            (dialog, which) -> {
                                                dialog.dismiss();
//                                                    ssmLib.startSSM();
                                            }).show();
                            break;
                        case SSMLib.ERROR_CONNECTION:
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.common_dialog_title_notice)
                                    .setMessage(R.string.unknown_error)
                                    .setCancelable(true)
                                    .setPositiveButton(R.string.common_button_ok,
                                            (dialog, which) -> {
                                                dialog.dismiss();
                                                ssmLib.stopMyApp();
                                            }).show();
                            break;
                        case SSMLib.OLD_VERSION_INSTALLED:
                            ssmLib.stopMyApp();
                            break;
                        case SSMLib.NOT_INSTALLED:
                            ssmLib.stopMyApp();
                            break;
                        default:
                            Log.d(TAG, "onSSMConnected :: checkSSMValidation error result = "
                                    + result);
                            break;

                    }
                }
            }
        } else {
            // 미 설치
//            ssmLib.requestDownloadSSMUrl(mGetDownloadUrlHandler, serverUrl, COMPANY, "SKT");
            /*
             * option 1 url 하드 코딩 시 if( url != null ) {
             * ssmLib.startSSMInstaller(mine, url, "Notice",
             * "Please, wait for install SSM.", true); }
             */
            /*
             * option 2 app이 있는 장소로 웹페이지 이동
             */
             context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mdm.meritzfire.com:52444/inhouse")));
        }
    }

    public void releaseMDM() {
        ssmLib.release();
    }

    private String getMessage(int result, String param) {
        String str = "";
        switch (result) {

            case OK_PANDING:
                str = StringUtil.formatToString(context.getApplicationContext(),
                        R.string.result_ok_panding, param);
                break;

            case OK:
                str = context.getResources().getString(R.string.result_ok);
                break;

            case ERROR_CONNECTION:
                str = context.getResources().getString(R.string.result_error_connection);
                break;

            case FAILED:
                str = context.getResources().getString(R.string.result_failed);
                break;

            case ERROR:
                str = context.getResources().getString(R.string.result_error);
                break;

            case NOT_INSTALLED:
                str = StringUtil.formatToString(context.getApplicationContext(),
                        R.string.result_uninstalled, param);
                break;

            case UNREGISTERED:
                str = StringUtil.formatToString(context.getApplicationContext(),
                        R.string.result_unregistered, param);
                break;

            case OLD_VERSION_INSTALLED:
                str = StringUtil.formatToString(context.getApplicationContext(),
                        R.string.result_old_version_installed, param);
                break;

            case NO_PERMISSION:
                str = context.getResources().getString(R.string.result_no_permission);
                break;
        }

        return str;
    }

    @Override
    public void onSSMInstalled() {
        ssmLib.initialize();
    }

    @Override
    public void onSSMConnected() {
        int result = ssmLib.checkSSMValidation();
        switch (result) {
            case SSMLib.OK:
                //   ssmLib.registerJobListManager();
                break;
            case SSMLib.ERROR_CONNECTION:
                new AlertDialog.Builder(context)
                        .setTitle(R.string.common_dialog_title_notice)
                        .setMessage(R.string.unknown_error)
                        .setCancelable(true)
                        .setPositiveButton(R.string.common_button_ok,
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    ssmLib.stopMyApp();
                                }).show();
                break;
            case SSMLib.OLD_VERSION_INSTALLED:
                ssmLib.stopMyApp();
                break;
            case SSMLib.NOT_INSTALLED:
                ssmLib.stopMyApp();
                break;
            case SSMLib.UNREGISTERED:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.common_dialog_title_notice)
                        .setMessage(R.string.unregistered_ssm)
                        .setCancelable(false)
                        .setPositiveButton(R.string.common_button_ok,
                                (dialog, which) -> {
                                    dialog.dismiss();
//                                        ssmLib.startSSM();
                                }).show();
                break;
            default:
                Log.d(TAG, "onSSMConnected :: checkSSMValidation error result = " + result);
                break;
        }
    }

    @Override
    public void onSSMRemoved() {
        // 삭제됨
        Log.d(TAG, "REMOVED SSM");
    }

    public String getHwTypeName(int nHwType) {
        String sHwType = "";

        switch (nHwType) {
            case KEY_WIFI:
                sHwType = context.getResources().getString(R.string.wifi);
                break;
            case KEY_BLUETOOTH:
                sHwType = context.getResources().getString(R.string.bluetooth);
                break;
            case KEY_TETHERING:
                sHwType = context.getResources().getString(R.string.tethering);
                break;
            case KEY_USB:
                sHwType = context.getResources().getString(R.string.usb);
                break;
            case KEY_CAMERA:
                sHwType = context.getResources().getString(R.string.camera);
                break;
            case KEY_GPS:
                sHwType = context.getResources().getString(R.string.gps);
                break;
            case KEY_MIKE:
                sHwType = context.getResources().getString(R.string.mike);
                break;
            case KEY_EXSDCARD:
                sHwType = context.getResources().getString(R.string.ext_sdcard);
                break;
            case KEY_CAPTURE:
                sHwType = context.getResources().getString(R.string.capture);
                break;
        }
        return sHwType;
    }

    @Override
    public void onSSMResult(String key, Object returnValue) {
        String str;
        if (key.equals(KEY_CHECK_SSM_VALIDATION)) {
            int code = (Integer)returnValue;
            str = getMessage(code, SSM_APP_NAME);
            Log.d(TAG, "onSSMResult " + key + " : " + str);
        } else if (key.equals(KEY_CHECK_V3_VALIDATION)) {
            int code = (Integer)returnValue;
            str = getMessage(code, V3_APP_NAME);
            Log.d(TAG, "onSSMResult " + key + " : " + str);
        } else if (key.equals(KEY_SET_LOGIN_STATUS)) {
            int code = (Integer)returnValue;
            str = getMessage(code, SSM_APP_NAME);
            Log.d(TAG, "onSSMResult " + key + " : " + str);
        } else if (key.equals(KEY_HW_CONTROL)) {
            Map<Integer, Integer> resultMap = (Map<Integer, Integer>)returnValue;

            if (resultMap != null && resultMap.size() > 0) {
                Iterator<Integer> iter = resultMap.keySet().iterator();
                int hwCode = 0;

                while (iter.hasNext()) {
                    hwCode = iter.next();
                    str = getHwTypeName(hwCode) + " : "
                            + getMessage(resultMap.get(hwCode), getHwTypeName(hwCode));
                    Log.d(TAG, "onSSMResult " + key + " : " + str);
                }
            }
        } else if (key.equals(KEY_FORCE_APP_INSTALL)) {
            int code = (Integer)returnValue;
            str = getMessage(code, SSM_APP_NAME);
            Log.d(TAG, "onSSMResult " + key + " : " + str);
        } else if (key.equals(KEY_GET_DEVICE_INFO)) {
            Map<String, DeviceInfoResponse> resultMap = (Map<String, DeviceInfoResponse>)returnValue;
            Iterator<String> iter = resultMap.keySet().iterator();
            String type = "";
            str = "";
            DeviceInfoResponse data = null;

            while (iter.hasNext()) {
                type = iter.next();
                data = resultMap.get(type);
                if (data != null) {
                    if (data.getResponseCode() == OK) {
                        str = type + " : " + data.getValue();
                    } else {
                        str = type + " : " + data.getResponseString();
                    }
                    Log.d(TAG, "onSSMResult " + key + " : " + str);
                }
            }
        } else if (key.equals(KEY_SEND_BROADCAST)) {
            int code = (Integer)returnValue;
            str = getMessage(code, SSM_APP_NAME);
            Log.d(TAG, "onSSMResult " + key + " : " + str);
        } else if (key.equals(KEY_SET_SERVER_URL)) {
            int code = (Integer)returnValue;
            str = getMessage(code, SSM_APP_NAME);
            Log.d(TAG, "onSSMResult " + key + " : " + str);
        } else if (key.equals(KEY_SET_HW_STATE)) {
            Map<Integer, Integer> resultMap = (Map<Integer, Integer>)returnValue;

            if (resultMap != null && resultMap.size() > 0) {
                Iterator<Integer> iter = resultMap.keySet().iterator();
                int hwCode = 0;

                while (iter.hasNext()) {
                    hwCode = iter.next();
                    str = getHwTypeName(hwCode) + " : "
                            + getMessage(resultMap.get(hwCode), getHwTypeName(hwCode));
                    Log.d(TAG, "onSSMResult " + key + " : " + str);
                }
            }
        }
    }
}
