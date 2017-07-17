package com.onix.recorder.lame.data.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static Context mContext;
    private static String PREF_NAME = "prefs";

    public static void init(Context context) {
        mContext = context;
    }

    public static void clearPrefs() {
        try {
            SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.clear();
            prefsEdit.commit();
        } catch (Exception e) {
        }
    }

    public static void save(String _key, String _value) {
        if (mContext == null)
            return;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();

        prefsEdit.putString(_key, _value);
        prefsEdit.commit();
    }

    public static void save(String _key, int _value) {
        if (mContext == null)
            return;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();

        prefsEdit.putInt(_key, _value);
        prefsEdit.commit();
    }

    public static void save(String _key, boolean _value) {
        if (mContext == null)
            return;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();

        prefsEdit.putBoolean(_key, _value);
        prefsEdit.commit();
    }

    public static String getString(String _key) {
        if (mContext == null)
            return "";
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String accessToken = prefs.getString(_key, "");

        return accessToken;
    }

    public static boolean getBoolean(String _key) {
        if (mContext == null)
            return false;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean accessToken = prefs.getBoolean(_key, false);

        return accessToken;
    }

    public static boolean getBoolean(String _key, boolean _def) {
        if (mContext == null)
            return false;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean accessToken = prefs.getBoolean(_key, _def);

        return accessToken;
    }

    public static int getInt(String _key) {
        if (mContext == null)
            return 0;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int accessToken = prefs.getInt(_key, 0);

        return accessToken;
    }

    public static int getInt(String _key, int _default_value) {
        if (mContext == null)
            return 0;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int accessToken = prefs.getInt(_key, _default_value);

        return accessToken;
    }

    public static long getLong(String _key, long _default_value) {
        if (mContext == null)
            return 0;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long accessToken = prefs.getLong(_key, _default_value);

        return accessToken;
    }

    public static long getLong(String _key) {
        if (mContext == null)
            return 0;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long accessToken = prefs.getLong(_key, 0l);

        return accessToken;
    }

    public static void save(String _key, long _value) {
        if (mContext == null)
            return;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();

        prefsEdit.putLong(_key, _value);
        prefsEdit.commit();
    }

    public static void removePrefValue(String key) {
        if (mContext == null) {
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.remove(key);
        prefsEdit.apply();
    }

    public static boolean isContains(String _key) {
        if (mContext == null)
            return false;
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.contains(_key);
    }
}
