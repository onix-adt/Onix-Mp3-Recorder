package com.onix.recorder.lame.adapters.base;

import android.view.View;

import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.interfaces.ISelectableView;

import java.util.List;

public abstract class BaseSingleSelectionAdapter<T extends ISelectableView> extends BaseRecyclerAdapter<T> {

    public interface IAdapterClickListener<T> {
        void onItemClick(T item);
    }

    private int mSelectedMenuPosition = Constants.UNDEFINED_INT;
    private IAdapterClickListener<T> mListener;

    public BaseSingleSelectionAdapter() {
        this(null);
    }

    public BaseSingleSelectionAdapter(List<T> items) {
        this(null, items);
    }

    public BaseSingleSelectionAdapter(Integer holderLayout, List<T> items) {
        super(holderLayout, items);
    }

    public void setListener(IAdapterClickListener<T> listener) {
        mListener = listener;
    }

    public int getSelectedMenuPosition() {
        return mSelectedMenuPosition;
    }

    public void clearSelectedItem() {
        if (mSelectedMenuPosition != Constants.UNDEFINED_INT
                && getAdapterItems() != null
                && mSelectedMenuPosition < getAdapterItems().size()) {
            T item = getAdapterItems().get(mSelectedMenuPosition);
            item.setIsSelected(false);
            notifyItemChanged(getAdapterItems().indexOf(item));
            mSelectedMenuPosition = Constants.UNDEFINED_INT;
        }
    }

    public void setSelectedMenuPosition(int selectedMenuPosition) {
        if (getAdapterItems() != null && selectedMenuPosition < getAdapterItems().size()) {
            T item = getAdapterItems().get(selectedMenuPosition);
            if (selectedMenuPosition != Constants.UNDEFINED_INT) {
                mSelectedMenuPosition = selectedMenuPosition;
                item.setIsSelected(true);
                notifyItemChanged(selectedMenuPosition);
            }
        }
    }

    public T getSelectedItem() {
        if (mSelectedMenuPosition == Constants.UNDEFINED_INT
                || getAdapterItems() == null
                || !(mSelectedMenuPosition < getAdapterItems().size())) {
            return null;
        }

        return getAdapterItems().get(mSelectedMenuPosition);
    }

    public static BaseSingleSelectionAdapter getInstance() {
        return new BaseSingleSelectionAdapter() {
        };
    }

    @Override
    public void onBindViewHolder(final BaseHolder holder, final int position) {
        View rootHolderView = holder.getBinding().getRoot();
        final T item = getAdapterItems().get(position);
        rootHolderView.setSelected(item.getIsSelected());

        if (item.getIsSelected()) {
            mSelectedMenuPosition = position;
        }

        rootHolderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedMenuPosition != Constants.UNDEFINED_INT
                        && holder.getAdapterPosition() == mSelectedMenuPosition) {
                    return;
                }

                swapSelectedItems(item, holder.getAdapterPosition());
            }
        });
    }

    protected void swapSelectedItems(T item, int updatePosition) {
        if (mSelectedMenuPosition != Constants.UNDEFINED_INT) {
            getAdapterItems().get(mSelectedMenuPosition).setIsSelected(false);
            notifyItemChanged(mSelectedMenuPosition);
        }

        mSelectedMenuPosition = updatePosition;
        getAdapterItems().get(mSelectedMenuPosition).setIsSelected(true);
        notifyItemChanged(mSelectedMenuPosition);

        if (mListener != null) {
            mListener.onItemClick(item);
        }
    }
}
