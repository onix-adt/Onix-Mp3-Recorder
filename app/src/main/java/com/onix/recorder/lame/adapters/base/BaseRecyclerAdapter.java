package com.onix.recorder.lame.adapters.base;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onix.recorder.lame.data.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class BaseRecyclerAdapter<T> extends Adapter<BaseHolder> {

    private Integer mHolderLayout;
    private List<T> mItems = new ArrayList<>();
    private int mLimitCount = Constants.UNDEFINED_INT;
    private int mScaleHolderCount = Constants.UNDEFINED_INT;

    public BaseRecyclerAdapter() {
        this(null);
    }

    public BaseRecyclerAdapter(List<T> items) {
        this(null, items);
    }

    public BaseRecyclerAdapter(Integer holderLayout, List<T> items) {
        this.mHolderLayout = holderLayout;
        this.mItems = items;
    }

    public void setScaleHolderCount(int scaleHolderCount) {
        mScaleHolderCount = scaleHolderCount;
    }

    public int getScaleHolderCount() {
        return mScaleHolderCount;
    }

    public int getLimitCount() {
        return mLimitCount;
    }

    public void setLimitCount(int limitCount) {
        this.mLimitCount = limitCount;
    }

    public void setItems(List<T> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public List<T> getAdapterItems() {
        return mItems;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHolderLayout == null) {
            throw new RuntimeException("Please, attach item layout");
        }

        BaseHolder baseHolder = new BaseHolder(LayoutInflater.from(parent.getContext())
                .inflate(mHolderLayout, parent, false));

        if (getScaleHolderCount() != Constants.UNDEFINED_INT) {
            baseHolder.getBinding().getRoot().setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    parent.getMeasuredHeight() / getScaleHolderCount()));
        }

        return baseHolder;
    }

    @Override
    public void onBindViewHolder(final BaseHolder holder, final int position) {
        final T item = mItems.get(position);
        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return getLimitCount() != Constants.UNDEFINED_INT && getAdapterItems() != null
                ? getLimitCount()
                : (getAdapterItems() != null ? getAdapterItems().size() : 0);
    }
}
