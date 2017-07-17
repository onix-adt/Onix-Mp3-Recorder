package com.onix.recorder.lame.components.audio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.onix.recorder.lame.data.media.interfaces.IAudioTrack;
import com.onix.recorder.lame.data.media.interfaces.IBufferUpdateListener;
import com.onix.recorder.lame.data.media.interfaces.ICompleteListener;
import com.onix.recorder.lame.data.media.interfaces.IErrorListener;
import com.onix.recorder.lame.data.media.interfaces.IEventNotificationListener;
import com.onix.recorder.lame.data.media.interfaces.IPlayProgressListener;
import com.onix.recorder.lame.data.media.interfaces.IPlayerDestroyListener;
import com.onix.recorder.lame.data.media.interfaces.IPreparedListener;
import com.onix.recorder.lame.data.media.interfaces.ISeekCompleteListener;
import com.onix.recorder.lame.data.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayerService extends Service {

    private final int UPDATE_INTERVAL = 500; // ms

    private MediaPlayer mMediaPlayer;
    private List<IAudioTrack> mTracks;
    private int mCurrentTrackPosition;
    private boolean isPaused;

    // listeners
    private IBufferUpdateListener mBufferListener;
    private ICompleteListener mCompleteListener;
    private IPreparedListener mPreparedListener;
    private ISeekCompleteListener mSeekListener;
    private IErrorListener mErrorListener;
    private IPlayProgressListener mPlayProgressListener;
    private IEventNotificationListener mOnPauseFromNotificationListener;
    private IPlayerDestroyListener mDestroyPlayerListener;
    private Timer mProgressTimer;
    private TimerTask mProgressTask;
    private boolean isEnabledNotification;
    private boolean randomMode = false;
    private boolean loopPlayback;
    private boolean mModePauseWhenError;
    private long mTrackInError;
    protected boolean mSingleTrackMode;
    private boolean isControlsEnabled;
    private final IBinder mBinder = new MediaServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        enableControls(true);
        mCurrentTrackPosition = 0;
        isEnabledNotification = false;
        isPaused = false;
        randomMode = false;
        loopPlayback = false;
        //   isWakeLock = false;
        mModePauseWhenError = true;
        mTrackInError = -1;
    }

    private int getRandExcept(int exceptValue) {
        try {
            int pos = 0;
            Random rand = new Random();
            for (int i = 0; i < 100; i++) {
                pos = rand.nextInt(getCurrentTracks().size());
                if (pos != exceptValue) {
                    break;
                }
            }
            return pos;
        } catch (Exception e) {
            return mCurrentTrackPosition;
        }
    }

    private void nextTrackPosition() {
        if (mCurrentTrackPosition < getCurrentTracks().size() - 1)
            mCurrentTrackPosition++;
        else if (loopPlayback)
            mCurrentTrackPosition = 0;
    }

    private void previousTrackPosition() {
        if (mCurrentTrackPosition > 0)
            mCurrentTrackPosition--;
        else if (loopPlayback)
            mCurrentTrackPosition = getCurrentTracks().size() - 1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void onNextClicked() {
        if (getCurrentTracks() == null) {
            return;
        }

        nextTrackPosition();
        resumePlayer(true);
    }

    public void onPauseClicked() {
        pausePlayer();
        if (mOnPauseFromNotificationListener != null)
            mOnPauseFromNotificationListener.onPauseFromNotification();
    }

    public void onPlayClicked() {
        resumePlayer(false);

        if (mOnPauseFromNotificationListener != null)
            mOnPauseFromNotificationListener.onResumeFromNotification();
    }

    public void onPreviousClicked() {
        previousTrackPosition();
        resumePlayer(true);
    }

    public boolean onStopNotification() {
        if (isEnabledNotification) {
            destroyPlayer();
            return true;
        }
        return false;
    }

    synchronized public List<IAudioTrack> getCurrentTracks() {
        return mTracks;
    }

    synchronized private void setTracks(List<IAudioTrack> tracks) {
        mTracks = tracks;
    }

    private void resumePlayer(boolean newTrack) {
        if (getMediaPlayer().isPlaying() || newTrack) {
            stopUpdateTimer();

            try {
                getMediaPlayer().stop();
                getMediaPlayer().reset();
            } catch (Exception e) {
            }

            isPaused = false;
        } else if (isPaused && !isInErrorState()) {
            isPaused = false;
            startUpdateTimer();
            try {
                getMediaPlayer().start();
            } catch (Exception e) {

            }
            return;
        }

        try {
            Uri mediaUri = getCurrentTrack().getAudioSource();
            getMediaPlayer().setDataSource(mediaUri.getPath());

            setOnPreparedListener(mPreparedListener);

            if (mPreparedListener != null)
                mPreparedListener.onStartPrepared(getCurrentTrack());

            setOnBufferedListener(mBufferListener);
            setOnCompletedListener(mCompleteListener);
            setOnSeekListener(mSeekListener);
            setOnErrorListener(mErrorListener);

            getMediaPlayer().prepareAsync();
            enableControls(false);
            isPaused = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pausePlayer() {
        try {
            getMediaPlayer().pause();
        } catch (Exception e) {

        }
        isPaused = true;
        stopUpdateTimer();
    }

    private void startUpdateTimer() {
        stopUpdateTimer();
        mProgressTimer = new Timer();
        mProgressTask = new TimerTask() {
            @Override
            public void run() {
                if (mPlayProgressListener != null) {
                    mPlayProgressListener.onProgress(getMediaPlayer().getCurrentPosition(), getMediaPlayer().getDuration());
                }
            }
        };
        mProgressTimer.schedule(mProgressTask, UPDATE_INTERVAL, UPDATE_INTERVAL);
    }

    private void stopUpdateTimer() {
        if (mProgressTimer != null && mProgressTask != null) {
            mProgressTask.cancel();
            mProgressTimer.purge();
            mProgressTimer.cancel();
            mProgressTask = null;
            mProgressTimer = null;
        }
    }

    // public methods

    public void setLoop(boolean loop) {
        loopPlayback = loop;
    }

    public void setRandomOn(boolean random) {
        randomMode = random;
    }

    public boolean isRandomMode() {
        return randomMode;
    }

    public boolean isLoop() {
        return loopPlayback;
    }

    public void resume() {
        resumePlayer(false);
    }

    public IAudioTrack getCurrentTrack() {
        try {
            return mTracks.get(mCurrentTrackPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = new MediaPlayer();
        return mMediaPlayer;
    }

    public void play(IAudioTrack track) {
        mCurrentTrackPosition = 0;
        mSingleTrackMode = true;
        List<IAudioTrack> tracks = new ArrayList<>();
        tracks.add(track);
        setTracks(tracks);
        resumePlayer(true);
    }

    public void play(List<IAudioTrack> tracks, int startPosition) {
        mCurrentTrackPosition = startPosition;
        mSingleTrackMode = false;
        List<IAudioTrack> localTracks = new ArrayList<>();
        localTracks.addAll(tracks);
        setTracks(localTracks);
        resumePlayer(true);
    }

    public void pause() {
        pausePlayer();
    }

    public void next() {
        if (getCurrentTracks() == null) {
            return;
        }
        nextTrackPosition();
        resumePlayer(true);
    }

    public void previous() {
        previousTrackPosition();
        resumePlayer(true);
    }

    public void seekTo(int percent) {
        float newPosition = (float) getMediaPlayer().getDuration() / 100f * percent;
        getMediaPlayer().seekTo((int) newPosition);
    }

    public void enableControls(boolean enable) {
        isControlsEnabled = enable;
    }

    public boolean isEnabledControls() {
        return isControlsEnabled;
    }

    public void destroyPlayer() {
        stopUpdateTimer();
        getMediaPlayer().setOnSeekCompleteListener(null);
        getMediaPlayer().setOnErrorListener(null);
        getMediaPlayer().setOnBufferingUpdateListener(null);
        getMediaPlayer().setOnCompletionListener(null);
        if (getMediaPlayer().isPlaying())
            getMediaPlayer().stop();
        setTracks(null);
        if (mDestroyPlayerListener != null)
            mDestroyPlayerListener.onPlayerDestroyed();
    }

    // attach listeners

    public void setOnErrorListener(IErrorListener errorListener) {
        mErrorListener = errorListener;
        getMediaPlayer().setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (getCurrentTracks() == null) {
                    mTrackInError = 1;
                    return false;
                }

                mTrackInError = getCurrentTrack().getTrackId();
                if (mModePauseWhenError) {
                    if (!isPaused) {
                        pausePlayer();
                        enableControls(true);
                    }
                    if (mOnPauseFromNotificationListener != null)
                        mOnPauseFromNotificationListener.onPauseFromNotification();
                } else {
                    nextTrackPosition();
                    resumePlayer(true);
                    if (mErrorListener != null)
                        mErrorListener.onError(getCurrentTrack(), what);
                }

                return false;
            }
        });
    }

    public void setOnPlayerDestroyListener(IPlayerDestroyListener listener) {
        mDestroyPlayerListener = listener;
    }


    public void setOnBufferedListener(IBufferUpdateListener listener) {
        mBufferListener = listener;
        getMediaPlayer().setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (mBufferListener != null)
                    mBufferListener.onBufferUpdate(getCurrentTrack(), percent);
            }
        });
    }

    public void setOnSeekListener(ISeekCompleteListener listener) {
        mSeekListener = listener;
        getMediaPlayer().setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (mSeekListener != null)
                    mSeekListener.onSeek(getCurrentTrack());
            }
        });
    }

    public void setPlayProgressListener(IPlayProgressListener listener) {
        mPlayProgressListener = listener;
    }

    public void setOnPauseFromNotificationListener(IEventNotificationListener listener) {
        mOnPauseFromNotificationListener = listener;
    }

    public void setOnCompletedListener(ICompleteListener listener) {
        mCompleteListener = listener;
        getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mTrackInError != -1 && mModePauseWhenError)
                    return;

                if (getCurrentTracks() == null) {
                    return;
                }

                if (mCompleteListener != null)
                    mCompleteListener.onCompleted(getCurrentTrack());

                if (!mSingleTrackMode) {
                    nextTrackPosition();
                    resumePlayer(true);
                } else {
                    onPauseClicked();
                }
            }
        });
    }

    public void setOnPreparedListener(IPreparedListener listener) {
        mPreparedListener = listener;

        getMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mTrackInError = -1;

                if (!mp.isPlaying()) {
                    mp.start();
                }

                enableControls(true);
                if (mPreparedListener != null)
                    mPreparedListener.onPrepared(getCurrentTrack());

                startUpdateTimer();
            }
        });
    }

    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // clear listeners
        mPlayProgressListener = null;
        mErrorListener = null;
        mBufferListener = null;
        mCompleteListener = null;
        mPreparedListener = null;
        mSeekListener = null;

        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        destroyPlayer();
    }

    public class MediaServiceBinder extends Binder {

        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public void setPauseWhenErrorMode(boolean pauseWhenerror) {
        mModePauseWhenError = pauseWhenerror;
    }

    public boolean isInErrorState() {
        return mTrackInError != -1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}

