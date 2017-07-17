package com.onix.recorder.lame.views;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.components.recording.RecordService;
import com.onix.recorder.lame.data.utils.Constants;

public class RecordNotificationView extends RemoteViews {

    public RecordNotificationView(String packageName) {
        this(packageName, R.layout.view_record_notification_normal);
    }

    public RecordNotificationView(String packageName, int layoutId) {
        super(packageName, layoutId);
    }

    public void initActions(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.setAction(Constants.ACTION_STOP_RECORDING);

        PendingIntent pStopIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        setOnClickPendingIntent(R.id.ivStopRecording, pStopIntent);
    }

    public void updateRecordingTime(String time) {
        setTextViewText(R.id.tvTime, time);
    }
}
