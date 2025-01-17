package com.irlink.meritz.util;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by iklee on 2018-05-02.
 */
public class PrefUtil {

    SharedPreferences pref;

    Context context;

    String SHARED_PREFERENCES = "IRWIRELESS_PREF";

    public PrefUtil(Context context) {
        pref = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public String getPrefString(String key, String defVal) {
        return pref.getString(key, defVal);
    }

    public Boolean getPrefBoolean(String key, Boolean defVal) {
        return pref.getBoolean(key, defVal);
    }

    long getPrefLong(String key, long defVal) {
        return pref.getLong(key, defVal);
    }

    int getPrefInt(String key, int defVal) {
        return pref.getInt(key, defVal);
    }

    public void putPrefString(String key, String value){
        pref.edit().putString(key, value).commit();
    }

    void putPrefBoolean(String key, Boolean value){
        pref.edit().putBoolean(key, value).commit();
    }

    public void putPrefLong(String key, long value) { pref.edit().putLong(key, value).commit(); }

    public void putPrefInt(String key, int value) { pref.edit().putInt(key, value).commit(); }
}
