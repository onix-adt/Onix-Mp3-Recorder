package com.onix.recorder.lame.data.helpers;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.onix.recorder.lame.data.model.ApplicationConfig;
import com.onix.recorder.lame.data.model.TrackId3Tags;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.data.utils.Prefs;

public class ApplicationManager {

    private ApplicationManager() {
    }

    private static ApplicationManager mInstance;
    private ApplicationConfig mConfig;

    public static ApplicationManager getInstance() {
        if (mInstance == null) {
            mInstance = new ApplicationManager();
        }

        return mInstance;
    }

    public ApplicationConfig getConfig(Context context) {
        if (mConfig == null) {
            mConfig = checkoutConfig(context);
        }

        return mConfig;
    }

    public void updateFilePath(Context context, String path) {
        ApplicationConfig config = getConfig(context);
        config.setPath(path);

        saveConfig(config);
    }

    public void updateBitrate(Context context, int bitrate) {
        ApplicationConfig config = getConfig(context);
        config.setBitrate(bitrate);

        saveConfig(config);
    }

    public void updateUseEmbeddedPlayer(Context context, boolean useEmbeddedPlayer) {
        ApplicationConfig config = getConfig(context);
        config.setEmbeddedPlayerEnable(useEmbeddedPlayer);

        saveConfig(config);
    }

    public void updateTrackTags(Context context, TrackId3Tags tags) {
        ApplicationConfig config = getConfig(context);
        config.setId3Tags(tags);

        saveConfig(config);
    }

    private ApplicationConfig checkoutConfig(Context context) {
        String encryptedUserData = Prefs.getString(Constants.PREFS_CONFIG);

        if (TextUtils.isEmpty(encryptedUserData)) {
            ApplicationConfig defaultConfig = ApplicationConfig.getDefaultConfig(context);
            saveConfig(defaultConfig);
            return defaultConfig;
        }

        return new GsonBuilder().create().fromJson(encryptedUserData, ApplicationConfig.class);
    }

    private void saveConfig(ApplicationConfig applicationConfig) {
        if (applicationConfig == null)
            return;

        Prefs.save(Constants.PREFS_CONFIG, new GsonBuilder().create().toJson(applicationConfig));
    }
}
