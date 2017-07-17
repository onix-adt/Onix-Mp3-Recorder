package com.onix.recorder.lame.components.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.onix.recorder.lame.data.media.interfaces.IPlayerStateListener;

public class MediaBinder {
    private boolean mBound = false;
    private ServiceConnection mConnection;
    private MediaPlayerService mService;
    private Context mContext;
    private IPlayerStateListener mPlayerStateListener;

    public MediaBinder(Context context) {
        mContext = context;
    }

    public void setPlayerStateListener(IPlayerStateListener listener, boolean callIfBound) {
        mPlayerStateListener = listener;
        if (callIfBound && mBound) {
            mPlayerStateListener.onPlayerReady(mService);
        }
    }

    public void onStop() {
        if (mBound && mConnection != null && mContext != null) {
            Intent intent = new Intent(mContext, MediaPlayerService.class);
            mContext.startService(intent);

            mContext.unbindService(mConnection);
            mBound = false;
        }
    }

    public void onStart() {
        if (mConnection != null && mContext != null) {
            Intent intent = new Intent(mContext, MediaPlayerService.class);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            intent = new Intent(mContext, MediaPlayerService.class);
            mContext.startService(intent);
        }
    }

    public void onCreate() {
        // start service
        Intent intent = new Intent(mContext, MediaPlayerService.class);
        mContext.startService(intent);

        if (mConnection == null) {
            mConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    MediaPlayerService.MediaServiceBinder binder = (MediaPlayerService.MediaServiceBinder) service;
                    mService = binder.getService();
                    mBound = true;
                    if (mPlayerStateListener != null)
                        mPlayerStateListener.onPlayerReady(mService);
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                    mBound = false;
                }
            };
        }
    }
}
