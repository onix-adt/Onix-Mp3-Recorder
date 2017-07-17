package com.onix.recorder.lame.interfaces;

public interface IRecordTimerListener {

    void onTrackTime(String formattedTime, long seconds);
}