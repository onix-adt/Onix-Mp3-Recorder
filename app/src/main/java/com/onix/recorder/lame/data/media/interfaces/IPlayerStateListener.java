package com.onix.recorder.lame.data.media.interfaces;

import com.onix.recorder.lame.components.audio.MediaPlayerService;

public interface IPlayerStateListener {
    void onPlayerReady(MediaPlayerService service);
}
