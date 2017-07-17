package com.onix.recorder.lame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.data.model.TrackId3Tags;
import com.onix.recorder.lame.databinding.DialogId3tagsBinding;

public class Id3TagsDialog extends Dialog {

    public interface ITagsDialogListener {
        void onChangeTags(TrackId3Tags tags);
    }

    private ObservableField<String> mTitleObservable = new ObservableField<>();
    private ObservableField<String> mArtistObservable = new ObservableField<>();
    private ObservableField<String> mYearObservable = new ObservableField<>();
    private ObservableField<String> mCommentObservable = new ObservableField<>();
    private DialogId3tagsBinding mBinding;
    private TrackId3Tags mTrackId3Tags;
    private ITagsDialogListener mListener;

    public ObservableField<String> getTitleObservable() {
        return mTitleObservable;
    }

    public ObservableField<String> getArtistObservable() {
        return mArtistObservable;
    }

    public ObservableField<String> getYearObservable() {
        return mYearObservable;
    }

    public ObservableField<String> getCommentObservable() {
        return mCommentObservable;
    }

    public Id3TagsDialog(Context context, TrackId3Tags trackId3Tags, ITagsDialogListener tagsDialogListener) {
        super(context, R.style.Theme_RecordDialog);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        fillData(trackId3Tags);

        this.mTrackId3Tags = trackId3Tags;
        this.mListener = tagsDialogListener;
    }

    private void fillData(TrackId3Tags trackId3Tags) {
        if (trackId3Tags == null) {
            throw new IllegalArgumentException("trackId3Tags should not be null");
        }

        mArtistObservable.set(trackId3Tags.getArtist());
        mTitleObservable.set(trackId3Tags.getTitle());
        mYearObservable.set(trackId3Tags.getYear());
        mCommentObservable.set(trackId3Tags.getComment());
    }

    public Id3TagsDialog showDialog() {
        if (mListener == null) {
            throw new RuntimeException("'ITagsDialogListener' is null");
        }

        show();
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_id3tags);

        mBinding = DataBindingUtil.bind(findViewById(R.id.lrDialogRoot));
        mBinding.setHandler(this);

        setupDialogSize();
        setCanceledOnTouchOutside(true);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvOk:
                if (mListener != null) {
                    mListener.onChangeTags(updateTrackTags());
                }
                dismiss();
                break;

            case R.id.tvCancel:
                dismiss();
                break;
        }
    }

    private TrackId3Tags updateTrackTags() {
        if (mTrackId3Tags == null) {
            mTrackId3Tags = new TrackId3Tags();
        }

        mTrackId3Tags.setTitle(mTitleObservable.get());
        mTrackId3Tags.setArtist(mArtistObservable.get());
        mTrackId3Tags.setComment(mCommentObservable.get());
        mTrackId3Tags.setYear(mYearObservable.get());

        return mTrackId3Tags;
    }

    private void setupDialogSize() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (displayMetrics.widthPixels * 0.8);
        lp.height = (int) (displayMetrics.heightPixels * 0.9);
        getWindow().setAttributes(lp);
    }
}
