package com.onix.recorder.lame.data.model;

import android.net.Uri;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.onix.recorder.lame.data.filtering.IFilterable;
import com.onix.recorder.lame.data.media.interfaces.IAudioTrack;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.data.utils.UnixTimeUtils;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

@Table(name = "audiorecords")
public class TrackRecord extends Model implements IFilterable, Serializable, IAudioTrack {

    private final String TEMR_RECORD_EXTENTION = "_temp".concat(Constants.MP3_EXTENTION);

    public static Comparator<TrackRecord> COMPARATOR_DURATION = new Comparator<TrackRecord>() {
        public int compare(TrackRecord o1, TrackRecord o2) {
            return (int) (o1.getDuration() - o2.getDuration());
        }
    };

    public static Comparator<TrackRecord> COMPARATOR_ALPHABETICAL = new Comparator<TrackRecord>() {
        public int compare(TrackRecord o1, TrackRecord o2) {
            return o1.getFileName().compareToIgnoreCase(o2.getFileName());
        }
    };

    public static Comparator<TrackRecord> COMPARATOR_DATE = new Comparator<TrackRecord>() {
        public int compare(TrackRecord o1, TrackRecord o2) {
            return (int) (o1.getRecordUnixDate() - o2.getRecordUnixDate());
        }
    };

    @Column(name = "filename")
    private String mFileName;

    @Column(name = "filepath")
    private String mFilePath;

    // duration in seconds
    @Column(name = "duration")
    private long mDuration;

    // unix time in seconds
    @Column(name = "record_unix_date")
    private long mRecordUnixDate;

    private long mTrackId = Constants.UNDEFINED_INT;
    private int mTrackPosition = Constants.UNDEFINED_INT;

    public long getRecordUnixDate() {
        return mRecordUnixDate;
    }

    public void setRecordUnixDate(long recordUnixDate) {
        mRecordUnixDate = recordUnixDate;
    }

    public TrackRecord() {
        super();
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public long getDuration() {
        return mDuration;
    }

    public TrackRecord(String fileName, String filePath, long unixDateTime) {
        mFileName = fileName;
        mFilePath = filePath;
        mRecordUnixDate = unixDateTime;
    }

    public String getTempPath() {
        if (!(mFilePath != null && mFileName != null)) {
            return "";
        }

        return mFilePath.concat(File.separator).concat(mFileName)
                .replace(Constants.MP3_EXTENTION, TEMR_RECORD_EXTENTION);
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public String getRealFilePath() {
        if (!(mFilePath != null && mFileName != null)) {
            return "";
        }

        return mFilePath.concat(File.separator).concat(mFileName);
    }

    @Override
    public String getFilterAttribute() {
        return getFileName();
    }

    public String getFormattedTime() {
        return getFormattedTime(mDuration);
    }

    public String getFormattedDate() {
        return UnixTimeUtils.convertToString(mRecordUnixDate, UnixTimeUtils.DATE_FORMAT_DD_MM_YYYY_HH_MM);
    }

    public static String getFormattedTime(long timeInSeconds) {
        int formattedMins = (int) (timeInSeconds / 60);
        int formattedSecs = (int) (timeInSeconds % 60);

        return String.format(Locale.getDefault(), "%02d : %02d", formattedMins, formattedSecs);
    }

    @Override
    public long getTrackId() {
        return this.hashCode();
    }

    @Override
    public String getTitle() {
        return mFileName;
    }

    @Override
    public Uri getAudioSource() {
        return Uri.parse(mFilePath.concat(File.separator).concat(mFileName));
    }
}
