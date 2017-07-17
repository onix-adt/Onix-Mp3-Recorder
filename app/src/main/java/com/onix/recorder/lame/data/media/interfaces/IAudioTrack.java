package com.onix.recorder.lame.data.media.interfaces;

import android.net.Uri;

public interface IAudioTrack {
    long getTrackId();

    String getTitle();

    Uri getAudioSource();
}
