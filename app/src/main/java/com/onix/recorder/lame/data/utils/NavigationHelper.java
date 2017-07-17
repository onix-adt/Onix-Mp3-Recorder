package com.onix.recorder.lame.data.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import com.onix.recorder.lame.App;
import com.onix.recorder.lame.R;
import com.onix.recorder.lame.data.model.TrackRecord;

import java.io.File;

public class NavigationHelper {

    public static void openThirdPartyPlayer(Context context, TrackRecord record) {
        try {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(record.getRealFilePath()), "audio/*");
            context.startActivity(intent);
        } catch (ActivityNotFoundException ignored) {
            Toast.makeText(context, App.getResString(R.string.error_no_external_player), Toast.LENGTH_SHORT).show();
        }
    }

    public static void openSettingsPermissions(Context context) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myAppSettings);
    }

    public static void shareRecord(Context context, TrackRecord trackRecord) {
        if (trackRecord == null) {
            return;
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/mp3");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(trackRecord.getRealFilePath())));
        context.startActivity(Intent.createChooser(share, App.getResString(R.string.menu_share)));
    }
}
