package com.onix.recorder.lame.adapters;

import android.view.View;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.adapters.base.BaseFilterAdapter;
import com.onix.recorder.lame.adapters.base.BaseHolder;
import com.onix.recorder.lame.data.model.TrackRecord;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.databinding.ItemTrackRecordBinding;

import java.util.List;

public class TrackRecordAdapter extends BaseFilterAdapter<TrackRecord> {

    public interface IRecordClickListener {
        void onRecordClick(TrackRecord trackRecord);

        void onMore(View view, TrackRecord trackRecord);
    }

    private IRecordClickListener mItemClickListener;

    public TrackRecordAdapter() {
        super(R.layout.item_track_record, null);
    }

    public TrackRecordAdapter(List<TrackRecord> trackRecordList, IRecordClickListener listener) {
        super(R.layout.item_track_record, trackRecordList);

        this.mItemClickListener = listener;
    }

    public void setItemClickListener(IRecordClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void updateItem(TrackRecord trackRecord) {
        if (!(trackRecord != null && getAdapterItems() != null)) {
            return;
        }

        int index = getAdapterItems().indexOf(trackRecord);
        if (index == Constants.UNDEFINED_INT) {
            getAdapterItems().add(trackRecord);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        ItemTrackRecordBinding binding = (ItemTrackRecordBinding) holder.getBinding();
        binding.setRecord(getAdapterItems().get(position));
        binding.setHandler(this);
    }

    public void onClick(View view, TrackRecord record) {
        switch (view.getId()) {
            case R.id.frameRootContent:
                if (mItemClickListener != null) {
                    mItemClickListener.onRecordClick(record);
                }
                break;

            case R.id.ivMore:
                if (mItemClickListener != null) {
                    mItemClickListener.onMore(view, record);
                }
                break;
        }
    }
}
