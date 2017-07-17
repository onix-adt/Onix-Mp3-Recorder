package com.onix.recorder.lame.adapters.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BaseHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding mBinding;

    public BaseHolder(View v) {
        super(v);
        mBinding = DataBindingUtil.bind(v);
    }

    public ViewDataBinding getBinding() {
        return mBinding;
    }

    public void onClick(View view) {
    }
}
