package com.onix.recorder.lame.adapters;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.adapters.base.BaseHolder;
import com.onix.recorder.lame.adapters.base.BaseSingleSelectionAdapter;
import com.onix.recorder.lame.databinding.ItemBitrateBinding;
import com.onix.recorder.lame.enums.Bitrate;

public class BitrateAdapter extends BaseSingleSelectionAdapter<Bitrate> {

    public BitrateAdapter() {
        super(R.layout.item_bitrate, null);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        ItemBitrateBinding bitrateBinding = (ItemBitrateBinding) holder.getBinding();
        bitrateBinding.setItem(getAdapterItems().get(position));
    }
}
