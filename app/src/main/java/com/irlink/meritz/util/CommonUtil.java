//package com.irlink.meritz.util;
//
//
//import static android.content.Context.WINDOW_SERVICE;
//import static com.irlink.meritz.model.RetrofitAdapter.RECSERVER_URL;
//import static com.irlink.meritz.model.RetrofitAdapter.SERVER_URL_NODE;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.KeyguardManager;
//import android.app.PendingIntent;
//import android.app.ProgressDialog;
//import android.app.TimePickerDialog;
//import android.arch.persistence.room.EmptyResultSetException;
//import android.content.BroadcastReceiver;
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.graphics.PixelFormat;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.BatteryManager;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.CallLog;
//import android.provider.DocumentsContract;
//import android.provider.MediaStore;
//import android.provider.Telephony;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatDialog;
//import android.telecom.TelecomManager;
//import android.telephony.SmsManager;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//import com.android.internal.telephony.ITelephony;
//import com.arthenica.mobileffmpeg.Config;
//import com.arthenica.mobileffmpeg.FFmpeg;
//import com.google.android.gms.tasks.OnCanceledListener;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.irlink.irrecorder.IRRecorderv2;
//import com.irlink.meritz.App;
//import com.irlink.meritz.MainActivity;
//import com.irlink.meritz.R;
//import com.irlink.meritz.call.CallItem;
//import com.irlink.meritz.call.OngoingCall;
//import com.irlink.meritz.model.ApiInterface;
//import com.irlink.meritz.model.MeritzAPI;
//import com.irlink.meritz.model.RetrofitAdapter;
//import com.irlink.meritz.room.database.AppDataBase;
//import com.irlink.meritz.socket.SocketService;
//import com.klinker.android.send_message.Transaction;
//
//import org.json.JSONArray;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.security.Key;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Optional;
//import java.util.StringTokenizer;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
//import io.reactivex.BackpressureStrategy;
//import io.reactivex.Completable;
//import io.reactivex.Flowable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.schedulers.Schedulers;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
///**
// * Created by iklee on 2018-05-02.
// */
//public class CommonUtil {
//
//    String TAG = "CommonUtil";
//
//    public Context context;
//    SimpleDateFormat format_test = null;
//    SimpleDateFormat format_yyymm = null;
//    SimpleDateFormat format_date = null;
//    SimpleDateFormat format_time = null;
//    SimpleDateFormat format_datetime = null;
//
//    public String AUDIO_RECORDER_FOLDER = "AudioRecorder";
//
//    public String AUDIO_RECORDER_FILE_EXT_WAV = ".amr";
//    String FILE_TO_LISTEN_ENC_NAME = "listen_enc";
//    String FILE_TO_LISTEN_NAME = "listen";
//
//    String PREF_NAME_CERT = "CERT";
//    String AES = "AES";
//
//    TextView view_block;    // 상판 막기 기능에서 사용됨.
//
//    public String devChkFilePath;
//
//    //201912 녹취파일생성실패 Error 판단 추가 (JHLEE)
//    public boolean boolRecordingFileNotCreate = false;
//
//    //202103 로그인시 m4a amr변환 후 강제전송 추가
//    ArrayList<String[]> listLoginResendConvertFile = new ArrayList<String[]>();
//    int iResendPos = 0;
//
//    public CommonUtil(Context context) {
//        this.context = context;
//        format_test = new SimpleDateFormat("yyMMdd", Locale.KOREA);
//        format_yyymm = new SimpleDateFormat("yyyyMM", Locale.KOREA);
//        format_date = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
//        format_time = new SimpleDateFormat("HHmmss", Locale.KOREA);
//        format_datetime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
//        devChkFilePath = System.getenv("EXTERNAL_STORAGE") + File.separator + "mertiz" + File.separator + "MeritzMobileEnv_Dev.txt";
//    }
//
//    public String getCurrentMonth() {
//        return format_yyymm.format(new Date());
//    }
//
//    public String getCurrentDate() {
//        return format_date.format(new Date());
//    }
//
//    public String getDeleteDate() {
//        return LocalDate.now().minusDays(App.getDeviceInfo().rcdgCstdPrd).format(DateTimeFormatter.BASIC_ISO_DATE);
//    }
//
//    public String getDateEspecailly() {
//        return LocalDate.now().minusDays(App.getDeviceInfo().rcdgCstdPrd).format(DateTimeFormatter.BASIC_ISO_DATE);
//    }
//
//    public String getCurrentTime() {
//        return format_time.format(new Date());
//    }
//
//    public String getFileNameWithoutExtension(final String file) {
//        String fileNameWithoutExtension = "";
//        fileNameWithoutExtension = file.replaceAll(AUDIO_RECORDER_FILE_EXT_WAV, "");
//        fileNameWithoutExtension = fileNameWithoutExtension.substring(fileNameWithoutExtension.lastIndexOf('/') + 1, fileNameWithoutExtension.length());
//        return fileNameWithoutExtension;
//    }
//
//
//    /**
//     * 레거시 녹취 파일 경로 반환.
//     *
//     * @return 폴더 경로
//     */
//    public String getLegacyRecorderFolder() {
//        return IRRecorderv2.getRecordingFilePath();
//    }
//
//    /**
//     * 통화시간(초)로 변환 함수
//     *
//     * @param endDate
//     * @param endTime
//     * @param startDate
//     * @param startTime
//     * @return
//     */
//    public String getDuration(final String endDate, final String endTime, final String startDate, final String startTime) {
//        boolean bValidate = true;
//        String sEndDate = endDate;
//        String sEndTime = endTime;
//        String sStartDate = startDate;
//        String sStartTime = startTime;
//
//        try {
//            // 숫자만 빼고 나머지 문자들은 모두 없앤다
//            sEndDate = sEndDate.replaceAll("-", "").replaceAll(":", "");
//            sEndTime = sEndTime.replaceAll("-", "").replaceAll(":", "");
//            sStartDate = sStartDate.replaceAll("-", "").replaceAll(":", "");
//            sStartTime = sStartTime.replaceAll("-", "").replaceAll(":", "");
//
//            // 날짜, 시간 길이 체크
//            if (sEndDate.length() != 8)
//                bValidate = false;
//            else if (sEndTime.length() != 6)
//                bValidate = false;
//            else if (sStartDate.length() != 8)
//                bValidate = false;
//            else if (sStartTime.length() != 6)
//                bValidate = false;
//
//            // 날짜, 시간 값 중에서 한개라도 이상이 있으면 0으로 리턴
//            if (!bValidate)
//                return "0";
//
//            String result = "";
//            long end_sec = 0;
//            long start_sec = 0;
//
//            final Calendar cal = Calendar.getInstance();
//
//            // 종료일을 cal 객체에 할당
//            cal.set(Integer.parseInt(sEndDate.trim().substring(0, 4)),
//                    Integer.parseInt(sEndDate.trim().substring(4, 6)) - 1,
//                    Integer.parseInt(sEndDate.trim().substring(6, 8)),
//                    Integer.parseInt(sEndTime.trim().substring(0, 2)),
//                    Integer.parseInt(sEndTime.trim().substring(2, 4)),
//                    Integer.parseInt(sEndTime.trim().substring(4, 6)));
//            end_sec = cal.getTimeInMillis();
//
//            // 시작일을 cal 객체에 할당
//            cal.set(Integer.parseInt(sStartDate.trim().substring(0, 4)),
//                    Integer.parseInt(sStartDate.trim().substring(4, 6)) - 1,
//                    Integer.parseInt(sStartDate.trim().substring(6, 8)),
//                    Integer.parseInt(sStartTime.trim().substring(0, 2)),
//                    Integer.parseInt(sStartTime.trim().substring(2, 4)),
//                    Integer.parseInt(sStartTime.trim().substring(4, 6)));
//            start_sec = cal.getTimeInMillis();
//
//            // 시작일과 종료일이 바뀌어도 날짜와 시간차이를 계산하고 절대값으로 리턴 함.
//            result = String.valueOf(Math.abs((end_sec - start_sec) / 1000));
//
//            return result;
//        } catch (NumberFormatException e) {
//            return "0";
//        } catch (Exception e) {
//            return "0";
//        }
//    }
//
//    public long getTalkTime() {
//        int duration = 0;
//        final Uri contacts = CallLog.Calls.CONTENT_URI;
//        final String order = CallLog.Calls.DATE + " DESC";
//        final Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, order);
//        if (managedCursor != null) {
//            duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);  //스마트폰의 통화이력(순수통화시간)
//            managedCursor.moveToNext();
//            duration = Integer.parseInt(managedCursor.getString(duration));
//        }
//
//        return duration;
//    }
//
//    public String getCallTimeConnect(final String callDate, final String callTime, final String second) {
//        String strDate = "0";
//        //boolean bValidate = true;
//        try {
//            String sStartDate = callDate;
//            String sStartTime = callTime;
//
//            sStartDate = sStartDate.replaceAll("-", "").replaceAll(":", "");
//            sStartTime = sStartTime.replaceAll("-", "").replaceAll(":", "");
//
//            final Calendar cal = Calendar.getInstance();
//            cal.set(Integer.parseInt(sStartDate.trim().substring(0, 4)),
//                    Integer.parseInt(sStartDate.trim().substring(4, 6)) - 1,
//                    Integer.parseInt(sStartDate.trim().substring(6, 8)),
//                    Integer.parseInt(sStartTime.trim().substring(0, 2)),
//                    Integer.parseInt(sStartTime.trim().substring(2, 4)),
//                    Integer.parseInt(sStartTime.trim().substring(4, 6)));
//            final int sec = Integer.parseInt(second);
//            cal.add(Calendar.SECOND, sec);
//            final SimpleDateFormat formatTime = new SimpleDateFormat("HHmmss", Locale.KOREA);
//            strDate = formatTime.format(cal.getTime());
//        } catch (Exception e) {
//            Log.d(TAG, "[Utils/getCallTimeConnect()] e=");
//        }
//        return strDate;
//    }
//
//    public long getFileSize(final String fileName) {
//        long fileSize = 0;
//
//        try {
//            final File srcFile = new File(fileName);
//            fileSize = srcFile.length();
//            if (fileSize > 0) {
//                fileSize = fileSize + 330;
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "[Recording/getFileSize()] e=");
//        }
//
//        return fileSize;
//    }
//
//    /**
//     * 녹취 파일 암호화
//     *
//     * @param
//     */
//    public void EncryptFile(final CallItem item) {
//        /* [Ver1.6] 암호화 여부 설정 저장(2016-05-24, kylee) */
//        final String encFlag = App.getPrefUtil().getPrefString("ENC_FLAG", "1");  // default 는 암호화(이미 사용하고 있는 대리점 모두 암호화하고 있기 때문)
//        if (encFlag.equals("1")) {
//            LogWrapper.addLog(TAG, "[Recording/EncryptFile()] ENC=1(암호화 함)");
//
//            LogWrapper.addLog(TAG, "[Recording/EncryptFile()] " + item.rcdgFileNm);
//
//            try {
//                final File file = new File(System.getenv("EXTERNAL_STORAGE") + "/" + App.getCommonUtil().AUDIO_RECORDER_FOLDER + "/" + item.rcdgFileNm);
//                final int size = (int) file.length();
//
//                LogWrapper.addLog("EncryptFile()", "bef size - " + size);
//
//                FileOutputStream out = null;
//                FileInputStream in = null;
//
//                final byte[] bytes_file = new byte[size];
//
//                try {
//                    in = new FileInputStream(file);
//                    final BufferedInputStream buf = new BufferedInputStream(in);
//                    buf.read(bytes_file, 0, size);
//                    buf.close();
//
//                    /* 2017/08/29 iklee IRRecorderv2 적용으로 주석. 해당 라이브러리에서는 amr파일에 헤더로 #!AMR이 기입되지 않음. mimetype = audio/3gpp */
//                    final String KEY = context.getResources().getString(R.string.key);
//                    final Key secureKey = new SecretKeySpec(KEY.getBytes(), "AES");
//                    Cipher cipher;
//                    try {
//
//                        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//                        cipher.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(KEY.substring(0, 16).getBytes()));
//                        final byte[] encryptedData = cipher.doFinal(bytes_file);
//
//                        file.delete();    // 암호화 되기 전 파일 삭제
//
//                        out = new FileOutputStream(System.getenv("EXTERNAL_STORAGE") + "/" + App.getCommonUtil().AUDIO_RECORDER_FOLDER + "/" + item.rcdgFileNm);
//                        out.write(encryptedData);
//                        out.close();
//
//                        LogWrapper.addLog("EncryptFile()", "afr size - " + file.length());
//
//                    } catch (Exception e) {
//                        LogWrapper.addLog(TAG, "[Recording/EncryptFile()] e=" + App.getCommonUtil().stackTraceToString(e));
//                        e.printStackTrace();
//                    } finally {
//                        if (out != null)
//                            out.close();
//                        if (in != null)
//                            in.close();
//                    }
//                } catch (FileNotFoundException e) {
//                    LogWrapper.addLog(TAG, "[Recording/EncryptFile()] FileNotFoundException, e=" + App.getCommonUtil().stackTraceToString(e));
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    LogWrapper.addLog(TAG, "[Recording/EncryptFile()] IOException, e=" + App.getCommonUtil().stackTraceToString(e));
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    LogWrapper.addLog(TAG, "[Recording/EncryptFile()] Exception, e=" + App.getCommonUtil().stackTraceToString(e));
//                    e.printStackTrace();
//                }
//
//            } catch (Exception e) {
//                LogWrapper.addLog(TAG, "[Recording/EncryptFile()] Exception, e=" + App.getCommonUtil().stackTraceToString(e));
//                e.printStackTrace();
//            }
//
//        } else {
//            LogWrapper.addLog(TAG, "[Recording/EncryptFile()] ENC = 0");
//        }
//        /* [Ver1.6] 암호화 여부 설정 저장(2016-05-24, kylee) */
//
//        // 암호화가 처리된 다음 파일명을 변경한다.
//        final String path = System.getenv("EXTERNAL_STORAGE") + "/" + App.getCommonUtil().AUDIO_RECORDER_FOLDER + "/";
//        File fileSource = null;
//        File fileTarget = null;
//        try {
//            fileSource = new File(path + item.rcdgFileNm);    // 통화시작 시 파일(최초)
//            LogWrapper.addLog(TAG, "[Recording/EncryptFile()]  - " + fileSource.exists());
//            if (fileSource.exists()) { // 녹취파일이 없으면 실행하지 않아도 됨
//                if (item.SetRecordFileName.equals("")) {
//                    // ocx에서 SetRecordFileName()을 호출하지 않은 경우이므로 default로 녹취파일명을 변경한다.
////                    Log.d(TAG, "[Recording/EncryptFile()] - 1] " + path + item.rcdgFileNm);
//                    fileTarget = new File(path + item.rcdgFileNm);
//                } else {
//                    // ocx에서 SetRecordFileName(strFileName);을 호출한 경우이므로 strFileName으로 파일명을 변경한다.
//                    LogWrapper.addLog(TAG, "[Recording/EncryptFile()] - 2] " + path + item.SetRecordFileName);
//                    fileTarget = new File(path + item.SetRecordFileName);
//                }
//
//                if (fileTarget.exists()) { // 스마트폰 내에 동일한 이름의 파일이 있다면 파일명(test.amr) 앞에 통화일시를 붙인다.(test.amr -> 20161223122030test.amr), 예전에 zilink를 잘 못 개발하여 setrecordfilename()을 동일하게 준 경우 녹취 유실이 된 적이 있었음. 이를 방지하는 방어 코디임.
//                    String tempFileName = App.getCommonUtil().getCurrentDate() + App.getCommonUtil().getCurrentTime();
//
//                    if (item.SetRecordFileName.equals("")) {
//                        // ocx에서 SetRecordFileName()을 호출하지 않은 경우이므로 default로 녹취파일명을 변경한다.
////                        Log.d(TAG, "[Recording/EncryptFile()] - 3] " + path + tempFileName + item.rcdgFileNm);
////                        fileTarget = new File(path + tempFileName + item.rcdgFileNm);
//                    } else {
//                        // ocx에서 SetRecordFileName(strFileName);을 호출한 경우이므로 strFileName으로 파일명을 변경한다.
//                        LogWrapper.addLog(TAG, "[Recording/EncryptFile()] - 4] " + path + tempFileName + item.SetRecordFileName);
//                        fileTarget = new File(path + tempFileName + item.SetRecordFileName);
//                    }
//                }
//                item.SetRecordFileName = "";
//
//                fileSource.renameTo(fileTarget);    // 최종 녹취파일명으로 녹취파일명 변경
//                item.rcdgFileNm = fileTarget.getName();
//                LogWrapper.addLog(TAG, "[Recording/EncryptFile()] 파일명변경(" + fileSource.getName() + ", " + fileTarget.getName() + ")");
//            }
//        } catch (Exception e) {
//            LogWrapper.addLog(TAG, "[Recording/EncryptFile()] e=" + App.getCommonUtil().stackTraceToString(e));
//            e.printStackTrace();
//        } finally {
//            fileSource = null;
//            fileTarget = null;
//        }
//    }
//
//    public void encryptFile(String fileName) throws Exception {
//        if (!fileName.endsWith(".amr") && !fileName.endsWith(".zip")) {
//            throw new RuntimeException("notAllowExt");
//        }
//
//        final File file = new File(System.getenv("EXTERNAL_STORAGE") + "/" + App.getCommonUtil().AUDIO_RECORDER_FOLDER + "/" + fileName);
//        final int size = (int) file.length();
//
//        FileOutputStream out = null;
//        FileInputStream in = null;
//
//        final byte[] bytes_file = new byte[size];
//
//        in = new FileInputStream(file);
//        final BufferedInputStream buf = new BufferedInputStream(in);
//        buf.read(bytes_file, 0, size);
//        buf.close();
//
//        // Header AMR Check.
//        if (fileName.endsWith(".amr") && !(bytes_file[0] == 35 && bytes_file[1] == 33 && bytes_file[2] == 65
//                && bytes_file[3] == 77 && bytes_file[4] == 82 && bytes_file[5] == 10)) {
//            throw new RuntimeException("noAMRHeader");
//        }
//
//        /* 2017/08/29 iklee IRRecorderv2 적용으로 주석. 해당 라이브러리에서는 amr파일에 헤더로 #!AMR이 기입되지 않음. mimetype = audio/3gpp */
//        final String KEY = context.getResources().getString(R.string.key);
//        final Key secureKey = new SecretKeySpec(KEY.getBytes(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(KEY.substring(0, 16).getBytes()));
//        final byte[] encryptedData = cipher.doFinal(bytes_file);
//
//        file.delete();    // 암호화 되기 전 파일 삭제
//
//        out = new FileOutputStream(System.getenv("EXTERNAL_STORAGE") + "/" + App.getCommonUtil().AUDIO_RECORDER_FOLDER + "/" + fileName);
//        out.write(encryptedData);
//        out.close();
//
//        if (out != null)
//            out.close();
//        if (in != null)
//            in.close();
//    }
//
//
//    public void startService(Intent intent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
//        } else {
//            context.startService(intent);
//        }
//    }
//
//    /**
//     * 스마트폰 사용 금지(상판 막음)
//     *
//     * @param yn
//     */
//    public void SetBlockMode(final int yn) {
//        if (yn == 1) {
//            if (view_block == null) {
//                view_block = new TextView(context);
//                view_block.setText("유저 입력 차단중");
//                view_block.setTextColor(Color.GREEN);
//                view_block.setBackgroundColor(Color.argb(127, 0, 0, 255));
//                view_block.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//                int type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY | WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                    type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//                }
//
//                final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                        type,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        PixelFormat.TRANSLUCENT);                         //투명
//
//                final WindowManager wm = (WindowManager) (context.getSystemService(WINDOW_SERVICE)); //윈도 매니저
//                wm.addView(view_block, params);  //최상위 윈도우에 뷰 넣기. permission필요.
//            }
//        } else {
//            if (view_block != null) {
//                ((WindowManager) (context.getSystemService(WINDOW_SERVICE))).removeView(view_block);
//                view_block = null;
//            }
//        }
//    }
//
//    private static final int SEND_FILE_SUCCESS = 1;
//    private static final int SEND_FILE_FILE_NOT_EXISTS = 2;
//    //private static final int SEND_FILE_WRONG_FILE = 3;
//    private static final int SEND_FILE_ERROR = 0;
//    private static final int SEND_FILE_SAVE_PATH_ERROR = 4;  // SAVE_PATH = 0 녹취저장 경로 지정 안됨
//
//    public int sendFile(final String local, String server, final String postURL, final String path, final boolean isReSend) throws Exception {
//        try {
//            if (local == null || server == null) return SEND_FILE_ERROR;
//            /* [Ver1.6] 녹취파일명 변경 영향으로 수정(2016-05-23, kylee) */
//            String s_name = server;        //녹취를 저장할 때는 server 파일명으로 저장해야 한다.
//            final String autoAppendDate = App.getPrefUtil().getPrefString("AUTO_APPEND_DATE", "1");    // 1 : 날짜 폴더에 녹취 저장, 0 : 날짜 폴더에 녹취 저장하지 않음
//
//            final String filePath = getSendFileName(local);
//
//            String yyyymm;
//            if (autoAppendDate.equals("1") && path.equals("")) {  // 날짜를 앱에서 붙여달라고 설정한 경우
////                final String yyyymmLastModified = getCreateDateTime(filePath);  //녹음 파일 최종 수정일
//
////                if (yyyymmLastModified.equals(yyyymmToday))
//                yyyymm = getCurrentMonth();
////                else
////                    yyyymm = yyyymmLastModified;
//            } else {
//                yyyymm = path;
//            }
//
//            final String savePath = App.getPrefUtil().getPrefString("SAVE_PATH", "2");
//
//            final File uploadFile = new File(filePath);
//
//            /* [Ver1.6] 녹취파일명 변경 영향으로 수정(2016-05-23, kylee) */
//            if (!uploadFile.exists()) {
//                LogWrapper.addLog(TAG, "[" + TAG + "/sendFile()] FILE NOT EXIST FILE = " + filePath);
//                return SEND_FILE_FILE_NOT_EXISTS;
//            }
//
//            if (savePath.equals("0")) { // 녹취파일 저장 경로 지정 안됨, 녹취 전송 안 되고 스마트폰에 보관한다.
//                LogWrapper.addLog(TAG, "[" + TAG + "/sendFile()] SEND_FILE_SAVE_PATH_ERROR, SAVE_PATH=0 " + filePath);
//                return SEND_FILE_SAVE_PATH_ERROR;
//            } else {    // savePath = 2는 HTTP로 전송을 의미한다.
//                if (local.length() == 0) {
//                    LogWrapper.addLog(TAG, "[" + TAG + "/sendFile()] SEND_FILE_ERROR local = ''");
//                    return SEND_FILE_ERROR;
//                }
//                if (server.length() == 0) {
//                    LogWrapper.addLog(TAG, "[" + TAG + "/sendFile()] SEND_FILE_ERROR server = ''");
//                    return SEND_FILE_ERROR;
//                }
//
//                String pathName;
//                if (yyyymm.equals(""))
//                    pathName = path + "";
//                else
//                    pathName = path + "\\" + yyyymm;
//
//                int slashIndex = ordinalIndexOf(postURL, "/", 3);
//
//                String baseURL = postURL.substring(0, slashIndex);
//                String addURL = postURL.substring(slashIndex + 1);
//
//                Retrofit ret = new Retrofit.Builder()
//                        .baseUrl(baseURL.endsWith("/") ? baseURL : baseURL + "/")
//                        .build();
//
//                File file = new File(filePath);
//
//                //202002 파일사이즈체크 추가
//                long sendFileSize = file.length();
//
//                Call<ResponseBody> call = ret.create(ApiInterface.class).postamr(
//                        addURL,
//                        RequestBody.create("REQ_TYPE_FILE", MultipartBody.FORM),
//                        RequestBody.create("ziphone", MultipartBody.FORM),
//                        RequestBody.create("irlink", MultipartBody.FORM),
//                        RequestBody.create(pathName, MultipartBody.FORM),
//                        RequestBody.create(s_name, MultipartBody.FORM),
//                        RequestBody.create(String.valueOf(sendFileSize), MultipartBody.FORM),
//                        MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(file, MultipartBody.FORM)));
//
//                try {
//                    Response<ResponseBody> response = call.execute();
//                    if (response.isSuccessful()) {
//                        String rslt = response.body().string().trim();
//                        Log.d("res", rslt);
//                        if (rslt == null || rslt.equals("") || !rslt.contains("RES_SUCCESS")) {
//                            return SEND_FILE_ERROR;
//                        }
//                    } else {
//                        LogWrapper.addLog(TAG, "[" + TAG + "/sendFile()] SEND_FILE_ERROR " + response.errorBody().string());
//                        return SEND_FILE_ERROR;
//                    }
//                } catch (IOException e) {
//                    LogWrapper.addLog(TAG, App.getCommonUtil().stackTraceToString(e));
//                    return SEND_FILE_ERROR;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return SEND_FILE_ERROR;
//        }
//
//        return SEND_FILE_SUCCESS;
//    }
//
//    /**
//     * 녹취파일 전송결과 이벤트를 발생시키는 메소드
//     *
//     * @param success 0:서버접속실패/기타 알 수 없는 오류, 1:전송성공, 2:전송할 파일 없음, 3:녹취 중 오류가 발생한 파일
//     * @param local   스마트폰 내 녹취파일명
//     * @param server  전송할 녹취파일명
//     * @param url     전송 경로
//     * @param path    전송 폴더
//     */
//    void saveFileSendResult(final int success, String local, String server, final String url, final String path) {
//        local = getSendFileName(local);
//        LogWrapper.addLog(TAG, "[" + TAG + "/saveFileSendResult()] success = " + success
//                + ", fileName = " + local
//                + ", serverFileName = " + server
//                + ", uploadUrl = " + url
//                + ", serverFolderName = " + path);
//        local = getFileNameWithoutExtension(local);
//        server = getFileNameWithoutExtension(server);
//        LogWrapper.addLog(TAG, "[" + TAG + "/saveFileSendResult()] COMMAND_DEVUPLOADED = " + success + "||" + local + "||" + server);
////        IRServerSocket.SendData(Background.COMMAND_DEVUPLOADED + PIPE + PIPE + success + PIPE + PIPE + local + PIPE + PIPE + server + SEMICOLON);
////        if(OngoingCall.INSTANCE.isCmd && OngoingCall.INSTANCE.callItem.io.equals("O")) {
//        Log.d("isCmd", "CMDCMDMCDMCMD - " + OngoingCall.INSTANCE.isCmd);
//        if (OngoingCall.INSTANCE.isCmd) {
//            HashMap msg = new HashMap<>();
//            msg.put("type", "DevUploaded");
//            msg.put("nResult", success);
//            msg.put("localFileName", local);
//            msg.put("serverFileName", server);
//            App.socketService.sendMsg(msg);
//        }
//
//        /* [Ver1.6] 녹취파일 전송 성공 시 파일 삭제(2016-05-24, kylee) */
//        if (success == 1) {
//            File uploadFile = null;
//            try {
//                // 녹취파일 삭제하기 전에 지능형MMS 대상인지 확인한다.
//                final String file1 = getSendFileName(server);
//                uploadFile = new File(file1);
////                Thread.sleep(1000 * 15);  // MMS 전송하는데 시간을 충분히 확보하기 위해서 사용
//                if (isUseBackup()) {
//                    backupFile(file1);
//                } else {
//                    //업로드한 파일 삭제
////                    if (uploadFile.delete()) {
////                        Log.d(TAG, "["+TAG+"/saveFileSendResult()] 녹취파일 전송 후 삭제 성공(DELETE FILE), 파일 = " + file1);
////                    } else {
////                        Log.d(TAG, "["+TAG+"/saveFileSendResult()] 녹취파일 전송 후 삭제 실패(DELETE FILE), 파일 = " + file1);
////                        final String file2 = getSendFileName(local);
////                        uploadFile = new File(file2);
////                        if (uploadFile.delete()) {
////                            Log.d(TAG, "["+TAG+"/saveFileSendResult()] 녹취파일 전송 후 삭제 성공(DELETE FILE), 파일 = " + file2);
////                        } else {
////                            Log.d(TAG, "["+TAG+"/saveFileSendResult()] 녹취파일 전송 후 삭제 실패(DELETE FILE), 파일 = " + file2);
////                        }
////                    }
//                }
//            } catch (Exception e) {
//                LogWrapper.addLog(TAG, "[" + TAG + "/saveFileSendResult()] 녹취파일 전송 후 삭제 실패(DELETE FILE), 파일 = " + local + ", e = ");
//            }
//        } else {
//            LogWrapper.addLog(TAG, "[" + TAG + "/saveFileSendResult()] 녹취파일 삭제하지 않음, success!=1, 파일 = " + local);
//        }
//        /* [Ver1.6] 녹취파일 전송 성공 시 파일 삭제(2016-05-24, kylee) */
//    }
//
//    private void backupFile(String filename) {
//        final String inputPath = System.getenv("EXTERNAL_STORAGE") + "/" + AUDIO_RECORDER_FOLDER;
//        final String outputPath = inputPath + "_bak";
//        final File dir = new File(outputPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            in = new FileInputStream(inputPath + "/" + filename + ".amr");
//            out = new FileOutputStream(outputPath + "/" + filename + ".amr");
//
//            byte[] buffer = new byte[1024];
//            int read;
//            while ((read = in.read(buffer)) != -1) {
//                out.write(buffer, 0, read);
//            }
//            // write the output file
//            out.flush();
//
//            // delete the original file
//            new File(inputPath + "/" + filename + ".amr").delete();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                in.close();
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private boolean isUseBackup() {
//        return App.getPrefUtil().getPrefBoolean("USE_BACKUP", false).equals("1");
//    }
//
//    /**
//     * 스마트폰 녹취 파일의 전체 경로와 파일명(확장자포함)을 리턴하는 메소드
//     *
//     * @param name 녹취파일명(확장자 미포함)
//     * @return
//     */
//    public String getSendFileName(final String name) {
//        return (System.getenv("EXTERNAL_STORAGE") + "/"
//                + AUDIO_RECORDER_FOLDER + "/" + name);
//    }
//
//    /**
//     * 파일 생성 일자를 리턴
//     *
//     * @param file
//     * @return
//     */
//    public String getCreateDateTime(final String file) {
//        final File f = new File(file);
//        String date = "";
//        if (f.exists()) {
//            final long millisec = f.lastModified();
//            final Date dt = new Date(millisec);
//
////            final SimpleDateFormat format_date = new SimpleDateFormat("yyyyMM", Locale.KOREA);
//            date = format_yyymm.format(dt);
//        }
//        return date;
//    }
//
//    public int ordinalIndexOf(String str, String substr, int n) {
//        int pos = str.indexOf(substr);
//        while (--n > 0 && pos != -1)
//            pos = str.indexOf(substr, pos + 1);
//        return pos;
//    }
//
//    public void listenFile(String fileUrl) {
//        Retrofit ret = new Retrofit.Builder().build();
//
//        final ApiInterface downloadService =
//                ret.create(ApiInterface.class);
//
//        Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlSync(fileUrl);
//
//        final ProgressDialog dialog = ProgressDialog.show(context, "", "청취 파일을 다운로드중입니다", true);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    LogWrapper.addLog(TAG, "server contacted and has file");
//
//                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());
//
//                    if (dialog != null)
//                        dialog.dismiss();
//                    final boolean decrypted = decryptAndRemoveListenFile();
//                    if (decrypted) {
//                        final Intent intent = new Intent();
//                        intent.setAction(Intent.ACTION_VIEW);
//                        final File file = new File(getFilepathOf(FILE_TO_LISTEN_NAME));
//                        intent.setDataAndType(Uri.fromFile(file), "audio/*");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//
//                    LogWrapper.addLog(TAG, "file download was a success? " + writtenToDisk);
//                } else {
//                    LogWrapper.addLog(TAG, "server contact failed");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "error");
//            }
//        });
//    }
//
//    private boolean writeResponseBodyToDisk(ResponseBody body) {
//        try {
//
//            File futureStudioIconFile = new File(FILE_TO_LISTEN_ENC_NAME);
//
//            InputStream inputStream = null;
//            OutputStream outputStream = null;
//
//            try {
//                byte[] fileReader = new byte[4096];
//
//                long fileSize = body.contentLength();
//                long fileSizeDownloaded = 0;
//
//                inputStream = body.byteStream();
//                outputStream = new FileOutputStream(futureStudioIconFile);
//
//                while (true) {
//                    int read = inputStream.read(fileReader);
//
//                    if (read == -1) {
//                        break;
//                    }
//
//                    outputStream.write(fileReader, 0, read);
//
//                    fileSizeDownloaded += read;
//
//                    LogWrapper.addLog(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
//                }
//
//                outputStream.flush();
//
//                return true;
//            } catch (IOException e) {
//                return false;
//            } finally {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//            }
//        } catch (IOException e) {
//            return false;
//        }
//    }
//
//    public boolean decryptAndRemoveListenFile() {
//        final File file = new File(getFilepathOf(FILE_TO_LISTEN_ENC_NAME));
//        if (!(file.exists())) return false;
//
//        final int size = (int) file.length();
//        FileOutputStream out = null;
//
//        final File listenFile = new File(getFilepathOf(FILE_TO_LISTEN_NAME));
//        if (listenFile.exists())
//            listenFile.delete();
//
//        final byte[] bytes_file = new byte[size];
//
//        try {
//            final BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//            buf.read(bytes_file, 0, size);
//            buf.close();
//
//            final String KEY = context.getResources().getString(R.string.key);
//            final Key secureKey = new SecretKeySpec(KEY.getBytes(), AES);
//            Cipher cipher;
//            try {
//                cipher = Cipher.getInstance(AES);
//                cipher.init(Cipher.DECRYPT_MODE, secureKey);
//                final byte[] decryptedData = cipher.doFinal(bytes_file);
//
//                out = new FileOutputStream(getFilepathOf(FILE_TO_LISTEN_NAME));
//                out.write(decryptedData);
//
//                out.close();
//
//                file.delete();
//                return true;
//            } catch (Exception e) {
//                LogWrapper.addLog(TAG, "[" + TAG + "/decryptAndRemoveListenFile()] e=");
//            }
//        } catch (Exception e) {
//            LogWrapper.addLog(TAG, "[" + TAG + "/decryptAndRemoveListenFile()] e1=");
//        }
//        return false;
//    }
//
//    /**
//     * 확장자 없는 파일명을 받아서 경로와 확장자를 포함한 파일명을 돌려준다.
//     *
//     * @param filename
//     * @return
//     */
//    public String getFilepathOf(final String filename) {
//        final String filepath = System.getenv("EXTERNAL_STORAGE");
//        final File file = new File(filepath, AUDIO_RECORDER_FOLDER);
//
//        if (!file.exists()) {
//            file.mkdirs(); //AudioRecorder 폴더가 없으면 만든다.
//        }
//
//        return (file.getAbsolutePath() + "/" + filename + AUDIO_RECORDER_FILE_EXT_WAV);
//    }
//
//    /**
//     * 부분 녹취
//     *
//     * @throws IOException
//     */
//    public void cutRecordingPartial() {
//
//        try {
//            if (OngoingCall.INSTANCE.callItem.RecPartial.equals("1")) {
//                // 부분녹취 시작, 시작 위치의 파일 index만 check한다.
//                final String firstFilePath = getFilepathOf(getFileNameWithoutExtension(OngoingCall.INSTANCE.callItem.rcdgFileNm));
//                final File srcFile = new File(firstFilePath);
//                final long recPartialStartIndex = srcFile.length();
//                OngoingCall.INSTANCE.callItem.RecPartialStartIndex = recPartialStartIndex;    // 시작위치 저장
//
//                HashMap msg = new HashMap<>();
//                msg.put("type", "DevRecPartial");
//                msg.put("nStartEnd", "1");
//                msg.put("szFileName", OngoingCall.INSTANCE.callItem.RecPartialFileName);
//                msg.put("nResult", "1");
//                App.socketService.sendMsg(msg);
//
//                LogWrapper.addLog(TAG, "[" + TAG + "/cutRecordingPartial()] COMMAND_DEVRECPARTIAL " + "1" + "|" + "|" + OngoingCall.INSTANCE.callItem.rcdgFileNm + "|" + "|" + "1" + ";");
//
//            } else if (OngoingCall.INSTANCE.callItem.RecPartial.equals("0")) {
//                // 부분녹취 종료, recordingItem.RecPartialStartIndex ~ 여기까지 파일을 부분녹취 파일로 생성한다.
//
//                final String firstFilePath = getFilepathOf(getFileNameWithoutExtension(OngoingCall.INSTANCE.callItem.rcdgFileNm));
//                final String destFilePath = getFilepathOf(getFileNameWithoutExtension(OngoingCall.INSTANCE.callItem.RecPartialFileName));
//
//                final File srcFile = new File(firstFilePath);
//                final File destFile = new File(destFilePath);
//                InputStream in = null;
//                OutputStream out = null;
//
//                try {
//                    in = new FileInputStream(srcFile);
//                    out = new FileOutputStream(destFile);
//                    final byte[] buf = new byte[1024];
//                    long totalCopiedSize = 0;
//                    int len;
//                    int i = 0;
//                    while ((len = in.read(buf)) > 0) {
//                        totalCopiedSize += len;
//                        if (totalCopiedSize < OngoingCall.INSTANCE.callItem.RecPartialStartIndex) {
//                            continue;
//                        }
//                        if (i++ == 0) {
//                            final byte[] header = {0x23, 0x21, 0x41, 0x4d, 0x52, 0x0a};
//                            out.write(header, 0, header.length);
//                            LogWrapper.addLog(TAG, "[" + TAG + "/cutRecordingPartial()] WRITE HEADER " + i);
//                        }
//                        out.write(buf, 0, len);
//                        LogWrapper.addLog(TAG, "[" + TAG + "/cutRecordingPartial()] WRITE " + i);
//                    }
//                    in.close();
//                    in = null;
//                    out.close();
//                    out = null;
//                    LogWrapper.addLog(TAG, "[" + TAG + "/cutRecordingPartial()] WRITE END " + i);
//
//                    EncryptFileOnly(destFilePath);
//
//                    HashMap msg = new HashMap<>();
//                    msg.put("type", "DevRecPartial");
//                    msg.put("nStartEnd", "0");
//                    msg.put("szFileName", OngoingCall.INSTANCE.callItem.RecPartialFileName);
//                    msg.put("nResult", "1");
//                    App.socketService.sendMsg(msg);
//
//                    LogWrapper.addLog(TAG, "[" + TAG + "/cutRecordingPartial()] COMMAND_DEVRECPARTIAL " + "0" + "|" + "|" + OngoingCall.INSTANCE.callItem.RecPartialFileName + "|" + "|" + "1" + ";");
//
//                    final String uploadUrl = App.getPrefUtil().getPrefString("UPLOAD_URL", "");
//                    final String uploadPath = App.getPrefUtil().getPrefString("UPLOAD_PATH", "");
//                    final ArrayList<String> params = new ArrayList<String>();
//                    params.add(OngoingCall.INSTANCE.callItem.RecPartialFileName + ".amr");    // local file name, 확장자 제외
//                    params.add(OngoingCall.INSTANCE.callItem.RecPartialFileName + ".amr");    // server file name, 확장자 제외
//                    params.add(uploadUrl);   // url
//                    params.add(uploadPath);    // server file directory로 통화일자는 나중에 sendFile()에서 붙인다.
//                    params.add("1");    // re-send -> "1"
//                    new FileSendTask().execute(params);
//
//                } catch (Exception e1) {
//                    LogWrapper.addLog(TAG, "[" + TAG + "/cutRecordingPartial()] e1=" + e1.getMessage());
//                } finally {
//                    if (in != null) in.close();
//                    if (out != null) out.close();
//                }
//            }
//        } catch (Exception e) {
//            LogWrapper.addLog(TAG, "[" + TAG + "/cutRecordingPartial()] e=" + App.getCommonUtil().stackTraceToString(e));
//        }
//    }
//
//    /**
//     * 부분 녹취 암호화
//     *
//     * @param fileName 확장자 포함한 전체 path
//     */
//    public void EncryptFileOnly(final String fileName) {
//        final String encFlag = App.getPrefUtil().getPrefString("ENC_FLAG", "1");  // default 는 암호화(이미 사용하고 있는 대리점 모두 암호화하고 있기 때문)
//        if (encFlag.equals("1")) {
//            LogWrapper.addLog(TAG, "[" + TAG + "/EncryptFileOnly()] ENC=1(암호화 함)");
//
//            final File file = new File(fileName);
//            final int size = (int) file.length();
//            FileOutputStream out = null;
//            FileInputStream in = null;
//
//            final byte[] bytes_file = new byte[size];
//
//            try {
//                in = new FileInputStream(file);
//                final BufferedInputStream buf = new BufferedInputStream(in);
//                buf.read(bytes_file, 0, size);
//                buf.close();
//
//                if (size > 10) {
//                    byte[] bytes_header = new byte[5];
//                    for (int i = 0; i < 5; i++)
//                        bytes_header[i] = bytes_file[i];
//                    final String header = new String(bytes_header, "UTF-8");
//                    if (!header.equals("#!AMR"))
//                        return;
//                }
//                final String KEY = context.getResources().getString(R.string.key);
//                final Key secureKey = new SecretKeySpec(KEY.getBytes(), AES);
//                Cipher cipher;
//                try {
//                    cipher = Cipher.getInstance(AES);
//                    cipher.init(Cipher.ENCRYPT_MODE, secureKey);
//                    final byte[] encryptedData = cipher.doFinal(bytes_file);
//
//                    out = new FileOutputStream(fileName);
//                    out.write(encryptedData);
//                    out.close();
//                } catch (Exception e) {
//                    LogWrapper.addLog(TAG, "[" + TAG + "/EncryptFileOnly()] e=" + App.getCommonUtil().stackTraceToString(e));
//                } finally {
//                    if (out != null)
//                        out.close();
//                    if (in != null)
//                        in.close();
//                }
//            } catch (FileNotFoundException e) {
//                LogWrapper.addLog(TAG, "[" + TAG + "/EncryptFileOnly()] FileNotFoundException, e=" + App.getCommonUtil().stackTraceToString(e));
//            } catch (IOException e) {
//                LogWrapper.addLog(TAG, "[" + TAG + "/EncryptFileOnly()] IOException, e=" + App.getCommonUtil().stackTraceToString(e));
//            }
//
//        } else {
//            LogWrapper.addLog(TAG, "[" + TAG + "/EncryptFileOnly()] ENC = 0");
//        }
//    }
//
//    BroadcastReceiver r;
//
//    public void sendSMS(final String smsNumber, final String smsText) {
//        final Intent intent = new Intent("SMS_SENT_ACTION");
//
//        if (r == null) {  // 브로드캐스트 리시버 1번만 생성되도록 수정
//            r = new BroadcastReceiver() {
//                @Override
//                public void onReceive(final Context context, final Intent intent) {
//                    final int result = getResultCode();
////                    final String retValue = COMMAND_DEVSMS + PIPE + PIPE + result + SEMICOLON;
//                    LogWrapper.addLog(TAG, "[" + TAG + "/sendSMS()] " + result);
//                    HashMap msg = new HashMap<>();
//                    msg.put("type", "DevSMS");
//                    msg.put("szState", String.valueOf(result));
//                    App.socketService.sendMsg(msg);
////                    IRServerSocket.SendData(retValue);
//                }
//            };
//            context.registerReceiver(r
//                    , new IntentFilter("SMS_SENT_ACTION"));
//        }
//
//        final SmsManager sm = SmsManager.getDefault();
//        //장문일 경우 메시지를 LMS로 보냄.
//        if (smsText.getBytes().length > 160) {
//            // LMS -> MMS 변경 iklee 2017-10-13
////			String operatorName = telephony.getNetworkOperatorName();
////			if(operatorName.equals("LG U+")){
//            Transaction transaction = new Transaction(context);
//            com.klinker.android.send_message.Message message = new com.klinker.android.send_message.Message(smsText, smsNumber);
//            transaction.sendNewMessage(message, (code, intent1) -> {
//                switch (code) {
//                    case -1:
//                        // 성공 시
//                        final int result = -1;
////                            final String retValue = COMMAND_DEVSMS + PIPE + PIPE + result + SEMICOLON;
//                        LogWrapper.addLog(TAG, "[" + TAG + "/onSendResult()] " + result);
//                        LogWrapper.addLog(TAG, "[" + TAG + "/sendSMS()] " + result);
//                        HashMap msg = new HashMap<>();
//                        msg.put("type", "DevSMS");
//                        msg.put("szState", String.valueOf(result));
//                        App.socketService.sendMsg(msg);
////                            IRServerSocket.SendData(retValue);
//                        break;
//                    default:
//                        // 실패 시
//                        LogWrapper.addLog(TAG, "[" + TAG + "/onSendResult()] Fail send mms ");
//                        break;
//                }
//            });
////			} else {
////			final ArrayList<String> parts = sm.divideMessage(smsText);
////			final int size = parts.size();
////			final ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(size);
////			final PendingIntent sentPI = PendingIntent.getBroadcast(serviceContext, 0, intent, 0);
////			for (int i = 0; i < size; i++) {
////				if(i == size-1)		// 이벤트 한 번만 발생하도록 하기 위해
////					sentIntents.add(sentPI);
////			}
////			sm.sendMultipartTextMessage(smsNumber, null, parts, sentIntents, null);
//        } else {
//            //단문일 경우 메시지를 SMS로 보냄.
//            final PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//            sm.sendTextMessage(smsNumber, null, smsText, sentIntent, null);
//        }
//    }
//
//    /**
//     * 녹취 파일 처음부터 메소드 호출될 때까지 부분 녹취파일 생성하는 메소드
//     *
//     * @param postfix
//     * @throws IOException
//     */
//    public void cutRecording(final String postfix) throws IOException {
//        CallItem callItem = OngoingCall.INSTANCE.callItem;
//        if (callItem == null || postfix.isEmpty()) return;
//
//        final String firstFilePath = getFilepathOf(getFileNameWithoutExtension(callItem.rcdgFileNm));
//        final String destFilePath = getFilepathOf(getFileNameWithoutExtension(callItem.rcdgFileNm) + postfix);
//        final File srcFile = new File(firstFilePath);
//        final File destFile = new File(destFilePath);
//        InputStream in = null;
//        OutputStream out = null;
//
//        try {
//            in = new FileInputStream(srcFile);
//            out = new FileOutputStream(destFile);
//            final long fileSize = srcFile.length();
//            final byte[] buf = new byte[1024];
//            long totalCopiedSize = 0;
//            int len;
//            /// 2017-11-10 amr header
//            final byte[] header = {0x23, 0x21, 0x41, 0x4d, 0x52, 0x0a};
//            out.write(header, 0, header.length);
//            while ((len = in.read(buf)) > 0 && totalCopiedSize < fileSize) {
//                if (totalCopiedSize == 0) {
//                    out.write(buf, 32, len - 32);
//                } else {
//                    out.write(buf, 0, len);
//                }
//                totalCopiedSize += len;
//            }
//            in.close();
//            in = null;
//            out.close();
//            out = null;
//
//            EncryptFileOnly(destFilePath);
//            final String fileName = destFile.getName().replace(AUDIO_RECORDER_FILE_EXT_WAV, "");
//            OnCutRecord(fileName);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (in != null) in.close();
//            if (out != null) out.close();
//        }
//    }
//
//    /**
//     * 부분 녹취(처음부터~CutCurrentRecord호출까지) 파일 저장 완료 이벤트
//     *
//     * @param filename
//     */
//    public void OnCutRecord(final String filename) {
//        HashMap msg = new HashMap<>();
//        msg.put("type", "DevCutRecord");
//        msg.put("filename", filename);
//        App.socketService.sendMsg(msg);
////        Log.d(IRUSBActivity.LOG_TAG_NAME, "[Recording/OnCutRecord()] COMMAND_DEVCUTRECORD " + Background.COMMAND_DEVCUTRECORD + "|" + "|" + filename + ";");
//    }
//
//    /**
//     * 당일 sms + lms + mms + 지능형sms(오차 가능성 있음) 총합
//     *
//     * @param strToday
//     * @return
//     */
//    public long getSmsMmsTotal(final String strToday) {
//        long totalCount = 0;
//        final long smsCount = getSMSCount(strToday);  //sms + lms
//        final long mmsCount = getMMSCount(strToday);  //mms
//
//        // 지능형mms인 경우는 strToday가 당일인 경우에만 의미가 있다.
//        int kctCount = 0;
//        final String kctDailyCount = App.getPrefUtil().getPrefString("KCT_DAILY_COUNT", "0"); // 당일 KCT로 지능형 MMS를 전송한 건 수(지능형 MMS로 전송한 것은 스마트폰에 이력이 남지 않는다.)
//        final String[] token = kctDailyCount.split("\\|");    //20160622|0
//        final String today = getCurrentDate();
//        if (today.equals(token[0])) {
//            kctCount = Integer.parseInt(token[1]);    // 당일이면 지능형 sms 전송 건 수를 읽는다. 날짜가 틀리면 0건으로 간주한다.(지능형 sms를 전송한 후에 당일 앱을 재 설치하면 0건으로 처리되므로 문제가 될 수 있다.)
//        }
//        totalCount = smsCount + mmsCount + kctCount;
//        return totalCount;
//    }
//
//    /* [Ver1.8] sms 당일 전송 갯수 가져오기(2016-06-08, kylee) */
//    private long getSMSCount(final String strToday) {
//        final ContentResolver contentResolver = context.getContentResolver();
//        final String[] projection = new String[]{"*"};
//        final Uri uri = Uri.parse("content://sms/sent");
//        final String sortOrder = Telephony.Sms.DEFAULT_SORT_ORDER;
//
//        long smsCount = 0;
//
//        Cursor cursor = null;
//        try {
//            final String selection = null;
//            cursor = contentResolver.query(uri, projection, selection, null, sortOrder);
//
//            if (cursor.moveToFirst()) {
//                for (int i = 0; i < cursor.getCount(); i++) {
//                    try {
//                        final String date = cursor.getString(cursor.getColumnIndexOrThrow("date")).toString();        // SMS 발송일시
//                        final Date dateSms = new Date(Long.valueOf(date));
//                        final SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
//                        final String strSmsDate = formatDate.format(dateSms);
//
//                        if (strSmsDate.equals(strToday)) {
//                            smsCount++;
//                        }
//                    } catch (Exception e1) {
//                        LogWrapper.addLog(TAG, "[" + TAG + "/getSMSCount()] e1=");
//                    }
//                    cursor.moveToNext();
//                }
//            }
//        } catch (Exception e) {
//            LogWrapper.addLog(TAG, "[" + TAG + "/getSMSCount()] e=");
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//
//        return smsCount;
//    }
//
//    private long getMMSCount(final String strToday) {
//
//        long mmsCount = 0;
//
//        final String selection = null;
//        Cursor cursor = null;
//
//        try {
//            cursor = context.getContentResolver().query(Uri.parse("content://mms/sent"), null, selection, null, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int cursorCount = cursor.getCount();
//                for (int i = 0; i < cursorCount; i++) {
//                    final int dateIndex = cursor.getColumnIndex("date");
//                    final long dateLong = cursor.getLong(dateIndex) * 1000;
//                    final Date dateMms = new Date(dateLong);
//                    final SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
//                    final String strMmsDate = formatDate.format(dateMms);
//
//                    if (strMmsDate.equals(strToday)) {
//                        mmsCount++;
//                    }
//                    cursor.moveToNext();
//                }
//            }
//        } catch (Exception e) {
//            LogWrapper.addLog(TAG, "[" + TAG + "/getMMSCount()] e=");
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return mmsCount;
//    }
//
//    /**
//     * 배터리 정보 확인
//     */
//    public void getBatteryPercentage() {
//        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
//            public void onReceive(Context context, Intent intent) {
//                context.unregisterReceiver(this);
//                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//                int level = 0;
//                if (currentLevel >= 0 && scale > 0) {
//                    level = (currentLevel * 100) / scale;
//                }
//                LogWrapper.addLog(TAG, "[" + TAG + "/getBatteryPercentage()] COMMAND_DEVBATTERYINFO=" + level);
//                HashMap msg = new HashMap<>();
//                msg.put("type", "DevBatteryInfo");
//                msg.put("level", level);
//                App.socketService.sendMsg(msg);
//            }
//        };
//        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        context.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
//    }
//
//    public void deleteDBFile(Context ctx) {
//        AppDataBase.getAppDatabase(ctx).callDao().getRemoveCallList().subscribe((v) -> {
//            for (String fn : v) {
//                File f = new File(System.getenv("EXTERNAL_STORAGE") + "/" + AUDIO_RECORDER_FOLDER + "/" + fn);
//                if (f.exists()) f.delete();
//            }
//            AppDataBase.getAppDatabase(ctx).callDao().deleteCallLog().subscribe(() -> {
//            }, e -> e.printStackTrace());
//        }, e -> e.printStackTrace());
//    }
//
//    public static class FileSendTask extends AsyncTask<ArrayList<String>, Void, Void> {
//        @Override
//        protected Void doInBackground(final ArrayList<String>... params) {
//            ArrayList<String> names = params[0];
//            // names[0] : 로컬 파일 명(확장자 제외)
//            // names[1] : 서버에 저장될 명(확장자 제외)
//            // names[2] : 업로드 url
//            // names[3] : 서버에 저장할 폴더
//            // names[4](if exists) : "1" 인 경우 자동 재전송
//
//            names.set(1, names.get(1).replaceAll("[\\\\|*\"?:/<>]", ""));        //서버에 저장할 때 파일명에 특수문자가 있으면 제거한다. 파일서버가 윈도우일때 특수문자가 있으면 파일이 수신되지 않는 문제가 있다.
//
//            /* [Ver1.6] 녹취 재전송시 사용하기 위한 서버의 녹취저장 폴더 저장(2016-05-23, kylee) */
//            App.getPrefUtil().putPrefString("UPLOAD_URL", names.get(2));
//            App.getPrefUtil().putPrefString("UPLOAD_PATH", names.get(3));
//            // 재전송 여부 판단
//            boolean isReSend = false;
//            if (names.size() > 4 && names.get(4).equals("1")) {
//                isReSend = true;
//            }
//            try {
//                LogWrapper.addLog("wireless", "[FileSendTask] 전송 시도");
//
//                final int iReturnCode = App.getCommonUtil().sendFile(names.get(0), names.get(1), names.get(2), names.get(3), isReSend);
//                App.getCommonUtil().saveFileSendResult(iReturnCode, names.get(0), names.get(1), names.get(2), names.get(3));
//            } catch (Exception e) {
//                LogWrapper.addLog("wireless", "[FileSendTask] e=" + App.getCommonUtil().stackTraceToString(e));
//            }
//            return null;
//        }
//    }
//
//    public Flowable<Double> uploadFile(final String localFileName, final String serverFileName, final String serverURL, final String fileSavePath, String cusCnsRespId) {
//        return Flowable.create(emitter -> {
//            try {
//                String serverFnm = serverFileName.replaceAll("[\\\\|*\"?:/<>]", "");
//
//                final int iReturnCode = App.getCommonUtil().
//                        sendFile(localFileName, serverFnm, serverURL, fileSavePath, false);
//                App.getCommonUtil().saveFileSendResult(iReturnCode, localFileName, serverFnm, serverURL, fileSavePath);
//                LogWrapper.addLog("uploadFile", localFileName + " - " + iReturnCode);
//                if (iReturnCode == SEND_FILE_SUCCESS) {
//                    // DB Update
//                    if (cusCnsRespId != null || localFileName.endsWith(".amr"))
//                        AppDataBase.getAppDatabase(context).callDao().updateRecSend(cusCnsRespId, "Y", localFileName).subscribe(() -> {
//                        }, e -> e.printStackTrace());
//                    emitter.onComplete();
//                } else {
//                    emitter.tryOnError(new Throwable());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                emitter.tryOnError(e);
//            }
//        }, BackpressureStrategy.LATEST);
//    }
//
//    public Flowable<Double> uploadFileForce(final String localFileName, final String serverFileName, final String serverURL, final String fileSavePath, String cusCnsRespId) {
//        return Flowable.create(emitter -> {
//            try {
//                String serverFnm = serverFileName.replaceAll("[\\\\|*\"?:/<>]", "");
//
//                final int iReturnCode = App.getCommonUtil().
//                        sendFile(localFileName, serverFnm, serverURL, fileSavePath, false);
//                App.getCommonUtil().saveFileSendResult(iReturnCode, localFileName, serverFnm, serverURL, fileSavePath);
//                LogWrapper.addLog("uploadFile", localFileName + " - " + iReturnCode);
//                if (iReturnCode == SEND_FILE_SUCCESS) {
//                    // DB Update
//                    if (cusCnsRespId != null || localFileName.endsWith(".amr"))
//                        AppDataBase.getAppDatabase(context).callDao().updateRecSend(cusCnsRespId, "F", localFileName).subscribe(() -> {
//                        }, e -> e.printStackTrace());
//                    emitter.onComplete();
//                } else {
//                    emitter.tryOnError(new Throwable());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                emitter.tryOnError(e);
//            }
//        }, BackpressureStrategy.LATEST);
//    }
//
//    /**
//     * 팝업 메시지
//     *
//     * @param isLogCert
//     * @param certState
//     * @param isError
//     */
//    public void showMessage(final Context ctx, final boolean isLogCert, final int certState, final boolean isError) {
//        ((MainActivity) ctx).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                boolean shouldShowAlert = false;
//                String message = "";
//
//                if (!isLogCert) {
//                    if (isError) {
//                        shouldShowAlert = true;
//                        message = "인증 과정에서 네트워크 연결 오류가 발생했습니다.\n인터넷 연결 상태를 확인해주세요.";
//                    } else if (certState == DeviceInfo.STATE_NOK) {
//                        shouldShowAlert = true;
//                        message = "정품 인증에 실패했습니다";
//                    }
//                }
//
//                if (shouldShowAlert) {
//                    new AlertDialog.Builder(ctx)
//                            .setTitle("정품 인증")
//                            .setMessage(message)
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(final DialogInterface dialog, final int which) {
//                                    dialog.cancel();
//                                }
//                            })
//                            .show();
//                }
//            }
//        });
//    }
//
//    public void showReconnectAlert(Context ctx) {
////        IRWireless.getCommonUtil().SetBlockMode(0);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//            ctx = context;
//        }
//        Context finalCtx = ctx;
//        AlertDialog alertDialog = new AlertDialog.Builder(ctx)
//                .setTitle("접속 실패")
//                .setMessage("중계 서버 접속에 실패하였습니다. 잠시 후 재접속 시도 혹은 관리자에게 문의하세요.")
//                .setPositiveButton("재접속", (dialog, which) -> {
//                    dialog.dismiss();
//
//                    Intent intent = new Intent(finalCtx, SocketService.class);
//                    intent.putExtra("IP", SERVER_URL_NODE);
//                    intent.putExtra("COMMAND_TYPE", SocketService.SOCKET_CONNECT);
//                    App.getCommonUtil().startService(intent);
//                })
//                //.setCancelable(false)
//                .setOnCancelListener(dialog -> {
////                        ((MainActivity)ctx).setCertOK(false);
//                }).create();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        }
//        alertDialog.show();
//    }
//
//    public interface AlertCallback {
//        void callBack();
//    }
//
//    public void showAlertDialog(Context context, String title, String contents, AlertCallback callback) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                context);
//
//        alertDialogBuilder
//                .setTitle(title)
//                .setMessage(contents)
//                .setCancelable(false)
//                .setPositiveButton("확인",
//                        (dialog, id) -> {
//                            if (callback != null) callback.callBack();
//                        });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
//
//    public void showConfirmDialog(Context context, String title, String contents, AlertCallback confirmCallback, AlertCallback cancelCallback) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                context);
//
//        alertDialogBuilder
//                .setTitle(title)
//                .setMessage(contents)
//                .setCancelable(false)
//                .setPositiveButton("예",
//                        (dialog, id) -> {
//                            if (confirmCallback != null) confirmCallback.callBack();
//                        })
//                .setNegativeButton("아니오",
//                        (dialog, which) -> {
//                            if (cancelCallback != null) cancelCallback.callBack();
//                        });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
//
//    AppCompatDialog progressDialog;
//
//    public void progressON(Activity activity) {
//        if (activity == null || activity.isFinishing()) {
//            return;
//        }
//        if (progressDialog != null && progressDialog.isShowing()) {
//            return;
//        }
//        progressDialog = new AppCompatDialog(activity);
//        progressDialog.setCancelable(false);
//        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        progressDialog.setContentView(R.layout.layout_progress);
//        progressDialog.show();
////        }
////        final ImageView img_loading_frame = progressDialog.findViewById(R.id.iv_frame_loading);
////        final BitmapDrawable frameAnimation = (BitmapDrawable) img_loading_frame.getBackground();
//////        img_loading_frame.post(() -> frameAnimation.start());
////        TextView tv_progress_message = progressDialog.findViewById(R.id.tv_progress_message);
////        if (!TextUtils.isEmpty(message)) {
////            tv_progress_message.setText(message);
////        }
//    }
//
////    public void progressSET(String message) {
////        if (progressDialog == null || !progressDialog.isShowing()) {
////            return;
////        }
////        TextView tv_progress_message = progressDialog.findViewById(R.id.tv_progress_message);
////        if (!TextUtils.isEmpty(message)) {
////            tv_progress_message.setText(message);
////        }
////    }
//
//    public void progressOFF() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }
//
//    public String getDateFormat(String date, String time) {
//        return date.substring(0, 4) + "-"
//                + date.substring(4, 6) + "-"
//                + date.substring(6, 8) + " "
//                + time.substring(0, 2) + ":"
//                + time.substring(2, 4) + ":"
//                + time.substring(4, 6);
//    }
//
//    public String getDateParsing(String date) {
//        return date.equals("") ? date :
//                date.length() == 8 ?
//                        (date.substring(0, 4) + "-"
//                                + date.substring(4, 6) + "-"
//                                + date.substring(6, 8) + " ") :
//                        (date.substring(0, 4) + "-"
//                                + date.substring(4, 6) + "-"
//                                + date.substring(6, 8) + " "
//                                + date.substring(8, 10) + ":"
//                                + date.substring(10, 12) + ":"
//                                + date.substring(12, 14));
//    }
//
//    public Date parseString(String dateString) throws ParseException {
//        return format_datetime.parse(dateString);
//    }
//
//    public String subtractDate(String main, String sub) throws ParseException {
//        return String.valueOf((parseString(main).getTime() - parseString(sub).getTime()) / 1000);
//    }
//
//    public String subtractSecond(String main, long second) throws ParseException {
//        return format_datetime.format(new Date(parseString(main).getTime() - (second * 1000) / 1000));
//    }
//
//    public String phoneNumberHyphenAdd(String num, String mask) {
//
//        String formatNum = "";
//        if (num.equals("")) return formatNum;
//        num = num.replaceAll("-", "").replaceAll("�", "");
//
//        if (num.length() == 11) {
//            if (mask.equals("Y")) {
//                formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-****");
//            } else {
//                formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
//            }
//        } else if (num.length() == 8) {
//            formatNum = num.replaceAll("(\\d{4})(\\d{4})", "$1-$2");
//        } else {
//            if (num.indexOf("02") == 0) {
//                if (mask.equals("Y")) {
//                    formatNum = num.replaceAll("(\\d{2})(\\d{3,4})(\\d{4})", "$1-$2-****");
//                } else {
//                    formatNum = num.replaceAll("(\\d{2})(\\d{3,4})(\\d{4})", "$1-$2-$3");
//                }
//            } else {
//                if (mask.equals("Y")) {
//                    formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-****");
//                } else {
//                    formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
//                }
//            }
//        }
//        return formatNum;
//    }
//
//    public Activity getActivity() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
//        Class activityThreadClass = Class.forName("android.app.ActivityThread");
//        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
//        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
//        activitiesField.setAccessible(true);
//
//        Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
//        if (activities == null)
//            return null;
//
//        for (Object activityRecord : activities.values()) {
//            Class activityRecordClass = activityRecord.getClass();
//            Field pausedField = activityRecordClass.getDeclaredField("paused");
//            pausedField.setAccessible(true);
//            if (!pausedField.getBoolean(activityRecord)) {
//                Field activityField = activityRecordClass.getDeclaredField("activity");
//                activityField.setAccessible(true);
//                Activity activity = (Activity) activityField.get(activityRecord);
//                return activity;
//            }
//        }
//
//        return null;
//    }
//
//    public String maskingName(String str) {
//        String replaceString = str;
//
//        String pattern = "";
//        if (str.length() == 2) {
//            pattern = "^(.)(.+)$";
//        } else {
//            pattern = "^(.)(.+)(.)$";
//        }
//
//        Matcher matcher = Pattern.compile(pattern).matcher(str);
//
//        if (matcher.matches()) {
//            replaceString = "";
//
//            for (int i = 1; i <= matcher.groupCount(); i++) {
//                String replaceTarget = matcher.group(i);
//                if (i == 2) {
//                    char[] c = new char[replaceTarget.length()];
//                    Arrays.fill(c, '*');
//
//                    replaceString = replaceString + String.valueOf(c);
//                } else {
//                    replaceString = replaceString + replaceTarget;
//                }
//
//            }
//        }
//
//        return replaceString;
//    }
//
//    public void logOutProc(Activity ctx) {
//        //202011 Oreo 강제 Legacy녹음사용 변경 28 > 26
//        if (Build.VERSION.SDK_INT >= 26) {
//            App.getCommonUtil().endCall(App.getContext());
//        } else {
//            if (OngoingCall.INSTANCE.getCall() != null)
//                OngoingCall.INSTANCE.hangup();
//        }
//        App.getCommonUtil().showConfirmDialog(ctx, "확인", "정말로 로그아웃 하시겠습니까?", () -> {
//            App.getCommonUtil().progressON(ctx);
//            MeritzAPI.handleCrpMchyUseAth("2", App.getDeviceInfo().getBirth())
//                    .subscribe(
//                            (json) -> {
//                                //202011 Oreo 강제 Legacy녹음사용 변경 28 > 26
//                                if (Build.VERSION.SDK_INT >= 26) {
//                                    App.getCommonUtil().endCall(App.getContext());
//                                } else {
//                                    if (OngoingCall.INSTANCE.getCall() != null)
//                                        OngoingCall.INSTANCE.hangup();
//                                }
//
//                                App.getDeviceInfo().isLogin = false;
//
//                                Intent intent = new Intent(ctx, SocketService.class);
//                                intent.putExtra("COMMAND_TYPE", SocketService.SOCKET_DISCONNECT);
//                                App.getCommonUtil().startService(intent);
//
//                                App.getCommonUtil().progressOFF();
//                                Intent intent_ = new Intent(ctx, MainActivity.class);
//                                intent_.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                ctx.startActivity(intent_);
//                                ctx.finish();
//                            },
//                            (v) -> {
//                                //202011 Oreo 강제 Legacy녹음사용 변경 28 > 26
//                                if (Build.VERSION.SDK_INT >= 26) {
//                                    App.getCommonUtil().endCall(App.getContext());
//                                } else {
//                                    if (OngoingCall.INSTANCE.getCall() != null)
//                                        OngoingCall.INSTANCE.hangup();
//                                }
//
//                                App.getCommonUtil().progressOFF();
//                                App.getCommonUtil().showAlertDialog(ctx, "경고", "로그아웃 실패.", null);
//                            });
//        }, null);
//    }
//
//    public long getMillisecond(String date) throws ParseException {
//        return format_date.parse(date.replaceAll("-", "")).getTime();
//    }
//
//    public String getBirthAndAge(String birth, boolean isDetail) {
//        return birth.equals("") ? birth :
//                birth.length() == 6 ? isDetail ?
//                        "생년월일 : 19"
//                                + birth.substring(0, 2) + "-"
//                                + birth.substring(2, 4) + "-"
//                                + birth.substring(4, 6) + " ( " + getInsuAge(birth) + "세 )"
//                        : "( " + getInsuAge(birth) + "세 )"
//                        : isDetail ?
//                        "생년월일 : 19"
//                                + birth.substring(0, 4) + "-"
//                                + birth.substring(4, 6) + "-"
//                                + birth.substring(6, 8) + " ( " + getInsuAge(birth) + "세 )"
//                        : "( " + getInsuAge(birth) + "세 )";
//    }
//
//    public int getAge(String birth) {
//        Calendar current = Calendar.getInstance();
//        int currentYear = current.get(Calendar.YEAR);
//        int currentMonth = current.get(Calendar.MONTH) + 1;
//        int currentDay = current.get(Calendar.DAY_OF_MONTH);
//
//        int birthYear;
//        int birthMonth;
//        int birthDay;
//
//        if (birth.length() == 6) {
//            birthYear = Integer.parseInt("19" + birth.substring(0, 2));
//            birthMonth = Integer.parseInt(birth.substring(2, 4));
//            birthDay = Integer.parseInt(birth.substring(4, 6));
//        } else {
//            birthYear = Integer.parseInt(birth.substring(0, 4));
//            birthMonth = Integer.parseInt(birth.substring(4, 6));
//            birthDay = Integer.parseInt(birth.substring(6, 8));
//        }
//
//        int age = currentYear - birthYear;
//        // 생일 안 지난 경우 -1
//        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay)
//            age--;
//
//        return age;
//    }
//
//    public int getInsuAge(String sBirth) {
//        sBirth = Integer.parseInt(sBirth.substring(0, 2))
//                > Integer.parseInt(App.getCommonUtil().getCurrentDate().substring(2, 4)) ?
//                "19" + sBirth : "20" + sBirth;
//        int m = getMonthDiff(sBirth);
//        int year = m / 12;
//        int month = m % 12;
//        return month >= 6 ? year + 1 : year;
//    }
//
//    private int getMonthDiff(String sBirth) {
//        Calendar date = StringToDate(sBirth);
//        Calendar date2 = Calendar.getInstance();
//        int years = date2.get(Calendar.YEAR) - date.get(Calendar.YEAR);
//        int months = date2.get(Calendar.MONTH) - date.get(Calendar.MONTH);
//        int days = date2.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH);
//
//        return (years * 12 + months + (days >= 0 ? 0 : -1));
//    }
//
//    private Calendar StringToDate(String sDate) {
//        try {
//            if (sDate == null || sDate.equals("")) return null;
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(format_date.parse(sDate.substring(0, 8)));
//            return cal;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // 개발계로 접속할지 말지 결정해주는 함수
//    // /sdcard/mertiz/MeritzMobileEnv_Dev.txt 유무 ( 있으면 테스트계, 없으면 운영계 )
//    public void chkIsDev() {
//        try {
//            File files = new File(devChkFilePath);
//            RetrofitAdapter.DEBUG = files.exists();
//            LogWrapper.addLog("chkIsDev -", "" + RetrofitAdapter.DEBUG);
//        } catch (Exception e) {
//            RetrofitAdapter.DEBUG = false;
//            LogWrapper.addLog("chkIsDev error - 1 ", App.getCommonUtil().stackTraceToString(e));
//            e.printStackTrace();
//        }
//
//        setEnvURL();
//    }
//
//    // 서버 최종 주소 설정
//    private void setEnvURL() {
//        RetrofitAdapter.resetInstance();
//        if (RetrofitAdapter.DEBUG) {
//            //"https://192.168.107.96:15945/"; - 개발계
//            RetrofitAdapter.SERVER_URL = "https://mjtmtest.meritzfire.com/"; // "https://210.105.196.97:15945/"; //"mjtmtest.meritzfire.com:15945/"; "https://192.168.107.96:15945/";
//            RetrofitAdapter.SERVER_URL_NODE = "https://mjtmcalltest.meritzfire.com:3000/"; // "https://mjtmcalltest.meritzfire.com:3000/";
//            RetrofitAdapter.RECSERVER_URL = "https://mjtmrectest.meritzfire.com/"; // "https://mjtmrectest.meritzfire.com/";
//        } else {
//            //운영
//            RetrofitAdapter.SERVER_URL = "https://mjtm.meritzfire.com/";
//            RetrofitAdapter.SERVER_URL_NODE = "https://mjtmcall.meritzfire.com:3000/";
//            RetrofitAdapter.RECSERVER_URL = "https://mjtmrec.meritzfire.com/";//http://192.168.42.113:8080/";
//
//            //TEST
//            //RetrofitAdapter.SERVER_URL = "https://mjtmtest.meritzfire.com/"; // "https://210.105.196.97:15945/"; //"mjtmtest.meritzfire.com:15945/"; "https://192.168.107.96:15945/";
//            //RetrofitAdapter.SERVER_URL_NODE = "https://mjtmcalltest.meritzfire.com:3000/"; // "https://mjtmcalltest.meritzfire.com:3000/";
//            //RetrofitAdapter.RECSERVER_URL = "https://mjtmrec.meritzfire.com/"; // "https://mjtmrectest.meritzfire.com/";
//        }
//    }
//
//    public JSONArray sort(JSONArray array, Comparator c) {
//        List asList = new ArrayList(array.length());
//        for (int i = 0; i < array.length(); i++) {
//            asList.add(array.opt(i));
//        }
//        Collections.sort(asList, c);
//        JSONArray res = new JSONArray();
//        for (Object o : asList) {
//            res.put(o);
//        }
//        return res;
//    }
//
//    public boolean isRightPhoneNumber(String address) {
//        // 2019-09-17 인증번호 발신번호 예외 추가 ( 앞에 02- 붙이면 신규등록 잘 됨. 허나, 해당 번호로만 나가는지와 번호 변경의 위험성으로 우선 보류 )
////        if(address.equals("1599-0011")) return true;
//        // 202002 해당번호 매티스 문자 전달 추가 jhlee
//        if (address.equals("02-1577-7711")) return true;
//        if (address.equals("02-1566-7711")) return true;
//        if (address.equals("02-1599-0011")) return true;
//        String pattern = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
//        return Pattern.matches(pattern, address) && !address.equals("010-0000-0000") && !address.equals("010-000-0000");
//    }
//
//    public String logReplaceNumberToAsta(String log) {
//        String formatNum = "";
//        if (log == null || log.equals("")) return formatNum;
//        formatNum = log.replaceAll("\"(number|rciTelNo|cusTelNo|crpMchyTelNo)\":\"(\\d{2,3}|\\d{2,3}-)(\\d{3,4}|\\d{3,4}-)(\\d{4})\"", "$2$3****");
//        return formatNum;
//    }
//
//    /**
//     * 지정된 폴더를 Zip 파일로 압축한다.
//     *
//     * @param sourcePath - 압축 대상 디렉토리
//     * @param output     - 저장 zip 파일 이름
//     * @throws Exception
//     */
//    public void zip(String sourcePath, String output) throws Exception {
//
//        // 압축 대상(sourcePath)이 디렉토리나 파일이 아니면 리턴한다.
//        File sourceFile = new File(sourcePath);
//        if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
//            throw new Exception("압축 대상의 파일을 찾을 수가 없습니다.");
//        }
//
//        // output 의 확장자가 zip이 아니면 리턴한다.
//        //if (!(StringUtils.substringAfterLast(output, ".")).equalsIgnoreCase("zip")) {
//        //    throw new Exception("압축 후 저장 파일명의 확장자를 확인하세요");
//        //}
//
//        FileOutputStream fos = null;
//        BufferedOutputStream bos = null;
//        ZipOutputStream zos = null;
//
//        try {
//            fos = new FileOutputStream(output); // FileOutputStream
//            bos = new BufferedOutputStream(fos); // BufferedStream
//            zos = new ZipOutputStream(bos); // ZipOutputStream
//            zos.setLevel(8); // 압축 레벨 - 최대 압축률은 9, 디폴트 8
//
//            zipEntry(sourceFile, sourcePath, zos); // Zip 파일 생성
//            zos.finish(); // ZipOutputStream finish
//        } finally {
//            if (zos != null) {
//                zos.close();
//            }
//            if (bos != null) {
//                bos.close();
//            }
//            if (fos != null) {
//                fos.close();
//            }
//        }
//    }
//
//    /**
//     * 압축
//     *
//     * @param sourceFile
//     * @param sourcePath
//     * @param zos
//     * @throws Exception
//     */
//    public void zipEntry(File sourceFile, String sourcePath, ZipOutputStream zos) throws Exception {
//        int BUFFER_SIZE = 1024 * 2;
//        // sourceFile 이 디렉토리인 경우 하위 파일 리스트 가져와 재귀호출
//        if (sourceFile.isDirectory()) {
//            if (sourceFile.getName().equalsIgnoreCase(".metadata")) { // .metadata 디렉토리 return
//                return;
//            }
//            File[] fileArray = sourceFile.listFiles(); // sourceFile 의 하위 파일 리스트
//            for (int i = 0; i < fileArray.length; i++) {
//                zipEntry(fileArray[i], sourcePath, zos); // 재귀 호출
//            }
//        } else { // sourcehFile 이 디렉토리가 아닌 경우
//            BufferedInputStream bis = null;
//
//            try {
//                String sFilePath = sourceFile.getPath();
//                Log.i("aa", sFilePath);
//                //String zipEntryName = sFilePath.substring(sourcePath.length() + 1, sFilePath.length());
//                StringTokenizer tok = new StringTokenizer(sFilePath, "/");
//
//                int tok_len = tok.countTokens();
//                String zipEntryName = tok.toString();
//                while (tok_len != 0) {
//                    tok_len--;
//                    zipEntryName = tok.nextToken();
//                }
//                bis = new BufferedInputStream(new FileInputStream(sourceFile));
//
//                ZipEntry zentry = new ZipEntry(zipEntryName);
//                zentry.setTime(sourceFile.lastModified());
//                zos.putNextEntry(zentry);
//
//                byte[] buffer = new byte[BUFFER_SIZE];
//                int cnt = 0;
//
//                while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
//                    zos.write(buffer, 0, cnt);
//                }
//                zos.closeEntry();
//            } finally {
//                if (bis != null) {
//                    bis.close();
//                }
//            }
//        }
//    }
//
//    public void showHourPicker(Context ctx, TimePickerDialog.OnTimeSetListener callBack) {
//        final Calendar myCalender = Calendar.getInstance();
//        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
//        int minute = myCalender.get(Calendar.MINUTE);
//
//
////        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
////            @Override
////            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
////                if (view.isShown()) {
////                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
////                    myCalender.set(Calendar.MINUTE, minute);
////
////                }
////            }
////        };
//        TimePickerDialog timePickerDialog = new TimePickerDialog(ctx, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, callBack, hour, minute, true);
//        timePickerDialog.setTitle("Choose hour:");
//        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        timePickerDialog.show();
//    }
//
//    public String stackTraceToString(Throwable e) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(Optional.ofNullable(e.getMessage()).orElse(""));
//        sb.append("\n");
//        for (StackTraceElement element : e.getStackTrace()) {
//            sb.append(element.toString());
//            sb.append("\n");
//        }
//        return sb.toString();
//    }
//
//    public void removeOldLogFiles() {
//        // mertiz 로그 파일 경로
//        File oldMeritzLogPath = new File(System.getenv("EXTERNAL_STORAGE") + File.separator + "mertiz");
//        if (oldMeritzLogPath.exists()) {
//            for (File f : oldMeritzLogPath.listFiles()) {
//                String fileName = f.getName();
//                if (fileName.startsWith("MeritzMobile")) f.delete();
//            }
//        }
//
//        File oldRecorderLogPath = new File(System.getenv("EXTERNAL_STORAGE") + File.separator + "IRRecorder");
//        if (oldRecorderLogPath.exists()) {
//            for (File f : oldRecorderLogPath.listFiles()) {
//                String fileName = f.getName();
//                if (fileName.startsWith("IRLog") || fileName.startsWith("logs")) f.delete();
//            }
//        }
//    }
//
//    public boolean isLocked() {
//        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        boolean locked = myKM.inKeyguardRestrictedInputMode();
//        LogWrapper.addLog("isLocked!", locked + "");
//        return locked;
//    }
//
//    public String getFilePathFromFileName(String fileName) {
//        fileName = fileName.replace("Cor", "");
//        return "/" +
//                fileName.substring(0, 6) +
//                "/" +
//                fileName.substring(0, 8) +
//                "/" +
//                fileName.substring(14, 23);
//    }
//
//    public int getLastCallDetails(Context context) {
//
//        Uri contacts = CallLog.Calls.CONTENT_URI;
//        try {
//
//            Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");
//
//            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
//            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
//            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
//            int incomingtype = managedCursor.getColumnIndex(String.valueOf(CallLog.Calls.INCOMING_TYPE));
//
//            if (managedCursor.moveToFirst()) { // added line
//
//                while (managedCursor.moveToNext()) {
//                    String callType;
//                    String phNumber = managedCursor.getString(number);
//                    if (incomingtype == -1) {
//                        callType = "incoming";
//                    } else {
//                        callType = "outgoing";
//                    }
//                    String callDate = managedCursor.getString(date);
//                    String callDayTime = new Date(Long.valueOf(callDate)).toString();
////                    format_date.format(callDayTime);
//                    String callDuration = managedCursor.getString(duration);
//
//                }
//            }
//            managedCursor.close();
//
//        } catch (SecurityException e) {
//            Log.e("Security Exception", "User denied call log permission");
//
//        }
//
//        return 0;
//
//    }
//
//    public void endCall(Context context) {
//        try {
//            //202102 Android 10 지원
//            if (Build.VERSION.SDK_INT >= 29) {
//                TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
//                telecomManager.endCall();
//            } else {
//                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                Class clazz = Class.forName(telephonyManager.getClass().getName());
//                Method method = clazz.getDeclaredMethod("getITelephony");
//                method.setAccessible(true);
//                ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
//                telephonyService.endCall();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogWrapper.addLog("IncomingCallBroadcastReceiver", App.getCommonUtil().stackTraceToString(e));
//        }
//    }
//
//    public void answerCall(Context context) {
//        try {
//            // set the logging tag constant; you probably want to change this
//            final String LOG_TAG = "TelephonyAnswer";
//
//            TelecomManager tm = (TelecomManager) context
//                    .getSystemService(Context.TELECOM_SERVICE);
//
//            try {
//                if (tm == null) {
//                    // this will be easier for debugging later on
//                    throw new NullPointerException("tm == null");
//                }
//
//                // do reflection magic
////                tm.getClass().getMethod("answerRingingCall").invoke(tm);
//                tm.acceptRingingCall();
//            } catch (Exception e) {
//                // we catch it all as the following things could happen:
//                // NoSuchMethodException, if the answerRingingCall() is missing
//                // SecurityException, if the security manager is not happy
//                // IllegalAccessException, if the method is not accessible
//                // IllegalArgumentException, if the method expected other arguments
//                // InvocationTargetException, if the method threw itself
//                // NullPointerException, if something was a null value along the way
//                // ExceptionInInitializerError, if initialization failed
//                // something more crazy, if anything else breaks
//
//                // TODO decide how to handle this state
//                // you probably want to set some failure state/go to fallback
//                Log.e(LOG_TAG, "Unable to use the Telephony Manager directly.", e);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogWrapper.addLog("IncomingCallBroadcaseReceiver", App.getCommonUtil().stackTraceToString(e));
//        }
//    }
//
//    public boolean checkFileResend(String fileName) {
//        return new File(System.getenv("EXTERNAL_STORAGE") + "/" + App.getCommonUtil().AUDIO_RECORDER_FOLDER + "/" + fileName).exists();
//    }
//
//    public long checkFileSize(String fileName) {
//        return new File(System.getenv("EXTERNAL_STORAGE") + "/" + App.getCommonUtil().AUDIO_RECORDER_FOLDER + "/" + fileName).length();
//    }
//
//    public Completable convertMp4ToAmr(String src, String dest) {
//        return Completable.create((emitter) -> {
//            String[] cmds = {
//                    "-i",
//                    src,
//                    "-vn",
//                    "-ar",
//                    "8000",
//                    "-ac",
//                    "1",
//                    "-ab",
//                    "12.2k",
//                    "-f",
//                    "amr",
//                    dest,
//            };
//
//            FFmpeg.execute(cmds);
//
//            //202011 Oreo 강제 Legacy녹음사용 변경 (IRRecorder 변경에 따른 ffmpeg 버전 변경)
//            //Log.d("ffmpeg!!", String.format(Locale.KOREA, "Command execution failed with ffmpegCode=%d and output=%s.", FFmpeg.getLastReturnCode(), FFmpeg.getLastCommandOutput()));
//            Log.d("ffmpeg!!", String.format(Locale.KOREA, "Command execution failed with ffmpegCode=%d and output=%s.", Config.getLastReturnCode(), Config.getLastCommandOutput()));
///*
//            switch (FFmpeg.getLastReturnCode()) {
//                case FFmpeg.RETURN_CODE_SUCCESS:
//                    Log.d("ffmpeg", "success conv : " +  dest);
////                    File file = new File(convertFilePath);
//                    emitter.onComplete();
//                    break;
//                case FFmpeg.RETURN_CODE_CANCEL:
//                    emitter.onError(new RuntimeException("Error"));
//                    break;
//            }
// */
//            switch (Config.getLastReturnCode()) {
//                case Config.RETURN_CODE_SUCCESS:
//                    Log.d("ffmpeg", "success conv : " + dest);
////                    File file = new File(convertFilePath);
//                    emitter.onComplete();
//                    break;
//                case Config.RETURN_CODE_CANCEL:
//                    emitter.onError(new RuntimeException("Error"));
//                    break;
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static String getPath(final Context context, final Uri uri) {
//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//        // DocumentProvider
//        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            // ExternalStorageProvider
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
//                // TODO handle non-primary volumes
//            }
//            // DownloadsProvider
//            else if (isDownloadsDocument(uri)) {
//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//                return getDataColumn(context, contentUri, null, null);
//            }
//            // MediaProvider
//            else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//                Uri contentUri = null;
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[]{split[1]};
//                return getDataColumn(context, contentUri, selection, selectionArgs);
//            }
//        }
//        // MediaStore (and general)
//        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            return getDataColumn(context, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }
//
//    /**
//     * Get the value of the data column for this Uri. This is useful for * MediaStore Uris, and other file-based ContentProviders. *
//     * * @param context The context. * @param uri The Uri to query. * @param selection (Optional) Filter used in the query.
//     * * @param selectionArgs (Optional) Selection arguments used in the query. * @return The value of the _data column, which is typically a file path.
//     */
//    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {column};
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int column_index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(column_index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }
//
//    /**
//     * @param uri The Uri to check. * @return Whether the Uri authority is ExternalStorageProvider.
//     */
//    public static boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check. * @return Whether the Uri authority is DownloadsProvider.
//     */
//    public static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check. * @return Whether the Uri authority is MediaProvider.
//     */
//    public static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//
//    public boolean sendValidate(String date) {
//        boolean rst = LocalDateTime.now().isBefore(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusMinutes(5));
//        Log.d("isSend??", rst + "");
//        return rst;
//    }
//
//    //202002 로그인시 녹취파일 재전송 jhlee
//    public void resendRecordFile(Context ctx) {
//        CompositeDisposable disposables = new CompositeDisposable();
//
//        disposables.add(AppDataBase.getAppDatabase(ctx).callDao().getResendCall(App.getDeviceInfo().getSabun(), getDateEspecailly())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((callLogs) -> {
//                    //202103 amr이 없는경우 m4a 파일 찾아서 강제 변환 재전송 (listLoginResendConvertFile 초기화)
//                    listLoginResendConvertFile.clear();
//                    iResendPos = 0;
//
//                    // 사이즈 검증
//                    if (callLogs.size() > 0) {
//                        for (int i = 0; i < callLogs.size(); i++) {
//                            if (callLogs.get(i).callEndDate.length() == 14) {
//
//                                // 이쪽에 있어서 문제. LogWrapper.addLog("resendRecordFile", "resend filename : " + callLog.get(i).fileName);
//                                String strCallEndDate = callLogs.get(i).callEndDate;
//                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                                Date dateCallEnd = sdf.parse(strCallEndDate);
//                                String paramEndDate = sdf2.format(dateCallEnd);
//
//                                if (App.getCommonUtil().sendValidate(paramEndDate)) {
//                                } else {
//                                    String filename = callLogs.get(i).fileName;
//
//                                    if (checkFileResend(filename)) {
//                                        LogWrapper.addLog("resendRecordFile", "resend filename : " + filename);
//                                        try {
//                                            encryptFile(filename);
//                                        } catch (Exception e) {
//                                            LogWrapper.addLog("pre re upload enc", App.getCommonUtil().stackTraceToString(e));
//                                        }
//                                        uploadFile(filename, filename, RECSERVER_URL + "httpup/RefHome.jsp", "", callLogs.get(i).callId)
//                                                .subscribeOn(Schedulers.io())
//                                                .observeOn(AndroidSchedulers.mainThread())
//                                                .subscribe(v2 -> {
//                                                }, e -> e.printStackTrace());
//                                    } else {
//                                        //202103 amr이 없는경우 m4a 파일 찾아서 강제 변환 재전송
//                                        if (callLogs.get(i).callDuration > 0) {
//                                            if (Build.VERSION.SDK_INT >= 26) {
//                                                Log.d("ForceReSend", "amrFileName : " + callLogs.get(i).fileName);
//                                                String strTalkStartDate = callLogs.get(i).talkStartDate;
//                                                if (strTalkStartDate.length() == 14 && strCallEndDate.length() == 14) {
//                                                    String strLegacyFileName = findLegacyRecordFile(strTalkStartDate, strCallEndDate);
//                                                    if (!"".equals(strLegacyFileName)) {
//                                                        String[] arrLoginResendConvertFile = new String[3];
//
//                                                        arrLoginResendConvertFile[0] = callLogs.get(i).callId;
//                                                        arrLoginResendConvertFile[1] = callLogs.get(i).fileName;
//                                                        arrLoginResendConvertFile[2] = strLegacyFileName;
//
//                                                        listLoginResendConvertFile.add(arrLoginResendConvertFile);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    //202103 amr이 없는경우 m4a 파일 찾아서 강제 변환 재전송 (동시에 m4a를 변환하는경우 APP이 죽기때문에 한개씩 작업한다.)
//                    if (listLoginResendConvertFile.size() > 0) {
//                        convertResendFile();
//                    }
//                    LogWrapper.addLog("resendRecordFile(count)", "" + callLogs.size());
//
//                }, (e) -> {
//                    if (e instanceof EmptyResultSetException) {
//                    } else {
//                        App.getCommonUtil().showAlertDialog(ctx, "오류", "오류.....", null);
//                        LogWrapper.addLog("resendRecordFile - 1", App.getCommonUtil().stackTraceToString(e));
//                    }
//                    e.printStackTrace();
//
//                }));
//    }
//
//    //202103 amr이 없는경우 m4a 파일 찾아서 강제 변환 재전송 (m4a 파일 찾는 함수)
//    public String findLegacyRecordFile(String strTalkStartDate, String strCallEndDate) {
//        String strLegacyFileName = "";
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//            String m4aFilePath = App.getCommonUtil().getLegacyRecorderFolder() + File.separator;
//            File m4aFolder = new File(m4aFilePath);
//            if (m4aFolder.isDirectory()) {
//                File[] m4aFiles = m4aFolder.listFiles();
//                for (int j = 0; j < m4aFiles.length; j++) {
//                    String[] arrFileName = m4aFiles[j].getName().split("_");
//                    File m4aFile = new File(m4aFiles[j].getAbsolutePath());
//                    if (arrFileName.length > 2) {
//                        Date dateTalkStart = sdf.parse(strTalkStartDate);
//                        Date dateCallEnd = sdf.parse(strCallEndDate);
//
//                        String[] arrFileNameExt = arrFileName[arrFileName.length - 1].split("\\.");
//                        String strRecCreateDate = "20" + arrFileName[arrFileName.length - 2] + arrFileNameExt[0];
//
//                        Date dateRecCreate = sdf.parse(strRecCreateDate);
//
//                        int dateCompare1 = dateTalkStart.compareTo(dateRecCreate);
//                        int dateCompare2 = dateRecCreate.compareTo(dateCallEnd);
//                        Log.d("ForceReSend", "strRecCreateDate : " + strRecCreateDate + ", strTalkStartDate : " + strTalkStartDate + ", strCallEndDate : " + strCallEndDate);
//                        Log.d("ForceReSend", "dateCompare1 : " + dateCompare1 + ", dateCompare2 : " + dateCompare2);
//                        if (dateCompare1 <= 0 && dateCompare2 <= 0) {
//                            strLegacyFileName = m4aFiles[j].getName();
//                            Log.d("ForceSend", "strLegacyFileName : " + strLegacyFileName);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//
//        }
//        return strLegacyFileName;
//    }
//
//    //202103 amr이 없는경우 m4a 파일 찾아서 강제 변환 재전송
//    public void convertResendFile() {
//        if (iResendPos < listLoginResendConvertFile.size()) {
//            String arrLoginResendConvertFile[] = listLoginResendConvertFile.get(iResendPos);
//            convertResendFileExec(arrLoginResendConvertFile[0], arrLoginResendConvertFile[1], arrLoginResendConvertFile[2]);
//        } else {
//            listLoginResendConvertFile.clear();
//            iResendPos = 0;
//        }
//    }
//
//    //202103 amr이 없는경우 m4a 파일 찾아서 강제 변환 재전송
//    public void convertResendFileExec(String callId, String amrFileName, String legacyFileName) {
//        String m4aFileFullPath = App.getCommonUtil().getLegacyRecorderFolder() + legacyFileName;
//        String amrFileFullPath = System.getenv("EXTERNAL_STORAGE") + File.separator + App.getCommonUtil().AUDIO_RECORDER_FOLDER + File.separator + amrFileName;
//
//        String amrTempConvertFileName = System.getenv("EXTERNAL_STORAGE") + File.separator + App.getCommonUtil().AUDIO_RECORDER_FOLDER + File.separator + "Temp_" + amrFileName;
//
//        //amr파일이 존재하는 경우 삭제한 후 변환한다.
//        SimpleDateFormat backupFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//        String backupTime = backupFormat.format(Calendar.getInstance().getTime());
//
//        File sendFile = new File(amrFileFullPath);
//        if (sendFile.exists()) {
//            File renameFile = new File(amrFileFullPath + "_bak" + backupTime);
//            sendFile.renameTo(renameFile);
//            sendFile.delete();
//        }
//        //변환하는 임시파일이 존재하는 경우 삭제한 후 변환한다.
//        File tempFile = new File(amrTempConvertFileName);
//        if (tempFile.exists()) {
//            File renameTempFile = new File(amrTempConvertFileName + "_bak" + backupTime);
//            tempFile.renameTo(renameTempFile);
//            tempFile.delete();
//        }
//
//        //IRWireless.getCommonUtil().convertMp4ToAmr(m4aFileFullPath, amrFileFullPath).subscribe(
//        App.getCommonUtil().convertMp4ToAmr(m4aFileFullPath, amrTempConvertFileName).subscribe(
//                () -> {
//                    // convert succeed
//                    // 변환 성공시 임시변환파일에서 전송파일명으로 rename 후 전송
//                    File tempAmrFile = new File(amrTempConvertFileName);
//                    File targetAmrFile = new File(amrFileFullPath);
//                    tempAmrFile.renameTo(targetAmrFile);
//
//                    String filename = amrFileName;
//
//                    try {
//                        App.getCommonUtil().encryptFile(filename);
//                    } catch (Exception e) {
//                        LogWrapper.addLog("convertResendFileExec enc", App.getCommonUtil().stackTraceToString(e));
//                    }
//
//                    App.getCommonUtil().uploadFileForce(filename, filename, RECSERVER_URL + "httpup/RefHome.jsp", "", callId)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(v2 -> {
//                            }, e -> {
//                                //fcmForceUploadResult("N", "Y", "Y", amrFileName);
//                            }, () -> {
//                                //fcmForceUploadResult("Y", "Y", "Y", amrFileName);
//                                iResendPos++;
//                                convertResendFile();
//                            });
//                }, e -> {
//                    // convert error
//                    //fcmForceUploadResult("N", "Y", "N", amrFileName);
//                }
//        );
//    }
//
//    //202010 FCM 수신시 녹취파일 재전송 jhlee
//    public void fcmSendRecordFile(Context ctx, String amrFileName) {
//
//        CompositeDisposable disposables = new CompositeDisposable();
//
//        disposables.add(AppDataBase.getAppDatabase(ctx).callDao().getFcmResendCall(amrFileName)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe((callLog) -> {
//
//                    LogWrapper.addLog("fcmSendRecordFile", "resend filename : " + amrFileName);
//
//                    // 사이즈 검증
//                    if (callLog.size() > 0) {
//                        String filename = callLog.get(0).fileName;
//                        if (checkFileResend(filename)) {
//
//                            long amrFileSize = checkFileSize(filename);
//
//                            try {
//                                encryptFile(filename);
//                            } catch (Exception e) {
//                                LogWrapper.addLog("pre re upload enc", App.getCommonUtil().stackTraceToString(e));
//                            }
//                            uploadFile(filename, filename, RECSERVER_URL + "httpup/RefHome.jsp", "", callLog.get(0).callId)
//                                    .subscribeOn(Schedulers.io())
//                                    //.observeOn(AndroidSchedulers.mainThread())
//                                    .observeOn(Schedulers.io())
//                                    .subscribe(v2 -> {
//                                    }, e -> {
//                                        e.printStackTrace();
//                                        //전송실패
//                                        fcmUploadResult("Y", "Y", "N", callLog.get(0).callId, callLog.get(0).callDuration, callLog.get(0).talkDuration, callLog.get(0).inOutFlag, callLog.get(0).recSendFlag, callLog.get(0).fileName, amrFileSize);
//                                    }, () -> {
//                                        //전송성공
//                                        fcmUploadResult("Y", "Y", "Y", callLog.get(0).callId, callLog.get(0).callDuration, callLog.get(0).talkDuration, callLog.get(0).inOutFlag, callLog.get(0).recSendFlag, callLog.get(0).fileName, amrFileSize);
//
//                                    });
//                        } else {
//                            //amr 파일이 존재하지 않을 경우
//                            fcmUploadResult("Y", "N", "N", callLog.get(0).callId, callLog.get(0).callDuration, callLog.get(0).talkDuration, callLog.get(0).inOutFlag, callLog.get(0).recSendFlag, callLog.get(0).fileName, 0);
//                        }
//
//                    } else {  //DB에 정보가 존재하지 않는 경우
//                        fcmUploadResult("N", "N", "N", "", 0, 0, "", "", amrFileName, 0);
//                    }
//
//                }, (e) -> {
//                    if (e instanceof EmptyResultSetException) {
//                    } else {
//                        App.getCommonUtil().showAlertDialog(ctx, "오류", "오류.....", null);
//                        LogWrapper.addLog("fcmSendRecordFile - 1", App.getCommonUtil().stackTraceToString(e));
//                    }
//                    e.printStackTrace();
//
//                }));
//    }
//
//    //202010 FCM 수신시 녹취파일 재전송 결과전송 jhlee
//    public void fcmUploadResult(String dbExistYn, String fileExistYn, String reSendYn, String callId, int callDuration, int talkDuration, String inOutFlag, String recSendFlag, String amrFileName, long amrFileSize) {
//
//        int pos = amrFileName.lastIndexOf(".");
//        String sendFileName = System.getenv("EXTERNAL_STORAGE") + File.separator + "mertiz" + File.separator + amrFileName.substring(0, pos) + ".txt";
//        File fcmSendFile = new File(sendFileName);
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fcmSendFile))) {
//            if ("Y".equals(dbExistYn)) {
//                writer.append("OS Ver : " + Build.VERSION.RELEASE + "\n");
//                writer.append("sendResult : " + reSendYn + "\n");
//                writer.append("----------------------------------------------" + "\n");
//                writer.append("amrFileSize : " + amrFileSize + "\n");
//                writer.append("fileExistYn : " + fileExistYn + "\n");
//                writer.append("db_recSendFlag : " + recSendFlag + "\n");
//                writer.append("db_callId : " + callId + "\n");
//                writer.append("db_callDuration : " + callDuration + "\n");
//                writer.append("db_talkDuration : " + talkDuration + "\n");
//                writer.append("db_inOutFlag : " + inOutFlag + "\n");
//
//                //안드로이드 버전이 PIE 이상일 경우 Call폴더에서 m4a리스트를 가져온다. (m4a용량도 가져오기)
//                //202011 Oreo 강제 Legacy녹음사용 변경 28 > 26
//                if (Build.VERSION.SDK_INT >= 26) {
//                    boolean boolMatch = false;
//                    String callDate = amrFileName.substring(5, 11);
//                    String m4aFilePath = App.getCommonUtil().getLegacyRecorderFolder();
//                    File m4aFolder = new File(m4aFilePath);
//                    if (m4aFolder.isDirectory()) {
//                        File[] m4aFiles = m4aFolder.listFiles();
//                        for (int i = 0; i < m4aFiles.length; i++) {
//                            String[] arrFileName = m4aFiles[i].getName().split("_");
//                            File m4aFile = new File(m4aFiles[i].getAbsolutePath());
//                            if (arrFileName.length > 2) {
//                                if (callDate.equals(arrFileName[arrFileName.length - 2])) {
//                                    writer.append("m4aFileName[" + i + "] : *****_" + arrFileName[arrFileName.length - 2] + "_" + arrFileName[arrFileName.length - 1] + " (" + m4aFile.length() + "bytes)\n");
//                                    boolMatch = true;
//                                }
//                            }
//                        }
//                    }
//                    if (!boolMatch) {
//                        writer.append("m4aFile not found!\n");
//                    }
//                }
//            } else {
//                writer.append("OS Ver : " + Build.VERSION.RELEASE + "\n");
//                writer.append("sendResult : " + reSendYn + "\n");
//                writer.append("fileExistYn : " + fileExistYn + "\n");
//                writer.append("dbExistYn : " + dbExistYn + "\n");
//                //안드로이드 버전이 PIE 이상일 경우 Call폴더에서 m4a리스트를 가져온다. (m4a용량도 가져오기)
//                //202011 Oreo 강제 Legacy녹음사용 변경 28 > 26
//                if (Build.VERSION.SDK_INT >= 26) {
//                    boolean boolMatch = false;
//                    String callDate = amrFileName.substring(5, 11);
//                    String m4aFilePath = App.getCommonUtil().getLegacyRecorderFolder();
//                    File m4aFolder = new File(m4aFilePath);
//                    if (m4aFolder.isDirectory()) {
//                        File[] m4aFiles = m4aFolder.listFiles();
//                        for (int i = 0; i < m4aFiles.length; i++) {
//                            String[] arrFileName = m4aFiles[i].getName().split("_");
//                            File m4aFile = new File(m4aFiles[i].getAbsolutePath());
//                            if (arrFileName.length > 2) {
//                                if (callDate.equals(arrFileName[arrFileName.length - 2])) {
//                                    writer.append("m4aFileName[" + i + "] : *****_" + arrFileName[arrFileName.length - 2] + "_" + arrFileName[arrFileName.length - 1] + " (" + m4aFile.length() + "bytes)\n");
//                                    boolMatch = true;
//                                }
//                            }
//                        }
//                    }
//                    if (!boolMatch) {
//                        writer.append("m4aFile not found!\n");
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //Firebase에 해당 파일을 업로드 한다.
//        //storage
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        Uri file = Uri.fromFile(fcmSendFile);
//        StorageReference riversRef = storageRef.child("logs/" + file.getLastPathSegment());
//        UploadTask uploadTask = riversRef.putFile(file);
//
//        // Register observers to listen for when the download is done or if it fails
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//                fcmSendFile.delete();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                fcmSendFile.delete();
//            }
//        });
//
//        LogWrapper.addLog("fcmUploadResult(reSendYn)", sendFileName + " : " + reSendYn);
//    }
//
//    public void fcmForceSendRecordFile(String sourceFileName, String amrFileName, String callId) {
//        String m4aFileFullPath = "";
//        String amrFileFullPath = System.getenv("EXTERNAL_STORAGE") + File.separator + App.getCommonUtil().AUDIO_RECORDER_FOLDER + File.separator + amrFileName;
//
//        //해당 M4A파일을 가져온다.
//        String m4aFilePath = App.getCommonUtil().getLegacyRecorderFolder();
//        File m4aFolder = new File(m4aFilePath);
//        File[] m4aFiles = m4aFolder.listFiles();
//
//        for (int i = 0; i < m4aFiles.length; i++) {
//            String[] arrFileName = m4aFiles[i].getName().split("_");
//            if (arrFileName.length > 2) {
//                if (sourceFileName.equals(arrFileName[arrFileName.length - 2] + "_" + arrFileName[arrFileName.length - 1])) {
//                    m4aFileFullPath = m4aFiles[i].getAbsolutePath();
//                    LogWrapper.addLog("fcmForceSendRecordFile", "amr filename : " + amrFileName + ", m4a filename : " + m4aFiles[i].getName());
//                }
//            }
//        }
//        if ("".equals(m4aFileFullPath)) {
//            fcmForceUploadResult("N", "N", "N", amrFileName);
//            LogWrapper.addLog("fcmForceSendRecordFile", "amr filename : " + amrFileName + ", m4a filename : not found");
//            return;
//        }
//
//        //amr파일이 존재하는 경우 백업 후 변환한다.
//        File sendFile = new File(amrFileFullPath);
//        if (sendFile.exists()) {
//            SimpleDateFormat backupFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//            String backupTime = backupFormat.format(Calendar.getInstance().getTime());
//            File renameFile = new File(amrFileFullPath + "_bak" + backupTime);
//            sendFile.renameTo(renameFile);
//            sendFile.delete();
//        }
//
//        App.getCommonUtil().convertMp4ToAmr(m4aFileFullPath, amrFileFullPath).subscribe(
//                () -> {
//                    // convert succeed
//                    String filename = amrFileName;
//
//                    try {
//                        App.getCommonUtil().encryptFile(filename);
//                    } catch (Exception e) {
//                        LogWrapper.addLog("fcmForceSendRecordFile enc", App.getCommonUtil().stackTraceToString(e));
//                    }
//
//                    App.getCommonUtil().uploadFileForce(filename, filename, RECSERVER_URL + "httpup/RefHome.jsp", "", callId)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(v2 -> {
//                            }, e -> {
//                                fcmForceUploadResult("N", "Y", "Y", amrFileName);
//                            }, () -> {
//                                fcmForceUploadResult("Y", "Y", "Y", amrFileName);
//                            });
//                }, e -> {
//                    // convert error
//                    fcmForceUploadResult("N", "Y", "N", amrFileName);
//                }
//        );
//    }
//
//    public void fcmForceUploadResult(String reSendYn, String fileExistYn, String convertYn, String amrFileName) {
//        int pos = amrFileName.lastIndexOf(".");
//        String sendFileName = System.getenv("EXTERNAL_STORAGE") + File.separator + "mertiz" + File.separator + amrFileName.substring(0, pos) + "_F.txt";
//        File fcmSendFile = new File(sendFileName);
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fcmSendFile))) {
//            //재전송이 성공한 경우 재전송 결과만 파일에 적어서 Return한다.
//            if ("Y".equals(reSendYn)) {
//                writer.append("OS Ver : " + Build.VERSION.RELEASE + "\n");
//                writer.append("sendResult : " + reSendYn);
//            } else {
//                writer.append("OS Ver : " + Build.VERSION.RELEASE + "\n");
//                writer.append("sendResult : " + reSendYn + "\n");
//                writer.append("fileExistYn : " + fileExistYn + "\n");
//                writer.append("convertYn : " + convertYn + "\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //Firebase에 해당 파일을 업로드 한다.
//        //storage
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        Uri file = Uri.fromFile(fcmSendFile);
//        StorageReference riversRef = storageRef.child("logs/" + file.getLastPathSegment());
//        UploadTask uploadTask = riversRef.putFile(file);
//
//        // Register observers to listen for when the download is done or if it fails
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//                fcmSendFile.delete();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                fcmSendFile.delete();
//            }
//        });
//
//        LogWrapper.addLog("fcmUploadResult(reSendYn)", sendFileName + " : " + reSendYn);
//    }
//
//    //신규 token 수신시 Firebase Database Update 진행
//    public void updateFcmDatabase() {
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("FIREBASE", "getInstanceId failed", task.getException());
//                            return;
//                        }
//                        String phoneNo = App.getDeviceInfo().getPhone();
//                        String sabun = App.getDeviceInfo().getSabun();
//                        String fcmToken = task.getResult().getToken();
//
//                        Log.d("FIREBASE", fcmToken);
//
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String updateDate = dateFormat.format(new Date());
//                        Map<String, Object> childUpdates = new HashMap<>();
//
//                        HashMap<String, Object> updateKeyMap = new HashMap<>();
//                        updateKeyMap.put("sabun", sabun);
//                        updateKeyMap.put("token", fcmToken);
//                        updateKeyMap.put("updateDate", updateDate);
//
//                        childUpdates.put("/users/" + phoneNo, updateKeyMap);
//                        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
//                        mRootRef.updateChildren(childUpdates);
//                    }
//                })
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//                        return;
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        return;
//                    }
//                });
//    }
//}
