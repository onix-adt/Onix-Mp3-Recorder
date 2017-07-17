package com.onix.recorder.lame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.onix.recorder.lame.App;
import com.onix.recorder.lame.R;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.databinding.DialogRecordBinding;
import com.onix.recorder.lame.interfaces.IRecordTimerListener;

import java.util.Locale;

public class RecordDialog extends Dialog implements IRecordTimerListener {

    public interface IRecordDialogListener {
        void onStartRecord();

        void onStopRecord();

        void onRegisterTimeListener(IRecordTimerListener listener);
    }

    private ObservableBoolean mIsRecording = new ObservableBoolean();
    private DialogRecordBinding mBinding;
    private IRecordDialogListener mDialogListener;
    private ObservableField<String> mDurationObservable = new ObservableField<>(App.getResString(R.string.record_time_default));

    public RecordDialog(Context context) {
        this(context, false);
    }

    public RecordDialog(Context context, boolean isRecording) {
        super(context, R.style.Theme_RecordDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mIsRecording.set(isRecording);
    }

    public RecordDialog attachListener(IRecordDialogListener listener) {
        this.mDialogListener = listener;
        return this;
    }

    public RecordDialog showDialog() {
        if (mDialogListener == null) {
            throw new RuntimeException("'mDialogListener' is null");
        }

        show();
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_record);

        mBinding = DataBindingUtil.bind(findViewById(R.id.lrDialogRecordRoot));
        mBinding.setHandler(this);

        setupDialogSize();
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mIsRecording.addOnPropertyChangedCallback(mIsRecodingChangedCallback);
        mIsRecording.notifyChange();
        mDialogListener.onRegisterTimeListener(RecordDialog.this);
    }

    private Observable.OnPropertyChangedCallback mIsRecodingChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if (mIsRecording.get()) {
                final Animation animation = new AlphaAnimation(1, 0);
                animation.setDuration(Constants.BLINKING_TIMEOUT);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE);
                animation.setRepeatMode(Animation.REVERSE);
                mBinding.ivBlinkingCircle.startAnimation(animation);
            } else {
                mBinding.ivBlinkingCircle.clearAnimation();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        mIsRecording.removeOnPropertyChangedCallback(mIsRecodingChangedCallback);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMic:
                if (mDialogListener != null) {
                    mDialogListener.onStartRecord();
                }
                mIsRecording.set(true);
                break;

            case R.id.ivActiveRecording:
                if (mDialogListener != null) {
                    mDialogListener.onStopRecord();
                }
                mIsRecording.set(false);
                dismiss();
                break;
        }
    }

    @Override
    public void onTrackTime(String formattedTime, long seconds) {
        mDurationObservable.set(String.format(Locale.getDefault(), App.getResString(R.string.record_time), formattedTime));
    }

    public ObservableField<String> getDurationObservable() {
        return mDurationObservable;
    }

    public ObservableBoolean getIsRecording() {
        return mIsRecording;
    }

    private void setupDialogSize() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (displayMetrics.widthPixels * 0.8);
        lp.height = (int) (displayMetrics.heightPixels * 0.9);
        getWindow().setAttributes(lp);
    }
}
