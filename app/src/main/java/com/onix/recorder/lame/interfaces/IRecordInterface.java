package com.onix.recorder.lame.interfaces;

import com.onix.recorder.lame.data.model.TrackRecord;

public interface IRecordInterface {

    void onStartRecording();

    void onStopRecording(TrackRecord trackRecord);

    void onUpdateRecordingTime(String formattedTime, long unixSeconds);

    void onRecordError(Throwable e);
}
