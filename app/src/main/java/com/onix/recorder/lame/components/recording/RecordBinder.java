package com.onix.recorder.lame.components.recording;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.onix.recorder.lame.interfaces.IRecordController;
import com.onix.recorder.lame.interfaces.IRecordInterface;

public class RecordBinder {

    private ServiceConnection mConnection;
    private Context mContext;
    private IRecordInterface mRecordInterface;
    private IRecordController mRecordController;
    private RecordService mRecordService;

    public RecordBinder(Context context) {
        mContext = context;
    }

    public void setRecordInterface(IRecordInterface recordInterface) {
        mRecordInterface = recordInterface;
        if (mRecordService != null) {
            mRecordService.setRecordListener(mRecordInterface);
        }
    }

    public void onCreate() {
        if (mConnection == null) {
            mConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className, IBinder service) {
                    RecordServiceBinder binder = (RecordServiceBinder) service;
                    mRecordService = binder.getService();
                    mRecordService.setRecordListener(mRecordInterface);
                    mRecordController = binder.getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                    mRecordController = null;
                }
            };
        }
    }

    public void onStart() {
        if (mConnection != null && mContext != null) {
            Intent intent = new Intent(mContext, RecordService.class);
            mContext.startService(intent);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void onStop() {
        if (mConnection != null) {
            mContext.unbindService(mConnection);
        }
    }

    public IRecordController getRecordController() {
        return mRecordController;
    }
}
