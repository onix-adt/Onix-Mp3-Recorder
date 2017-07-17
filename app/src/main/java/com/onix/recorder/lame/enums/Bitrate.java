package com.onix.recorder.lame.enums;

import com.onix.recorder.lame.interfaces.ISelectableView;

public enum Bitrate implements ISelectableView {
    LOW(32), MIDDLE(128), HIGH(192);

    private int mBitrate;
    private boolean mIsSelected;

    Bitrate(int bitrate) {
        this.mBitrate = bitrate;
    }

    public int getBitrate() {
        return mBitrate;
    }

    public void setIsSelected(boolean selected) {
        mIsSelected = selected;
    }

    @Override
    public boolean getIsSelected() {
        return mIsSelected;
    }
}
