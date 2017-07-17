package com.onix.recorder.lame.views;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.onix.recorder.lame.App;
import com.onix.recorder.lame.R;
import com.onix.recorder.lame.activity.MainActivity;
import com.onix.recorder.lame.data.model.TrackRecord;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.enums.RecordingState;
import com.onix.recorder.lame.components.recording.RecordService;

public class WidgetRecorder extends AppWidgetProvider {

    private long mRecordingTime;
    private RecordingState mRecordingState;
    private SparseArray<RemoteViews> mWidgetViewsMap;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        mRecordingState = RecordingState.STOPPED;

        int[] appWidgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, WidgetRecorder.class));

        updateWidgetUI(context, appWidgetIds, AppWidgetManager.getInstance(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        if (appWidgetIds == null) {
            return;
        }

        updateWidgetUI(context, appWidgetIds, appWidgetManager);
    }

    private void updateWidgetUI(Context context, int[] appWidgetIds, AppWidgetManager widgetManager) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews widgetRemoteViews = getWidgetView(context, appWidgetIds[i]);
            widgetRemoteViews.setTextViewText(R.id.tvTime, TrackRecord.getFormattedTime(mRecordingTime));

            if (mRecordingState != null) {
                int padding = 0;
                int recordControlRes = 0;
                Intent onClickIntent = new Intent(context, RecordService.class);
                switch (mRecordingState) {
                    case RECORDING:
                        onClickIntent.setAction(Constants.ACTION_STOP_RECORDING);
                        recordControlRes = R.drawable.ic_widget_stop_record;
                        break;

                    case STOPPED:
                        onClickIntent.setAction(Constants.ACTION_START_RECORDING);
                        recordControlRes = R.drawable.ic_widget_record;
                        break;
                }
                widgetRemoteViews.setViewPadding(R.id.ivRecordControl, padding, padding, padding, padding);
                widgetRemoteViews.setImageViewResource(R.id.ivRecordControl, recordControlRes);

                PendingIntent recordControlIntent = PendingIntent.getService(context, 0, onClickIntent, 0);
                widgetRemoteViews.setOnClickPendingIntent(R.id.ivRecordControl, recordControlIntent);
            }

            widgetManager.updateAppWidget(appWidgetIds[i], widgetRemoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent != null) {
            switch (intent.getAction()) {
                case Constants.ACTION_ERROR:
                    Intent mainAct = new Intent(context, MainActivity.class);
                    mainAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mainAct);
                    break;

                case Constants.ACTION_LISTENER_RECORDING_ONSTOP:
                case Constants.ACTION_LISTENER_RECORDING_ONSTART:
                case Constants.ACTION_UPDATE:
                    notifyWidgetUpdate(context, intent);
                    break;
            }
        }
    }

    private void notifyWidgetUpdate(Context context, Intent intent) {
        mRecordingTime = intent.getLongExtra(Constants.EXTRA_UPDATE_RECORD_TIME, 0);

        if (intent.hasExtra(Constants.EXTRA_RECORDING_STATE)) {
            mRecordingState = (RecordingState) intent.getSerializableExtra(Constants.EXTRA_RECORDING_STATE);
        }

        onUpdate(context, AppWidgetManager.getInstance(context), intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS));
    }

    public RemoteViews getWidgetView(Context context, int widgetId) {
        if (mWidgetViewsMap == null) {
            mWidgetViewsMap = new SparseArray<>();
        }

        if (mWidgetViewsMap.get(widgetId) == null) {
            RemoteViews createView = new RemoteViews(context.getPackageName(), R.layout.widget_recorder);
            mWidgetViewsMap.put(widgetId, createView);

            return createView;
        }

        return mWidgetViewsMap.get(widgetId);
    }
}
