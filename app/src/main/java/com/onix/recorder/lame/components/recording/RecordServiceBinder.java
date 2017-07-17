package com.onix.recorder.lame.components.recording;

import android.os.Binder;

class RecordServiceBinder extends Binder {

    private RecordService mService;

    RecordServiceBinder(RecordService service) {
        this.mService = service;
    }

    public RecordService getService() {
        return mService;
    }
}
