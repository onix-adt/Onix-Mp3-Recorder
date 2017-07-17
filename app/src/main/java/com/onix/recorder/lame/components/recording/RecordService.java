package com.onix.recorder.lame.components.recording;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.onix.recorder.lame.App;
import com.onix.recorder.lame.activity.MainActivity;
import com.onix.recorder.lame.core.LameBuilder;
import com.onix.recorder.lame.core.LameCore;
import com.onix.recorder.lame.data.helpers.ApplicationManager;
import com.onix.recorder.lame.data.model.TrackRecord;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.data.utils.FileUtils;
import com.onix.recorder.lame.data.utils.UnixTimeUtils;
import com.onix.recorder.lame.enums.RecordingState;
import com.onix.recorder.lame.enums.TimerState;
import com.onix.recorder.lame.exceptions.PermissionException;
import com.onix.recorder.lame.interfaces.IRecordController;
import com.onix.recorder.lame.interfaces.IRecordInterface;
import com.onix.recorder.lame.views.WidgetRecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecordService extends BaseRecordService implements IRecordController {

    // 'MP3_BUFFER' should be at least 7200 bytes long
    // to hold all possible emitted data.
    private final int SAMPLE_RATE = 8000;
    private final short[] BUFFER = new short[SAMPLE_RATE * 2 * 5];
    private final byte[] MP3_BUFFER = new byte[(int) (7200 + BUFFER.length * 2 * 1.25)];

    private AudioRecord mAudioRecord;
    private LameCore mLameCore;
    private FileOutputStream mOutputStream;
    private TrackRecord mTrackRecord;
    private Thread mRecordThread;
    private int mMinBuffer;

    private RecordServiceBinder mBinder = new RecordServiceBinder(this);
    private IRecordInterface mRecordListener;

    public void setRecordListener(IRecordInterface recordListener) {
        mRecordListener = recordListener;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        onHandleIntent(intent);

        return START_STICKY;
    }

    private int getMinBuffer() {
        if (mMinBuffer == 0) {
            mMinBuffer = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
        }

        return mMinBuffer;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private AudioRecord getAudioRecord() {
        if (mAudioRecord == null) {
            mAudioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, getMinBuffer() * 2);
        }

        return mAudioRecord;
    }

    private CountDownTimer mRecordingTimer = new CountDownTimer(Constants.DEFAULT_MAX_RECORD_LENGTH, Constants.DEFAULT_TICK_LENGTH) {
        @Override
        public void onTick(long millisUntilFinished) {
            long recordLength = (Constants.DEFAULT_MAX_RECORD_LENGTH - millisUntilFinished) / 1000;
            if (mTrackRecord != null) {
                mTrackRecord.setDuration(recordLength);
            }
            updateNotificationRecordLength(TrackRecord.getFormattedTime(recordLength));
            notifyUpdateRecordingTime(recordLength, TimerState.STARTED);
        }

        @Override
        public void onFinish() {
        }
    };

    public void onHandleIntent(@Nullable Intent intent) {
        if (!(intent != null && intent.getAction() != null)) {
            return;
        }

        switch (intent.getAction()) {
            case Constants.ACTION_ABORT_RECORDING:
                stopRecording();
                break;

            case Constants.ACTION_START_RECORDING:
                startRecording();
                break;

            case Constants.ACTION_STOP_RECORDING:
                stopRecording();
                break;
        }
    }

    @Override
    public void startRecording() {
        if (!checkPermission(getApplicationContext())) {
            notifyError(new PermissionException());
            return;
        }

        String savingPath = ApplicationManager.getInstance().getConfig(this).getPath();
        mTrackRecord = new TrackRecord(FileUtils.getDefaultFileName(savingPath), savingPath, UnixTimeUtils.getCurrentUnixTime());

        showNotification();

        mLameCore = new LameBuilder()
                .setInSampleRate(SAMPLE_RATE)
                .setOutChannels(1)
                .setOutBitrate(ApplicationManager.getInstance().getConfig(this).getBitrate())
                .setOutSampleRate(SAMPLE_RATE)
                .build();

        try {
            mOutputStream = new FileOutputStream(new File(mTrackRecord.getTempPath()));
        } catch (FileNotFoundException e) {
            stopRecording();
            notifyError(e);
        }

        getAudioRecord().startRecording();

        if (mRecordThread != null && mRecordThread.isAlive()) {
            mRecordThread.interrupt();
            mRecordThread = null;
        }

        mRecordThread = new Thread() {
            @Override
            public void run() {
                int bytesRead = 0;
                while (getAudioRecord().getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    bytesRead = getAudioRecord().read(BUFFER, 0, getMinBuffer());

                    if (bytesRead > 0) {
                        int bytesEncoded = mLameCore.encode(BUFFER, BUFFER, bytesRead, MP3_BUFFER);

                        if (bytesEncoded > 0) {
                            try {
                                mOutputStream.write(MP3_BUFFER, 0, bytesEncoded);
                            } catch (IOException e) {
                                stopRecording();
                                notifyError(e);
                            }
                        }
                    }
                }
            }
        };
        mRecordThread.start();

        startTimer();
        notifyStartRecording();
    }

    @Override
    public void stopRecording() {
        if (!(mLameCore != null && mOutputStream != null)) {
            return;
        }

        getAudioRecord().stop();
        hideNotification();

        int outputMp3buf = mLameCore.flush(MP3_BUFFER);
        if (outputMp3buf > 0) {
            try {
                mOutputStream.write(MP3_BUFFER, 0, outputMp3buf);
                mOutputStream.close();
            } catch (IOException e) {
            }
        }

        mLameCore.close();

        if (mRecordThread != null) {
            mRecordThread.interrupt();
            mRecordThread = null;
        }

        stopTimer();
        notifyStopRecording();
    }

    private void startTimer() {
        mRecordingTimer.start();
    }

    private void stopTimer() {
        mRecordingTimer.onFinish();
        mRecordingTimer.cancel();
    }

    private static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    /*
     * NOTIFY LISTENERS ABOUT CHANGES
     */
    private void notifyStartRecording() {
        Intent widgetIntent = new Intent(Constants.ACTION_LISTENER_RECORDING_ONSTART);

        int[] ids = AppWidgetManager.getInstance(getApplicationContext())
                .getAppWidgetIds(new ComponentName(getApplicationContext(), WidgetRecorder.class));

        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        widgetIntent.putExtra(Constants.EXTRA_RECORDING_STATE, RecordingState.RECORDING);
        widgetIntent.putExtra(Constants.EXTRA_RECORD, mTrackRecord);

        getApplicationContext().sendBroadcast(widgetIntent);

        if (mRecordListener != null) {
            mRecordListener.onStartRecording();
        }
    }

    private void notifyStopRecording() {
        Intent widgetIntent = new Intent(Constants.ACTION_LISTENER_RECORDING_ONSTOP);

        int[] ids = AppWidgetManager.getInstance(getApplicationContext())
                .getAppWidgetIds(new ComponentName(getApplicationContext(), WidgetRecorder.class));

        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        widgetIntent.putExtra(Constants.EXTRA_RECORDING_STATE, RecordingState.STOPPED);
        widgetIntent.putExtra(Constants.EXTRA_RECORD, mTrackRecord);

        getApplicationContext().sendBroadcast(widgetIntent);

        if (mRecordListener != null) {
            mRecordListener.onStopRecording(mTrackRecord);
        }

        if (App.isAppPaused() || !App.isAppRunning()) {
            Intent mainActIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainActIntent.setAction(Constants.ACTION_STOP_RECORDING_FROM_WIDGET);
            mainActIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mainActIntent.putExtra(Constants.EXTRA_RECORD, mTrackRecord);
            getApplicationContext().startActivity(mainActIntent);
        }
    }

    private void notifyError(@NonNull Exception e) {
        Intent intent = new Intent(getApplicationContext(), WidgetRecorder.class);
        intent.setAction(Constants.ACTION_ERROR);

        int[] ids = AppWidgetManager.getInstance(getApplicationContext())
                .getAppWidgetIds(new ComponentName(getApplicationContext(), WidgetRecorder.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra(Constants.EXTRA_ERROR, Constants.EXTRA_ERROR_PERMISSION);

        getApplicationContext().sendBroadcast(intent);

        if (mRecordListener != null) {
            mRecordListener.onRecordError(e);
        }
    }

    private void notifyUpdateRecordingTime(long timeInSeconds, TimerState timerState) {
        Intent intent = new Intent(getApplicationContext(), WidgetRecorder.class);

        intent.setAction(Constants.ACTION_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getApplicationContext())
                .getAppWidgetIds(new ComponentName(getApplicationContext(), WidgetRecorder.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra(Constants.EXTRA_UPDATE_RECORD_TIME, timeInSeconds);
        intent.putExtra(Constants.EXTRA_TIMER_STATE, timerState);

        getApplicationContext().sendBroadcast(intent);

        if (mRecordListener != null) {
            mRecordListener.onUpdateRecordingTime(TrackRecord.getFormattedTime(timeInSeconds), timeInSeconds);
        }
    }
}
