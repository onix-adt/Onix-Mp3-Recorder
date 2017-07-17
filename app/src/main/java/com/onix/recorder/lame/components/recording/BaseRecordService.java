package com.onix.recorder.lame.components.recording;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.views.RecordNotificationView;

public abstract class BaseRecordService extends Service {

    private Notification mCurrentNotification;
    private RecordNotificationView mNotificationView;

    protected void showNotification() {
        mNotificationView = new RecordNotificationView(getPackageName());
        mCurrentNotification = createNotificationInstance(getBaseContext(), mNotificationView);
        mNotificationView.initActions(getBaseContext());
        startForeground(1, mCurrentNotification);
    }

    protected void updateNotificationRecordLength(String time) {
        if (!(mNotificationView != null && mCurrentNotification != null)) {
            throw new IllegalStateException("Check the state, when you launch updateNotificationRecordLength()");
        }

        mNotificationView.updateRecordingTime(time);
        mNotificationView.initActions(getBaseContext());
        mCurrentNotification.contentView = mNotificationView;
        startForeground(1, mCurrentNotification);
    }

    protected void hideNotification() {
        stopForeground(true);
    }

    private static Notification createNotificationInstance(Context context, RemoteViews remoteViews) {
        Intent intent = new Intent(context, RecordService.class);
        intent.setAction(Constants.ACTION_ABORT_RECORDING);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifiation_mic)
                .setCustomContentView(remoteViews)
                .setDeleteIntent(pendingIntent)
                .build();
    }
}
