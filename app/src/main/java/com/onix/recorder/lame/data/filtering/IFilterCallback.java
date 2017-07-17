package com.onix.recorder.lame.data.filtering;

import java.util.List;

public interface IFilterCallback<T extends IFilterable> {

    void onFinish(List<T> filteredCollection);
}
