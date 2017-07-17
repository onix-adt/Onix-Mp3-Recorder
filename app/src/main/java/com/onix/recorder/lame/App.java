package com.onix.recorder.lame;

import android.app.ActivityManager;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.onix.recorder.lame.data.utils.Prefs;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class App extends BaseApplication {

    private static App mInstance;
    private static boolean mIsAppPaused;
    private static boolean mIsAppRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Prefs.init(this);
        mInstance = this;
    }

    public static String getResString(int resId) {
        if (null == mInstance) {
            return "";
        }

        return mInstance.getString(resId);
    }

    @Override
    protected void onAppPause() {
        mIsAppPaused = true;
    }

    @Override
    protected void onAppResume() {
        mIsAppPaused = false;
        mIsAppRunning = true;
    }

    @Override
    protected void onAppDestroy() {
        mIsAppRunning = false;
    }

    public static boolean isAppPaused() {
        return mIsAppPaused;
    }

    public static boolean isAppRunning() {
        return mIsAppRunning;
    }
}
