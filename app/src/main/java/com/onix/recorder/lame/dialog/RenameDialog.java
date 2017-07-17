package com.onix.recorder.lame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.onix.recorder.lame.App;
import com.onix.recorder.lame.R;
import com.onix.recorder.lame.data.model.TrackRecord;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.databinding.DialogRenameBinding;

public class RenameDialog extends Dialog {

    public interface IRenameDialogListener {
        void onRename(TrackRecord trackRecord, String updateName);
    }

    private DialogRenameBinding mBinding;
    private TrackRecord mRecord;
    private IRenameDialogListener mDialogListener;
    private ObservableField<String> mNameObservable = new ObservableField<>();

    public RenameDialog(Context context, TrackRecord trackRecord) {
        super(context, R.style.Theme_RecordDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.mRecord = trackRecord;
        if (mRecord == null) {
            throw new IllegalArgumentException();
        }

        String cutFileName = mRecord.getFileName().replace(Constants.MP3_EXTENTION, "");
        mNameObservable.set(cutFileName);
    }

    public RenameDialog attachListener(IRenameDialogListener listener) {
        this.mDialogListener = listener;
        return this;
    }

    public RenameDialog showDialog() {
        if (mDialogListener == null) {
            throw new RuntimeException("'mDialogListener' is null");
        }

        show();
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rename);

        mBinding = DataBindingUtil.bind(findViewById(R.id.lrDialogRoot));
        mBinding.setHandler(this);

        setupDialogSize();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvOk:
                if (TextUtils.isEmpty(mNameObservable.get())) {
                    Toast.makeText(view.getContext(), App.getResString(R.string.hint_record_file_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mDialogListener != null) {
                    String updatedName = mNameObservable.get().concat(Constants.MP3_EXTENTION);
                    mDialogListener.onRename(mRecord, updatedName);
                }
                dismiss();
                break;
        }
    }

    public ObservableField<String> getNameObservable() {
        return mNameObservable;
    }

    private void setupDialogSize() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (displayMetrics.widthPixels * 0.8);
        lp.height = (int) (displayMetrics.heightPixels * 0.9);
        getWindow().setAttributes(lp);
    }
}
