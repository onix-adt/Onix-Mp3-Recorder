package com.onix.recorder.lame.data.model;

import android.content.Context;

import com.onix.recorder.lame.App;
import com.onix.recorder.lame.R;
import com.onix.recorder.lame.data.utils.AccountUtils;

import java.util.Calendar;

public class TrackId3Tags {

    private String mTitle;
    private String mArtist;
    private String mYear;
    private String mComment;

    public static TrackId3Tags getDefaultTags(Context context) {
        TrackId3Tags trackId3Tags = new TrackId3Tags();

        trackId3Tags.setYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        trackId3Tags.setTitle(App.getResString(R.string.id3_tags_default_title));
        trackId3Tags.setArtist(AccountUtils.getDeviceUserName(context));
        trackId3Tags.setComment("");

        return trackId3Tags;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public void setYear(String year) {
        mYear = year;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getYear() {
        return mYear;
    }

    public String getComment() {
        return mComment;
    }
}
