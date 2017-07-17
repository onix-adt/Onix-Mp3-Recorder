package com.onix.recorder.lame.data.helpers;

import com.activeandroid.query.Select;
import com.onix.recorder.lame.data.model.TrackRecord;

import java.util.List;

public class TrackRecordProvider {

    public static List<TrackRecord> getAudioRecords() {
        return new Select().from(TrackRecord.class).execute();
    }

    public static void saveTrackRecord(TrackRecord trackRecord) {
        trackRecord.save();
    }

    public static void remove(TrackRecord record) {
        if (record == null) {
            return;
        }

        if (record.getId() != null) {
            record.delete();
            return;
        }

        TrackRecord existTrack = new Select()
                .from(TrackRecord.class)
                .where("filename=?", record.getFileName())
                .where("filepath=?", record.getFilePath())
                .executeSingle();
        existTrack.delete();
    }
}
