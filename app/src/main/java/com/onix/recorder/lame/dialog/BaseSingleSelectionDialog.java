package com.onix.recorder.lame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.adapters.base.BaseSingleSelectionAdapter;
import com.onix.recorder.lame.databinding.DialogBaseSingleselectionBinding;
import com.onix.recorder.lame.interfaces.ISelectableView;

import java.util.List;

public class BaseSingleSelectionDialog<V extends ISelectableView, T extends BaseSingleSelectionAdapter<V>> extends Dialog {

    public interface IDialogSelectionListener<V> {
        void onDialogClosed(V item);
    }

    private Class<T> mAdapterClass;
    private String mTitle;
    private List<V> mItemList;
    private DialogBaseSingleselectionBinding mBinding;
    private IDialogSelectionListener<V> mListener;

    public BaseSingleSelectionDialog(Context context, String title, Class<T> adapterClass, List<V> itemList) {
        super(context, R.style.Theme_RecordDialog);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.mAdapterClass = adapterClass;
        this.mTitle = title;
        this.mItemList = itemList;
    }

    public BaseSingleSelectionDialog attachListener(IDialogSelectionListener<V> listener) {
        this.mListener = listener;

        return this;
    }

    public void showDialog() {
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_base_singleselection);
        setupDialogSize();

        setCanceledOnTouchOutside(false);
        setCancelable(false);

        mBinding = DataBindingUtil.bind(findViewById(R.id.lrDialogBaseSingleSelectionRoot));
        mBinding.setHandler(this);
        mBinding.setTitle(mTitle);

        try {
            T adapter = mAdapterClass.newInstance();
            adapter.setListener(new BaseSingleSelectionAdapter.IAdapterClickListener<V>() {
                @Override
                public void onItemClick(V item) {
                    notifyDialogClosed();
                    dismiss();
                }
            });
            adapter.setItems(mItemList);

            mBinding.rvItems.setAdapter(adapter);
        } catch (InstantiationException | IllegalAccessException e) {
        }
    }

    private void notifyDialogClosed() {
        if (mListener != null && mBinding != null) {
            T adapter = (T) mBinding.rvItems.getAdapter();
            if (adapter != null) {
                mListener.onDialogClosed(adapter.getSelectedItem());
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDialogClose:
                notifyDialogClosed();
                dismiss();
                break;
        }
    }

    private void setupDialogSize() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (displayMetrics.widthPixels * 0.8);
        lp.height = (int) (displayMetrics.heightPixels * 0.9);
        getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        // leave empty
    }
}
