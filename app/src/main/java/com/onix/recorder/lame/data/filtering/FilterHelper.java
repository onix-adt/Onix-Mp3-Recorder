package com.onix.recorder.lame.data.filtering;

import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper<T extends IFilterable> extends Filter {

    private List<T> mSourceList;
    private IFilterCallback mFilterCallback;

    public FilterHelper(List<T> sourceList, IFilterCallback filterCallback) {
        mSourceList = sourceList;
        mFilterCallback = filterCallback;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        String filterString = constraint.toString().toLowerCase();
        FilterResults results = new FilterResults();

        int count = mSourceList.size();
        final ArrayList<T> models = new ArrayList<>(count);
        String filterableString;

        for (int i = 0; i < count; i++) {
            T model = mSourceList.get(i);
            filterableString = model.getFilterAttribute();

            if (TextUtils.isEmpty(filterableString) || filterableString.toLowerCase().contains(filterString)) {
                models.add(model);
            }
        }

        results.values = models;
        results.count = models.size();

        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (mFilterCallback != null) {
            mFilterCallback.onFinish((ArrayList<T>) results.values);
        }
    }
}
