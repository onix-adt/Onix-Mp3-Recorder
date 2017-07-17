package com.onix.recorder.lame;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public abstract class BaseApplication extends com.activeandroid.app.Application {

    private int mBoundCount = 0;
    private int mCreatedCount;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new AppLifecycleHelper());
    }

    protected void bind() {
        if (mBoundCount == 0) {
            this.onAppResume();
        }
        mBoundCount++;
    }

    protected void unbind() {
        mBoundCount--;

        if (mBoundCount == 0) {
            this.onAppPause();
        }
    }

    protected void bindCreated() {
        if (mCreatedCount == 0) {
            this.onAppResume();
        }
        mCreatedCount++;
    }

    protected void unbindCreated() {
        mCreatedCount--;

        if (mCreatedCount == 0) {
            this.onAppPause();
        }
    }

    protected abstract void onAppPause();

    protected abstract void onAppResume();

    protected abstract void onAppDestroy();

    private class AppLifecycleHelper implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            bindCreated();
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            unbindCreated();
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            bind();
        }

        @Override
        public void onActivityStopped(Activity activity) {
            unbind();
        }
    }
}
