package com.onix.recorder.lame.adapters.base;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.onix.recorder.lame.data.filtering.FilterHelper;
import com.onix.recorder.lame.data.filtering.IFilterCallback;
import com.onix.recorder.lame.data.filtering.IFilterable;
import com.onix.recorder.lame.data.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFilterAdapter<T extends IFilterable> extends Adapter<BaseHolder> implements Filterable {

    private Integer mHolderLayout;

    private List<T> mItems = new ArrayList<>();
    private List<T> mFilteredItems = new ArrayList<>();

    public BaseFilterAdapter() {
        this(null);
    }

    public BaseFilterAdapter(List<T> items) {
        this(null, items);
    }

    public BaseFilterAdapter(Integer holderLayout, List<T> items) {
        this.mHolderLayout = holderLayout;
        this.mItems = items;
        if (mItems != null) {
            this.mFilteredItems = new ArrayList<>(mItems);
        }
    }

    public void setItems(List<T> items) {
        this.mItems = items;
        if (mItems != null) {
            this.mFilteredItems = new ArrayList<>(mItems);
        }
        notifyDataSetChanged();
    }

    public void resetFilter() {
        setItems(mItems);
    }

    public void addItem(T item) {
        if (mItems != null) {
            mItems.add(item);
        }

        int insertPosition = Constants.UNDEFINED_INT;
        if (mFilteredItems != null) {
            insertPosition = mFilteredItems.size();
            mFilteredItems.add(item);
        }

        if (insertPosition != Constants.UNDEFINED_INT) {
            notifyItemInserted(insertPosition);
        }
    }

    public void remove(T item) {
        if (mItems != null) {
            mItems.remove(item);
        }

        if (mFilteredItems != null) {
            mFilteredItems.remove(item);
        }

        notifyDataSetChanged();
    }

    public List<T> getAdapterItems() {
        return mFilteredItems;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHolderLayout == null) {
            throw new RuntimeException("Please, attach item layout");
        }

        return new BaseHolder(LayoutInflater.from(parent.getContext())
                .inflate(mHolderLayout, parent, false));
    }

    @Override
    public int getItemCount() {
        return mFilteredItems != null ? mFilteredItems.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new FilterHelper<>(mItems, new IFilterCallback<T>() {
            @Override
            public void onFinish(List<T> filteredCollection) {
                mFilteredItems = filteredCollection;
                notifyDataSetChanged();
            }
        });
    }
}
