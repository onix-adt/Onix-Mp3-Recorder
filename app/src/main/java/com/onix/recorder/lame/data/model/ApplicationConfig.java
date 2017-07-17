package com.onix.recorder.lame.data.model;

import android.content.Context;
import android.os.Environment;

import com.google.gson.annotations.SerializedName;
import com.onix.recorder.lame.enums.Bitrate;

public class ApplicationConfig {

    @SerializedName("path")
    private String mPath;

    @SerializedName("id3_tags")
    private TrackId3Tags mId3Tags;

    @SerializedName("bitrate")
    private int mBitrate;

    @SerializedName("is_embeded_player_enable")
    private boolean mIsEmbeddedPlayerEnable;

    public TrackId3Tags getId3Tags(Context context) {
        if (mId3Tags == null) {
            mId3Tags = TrackId3Tags.getDefaultTags(context);
        }

        return mId3Tags;
    }

    public void setEmbeddedPlayerEnable(boolean embeddedPlayerEnable) {
        mIsEmbeddedPlayerEnable = embeddedPlayerEnable;
    }

    public boolean isEmbeddedPlayerEnable() {
        return mIsEmbeddedPlayerEnable;
    }

    public int getBitrate() {
        if (mBitrate == 0) {
            return Bitrate.HIGH.getBitrate();
        }

        return mBitrate;
    }

    public void setBitrate(int bitrate) {
        mBitrate = bitrate;
    }

    public String getPath() {
        return mPath;
    }

    public void setId3Tags(TrackId3Tags id3Tags) {
        mId3Tags = id3Tags;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public static ApplicationConfig getDefaultConfig(Context context) {
        ApplicationConfig config = new ApplicationConfig();

        config.setPath(Environment.getExternalStorageDirectory().getPath());
        config.setId3Tags(TrackId3Tags.getDefaultTags(context));
        config.setEmbeddedPlayerEnable(true);

        return config;
    }
}
