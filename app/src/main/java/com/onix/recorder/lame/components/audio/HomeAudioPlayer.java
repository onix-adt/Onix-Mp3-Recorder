package com.onix.recorder.lame.components.audio;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.data.media.interfaces.IAudioTrack;
import com.onix.recorder.lame.data.media.interfaces.ICompleteListener;
import com.onix.recorder.lame.data.media.interfaces.IEventNotificationListener;
import com.onix.recorder.lame.data.media.interfaces.IPlayProgressListener;
import com.onix.recorder.lame.data.media.interfaces.IPlayerDestroyListener;
import com.onix.recorder.lame.data.media.interfaces.IPlayerInitializationListener;
import com.onix.recorder.lame.data.media.interfaces.IPlayerStateListener;
import com.onix.recorder.lame.data.media.interfaces.IPreparedListener;

import java.util.ArrayList;
import java.util.List;

public class HomeAudioPlayer {

    private MediaPlayerService mService;

    // views
    private View mViewPlay;
    private View mViewPause;
    private boolean isViewsCreated;
    private Context mContext;
    private Handler mHandler;
    private TextView mViewTitle;
    private boolean isPreparing;
    private View mViewProgress;
    private View mTxtvLoading;
    private IAudioTrack mPreparingTrack;
    private SeekBar mTrackProgress;

    public HomeAudioPlayer(Context context) {
        isViewsCreated = false;
        mContext = context;
        mHandler = new Handler();
        isPreparing = false;
    }

    public View createView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_simple_player, null);

        mViewPlay = view.findViewById(R.id.imgv_play);
        mViewPause = view.findViewById(R.id.imgv_pause);
        mViewTitle = (TextView) view.findViewById(R.id.txt_title);
        mTrackProgress = (SeekBar) view.findViewById(R.id.progress_seek);
        mViewProgress = view.findViewById(R.id.view_progress);
        mTxtvLoading = view.findViewById(R.id.txtv_loading);
        mTxtvLoading.setVisibility(View.INVISIBLE);
        isViewsCreated = true;
        return view;
    }

    private void initViews() {

        mTxtvLoading.setVisibility(View.INVISIBLE);

        if (mService.getMediaPlayer().isPlaying() || mService.isPaused()) {
            updateState(null);
        }
        mService.setOnPauseFromNotificationListener(new IEventNotificationListener() {
            @Override
            public void onPauseFromNotification() {
                updateState(null);
            }

            @Override
            public void onResumeFromNotification() {
                updateState(null);
            }
        });


        mService.setOnPreparedListener(new IPreparedListener() {
            @Override
            public void onPrepared(IAudioTrack track) {
                isPreparing = false;
                if (mViewProgress != null && mTxtvLoading != null) {
                    mViewProgress.setVisibility(View.INVISIBLE);
                }
                updateState(null);
            }

            @Override
            public void onStartPrepared(IAudioTrack track) {
                isPreparing = true;
                if (mViewProgress != null && mViewPlay != null && mViewPause != null && mTxtvLoading != null) {
                    mViewProgress.setVisibility(View.VISIBLE);
                    mViewPause.setVisibility(View.INVISIBLE);
                    mViewPlay.setVisibility(View.INVISIBLE);

                    if (mTrackProgress != null)
                        mTrackProgress.setProgress(0);
                }
            }
        });

        mService.setOnCompletedListener(new ICompleteListener() {
            @Override
            public void onCompleted(IAudioTrack track) {
                updateState(null);
                mTrackProgress.setProgress(mTrackProgress.getMax());
            }
        });

        mViewPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPreparing)
                    return;

                if (mService != null)
                    mService.pause();

                updateState(null);
            }
        });

        mViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IAudioTrack track = getCurrentTrack();
                if (track == null)
                    track = getPreparingTrack();

                if (isPreparing && !isInErrorState())
                    return;
                if (mService != null)
                    mService.resume();
                updateState(null);
            }
        });

        mService.setOnPlayerDestroyListener(new IPlayerDestroyListener() {
            @Override
            public void onPlayerDestroyed() {
                initDefState();
                release();
            }
        });

        updateState(null);

        mTrackProgress.setMax(100);
        mTrackProgress.setProgress(0);

        mTrackProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mService != null)
                        mService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mService.setPlayProgressListener(new IPlayProgressListener() {
            @Override
            public void onProgress(final int current, final int duration) {
                if (mHandler != null)
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            float progress = current * 100 / duration;

                            // crutch because of negative margins for seekbar (it`s needed for cover place under thumb)
                            if (progress <= 1)
                                mTrackProgress.setProgress(1);
                            else
                                mTrackProgress.setProgress((int) progress);
                        }
                    });
            }
        });
    }

    private void updateState(IAudioTrack preloadTrack) {
        if (!isViewsCreated)
            return;

        if (mService == null)
            return;

        IAudioTrack currentTrack = mService.getCurrentTrack();
        if (currentTrack == null) {
            currentTrack = preloadTrack;
            mPreparingTrack = preloadTrack;
            if (currentTrack == null) {
                initDefState();
                return;
            }
        }

        mViewTitle.setText(currentTrack.getTitle());

        if (isPreparing()) {
            mViewPlay.setVisibility(View.INVISIBLE);
            mViewPause.setVisibility(View.INVISIBLE);
            mViewProgress.setVisibility(View.VISIBLE);
        } else {
            mViewProgress.setVisibility(View.INVISIBLE);
            if (mService.isPaused()) {
                mViewPlay.setVisibility(View.VISIBLE);
                mViewPause.setVisibility(View.GONE);
            } else {
                mViewPlay.setVisibility(View.GONE);
                mViewPause.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initDefState() {
        mTrackProgress.setProgress(0);
        mViewPlay.setVisibility(View.VISIBLE);
        mViewPause.setVisibility(View.GONE);
        mViewTitle.setText("");
    }

    public void onResume(MediaBinder binder, final IPlayerInitializationListener listener) {
        binder.setPlayerStateListener(new IPlayerStateListener() {
            @Override
            public void onPlayerReady(MediaPlayerService service) {
                if (!isViewsCreated)
                    return;

                mService = service;
                initViews();
                if (listener != null)
                    listener.onPlayerInitialized();
            }
        }, true);
    }

    public void onPause() {
        if (mService != null) {
            mTrackProgress.setOnSeekBarChangeListener(null);
            mService.setOnErrorListener(null);
            mService.setOnCompletedListener(null);
            mService.setOnBufferedListener(null);
            mService.setOnSeekListener(null);
            mService.setPlayProgressListener(null);
            mService.setOnPlayerDestroyListener(null);
            mService.setOnPauseFromNotificationListener(null);
            mService = null;
        }
    }

    public void resume() {
        if (mService != null)
            mService.resume();
    }

    public void pause() {
        if (mService != null)
            mService.pause();
    }

    public void play(IAudioTrack track) {
        if (mService != null)
            mService.play(track);
    }

    public boolean isPlaying() {
        if (mService == null)
            return false;

        return mService.getMediaPlayer().isPlaying();
    }

    public boolean isPreparing() {
        if (mService == null)
            return false;

        return !mService.isEnabledControls();
    }

    public void play(List<IAudioTrack> tracks, int position) {
        if (mService != null)
            mService.play(tracks, position);

        try {
            updateState(tracks.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacks(null);
            mHandler = null;
        }
        onPause();
        mContext = null;
    }

    public IAudioTrack getPreparingTrack() {
        return mPreparingTrack;
    }

    public IAudioTrack getCurrentTrack() {
        if (mService != null)
            return mService.getCurrentTrack();

        return null;
    }

    public List<IAudioTrack> getCurrentTracks() {
        if (mService != null)
            return mService.getCurrentTracks();

        return new ArrayList<>();
    }

    public boolean isInErrorState() {
        if (mService != null)
            return mService.isInErrorState();

        return true;
    }
}
