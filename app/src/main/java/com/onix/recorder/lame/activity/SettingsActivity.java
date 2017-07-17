package com.onix.recorder.lame.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.onix.recorder.lame.App;
import com.onix.recorder.lame.R;
import com.onix.recorder.lame.adapters.BitrateAdapter;
import com.onix.recorder.lame.data.helpers.ApplicationManager;
import com.onix.recorder.lame.data.model.TrackId3Tags;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.databinding.ActivitySettingsBinding;
import com.onix.recorder.lame.dialog.BaseSingleSelectionDialog;
import com.onix.recorder.lame.dialog.Id3TagsDialog;
import com.onix.recorder.lame.enums.Bitrate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private StorageChooser mStorageChooser;
    private ObservableField<String> mPathObservable = new ObservableField<>();
    private ObservableBoolean mUseEmbeddedPlayerObservable = new ObservableBoolean();
    private ObservableInt mBitrateObservable = new ObservableInt();

    public ObservableBoolean getUseEmbeddedPlayerObservable() {
        return mUseEmbeddedPlayerObservable;
    }

    public ObservableInt getBitrateObservable() {
        return mBitrateObservable;
    }

    public ObservableField<String> getPathObservable() {
        return mPathObservable;
    }

    public StorageChooser getStorageChooser() {
        if (mStorageChooser == null) {
            mStorageChooser = new StorageChooser.Builder()
                    .withActivity(this)
                    .withFragmentManager(getSupportFragmentManager())
                    .setType(StorageChooser.DIRECTORY_CHOOSER)
                    .allowCustomPath(true)
                    .allowAddFolder(true)
                    .withMemoryBar(true)
                    .withThreshold(1, DiskUtil.IN_GB)
                    .build();

            mStorageChooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                @Override
                public void onSelect(String path) {
                    ApplicationManager.getInstance().updateFilePath(SettingsActivity.this, path);
                    mPathObservable.set(path);
                }
            });
        }

        return mStorageChooser;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_purple));
        }

        mPathObservable.set(ApplicationManager.getInstance().getConfig(this).getPath());
        mBitrateObservable.set(ApplicationManager.getInstance().getConfig(this).getBitrate());
        mUseEmbeddedPlayerObservable.set(ApplicationManager.getInstance().getConfig(this).isEmbeddedPlayerEnable());

        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setHandler(this);

        binding.tvLameUrl.setPaintFlags(binding.tvLameUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tvPlayMarketUrl.setPaintFlags(binding.tvPlayMarketUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tvPlayMarketUrl.setText(getPlaymarketUrl());
    }

    public void showId3TagsDialog() {
        new Id3TagsDialog(this, ApplicationManager.getInstance().getConfig(this).getId3Tags(this), new Id3TagsDialog.ITagsDialogListener() {
            @Override
            public void onChangeTags(TrackId3Tags tags) {
                ApplicationManager.getInstance().updateTrackTags(SettingsActivity.this, tags);
            }
        }).showDialog();
    }

    public void changeBitrate() {
        new BaseSingleSelectionDialog<>(
                this,
                App.getResString(R.string.dialog_bitrate_title),
                BitrateAdapter.class,
                new ArrayList<>(Arrays.asList(Bitrate.values())))
                .attachListener(new BaseSingleSelectionDialog.IDialogSelectionListener<Bitrate>() {
                    @Override
                    public void onDialogClosed(Bitrate item) {
                        if (item != null) {
                            mBitrateObservable.set(item.getBitrate());
                            ApplicationManager.getInstance().updateBitrate(SettingsActivity.this, item.getBitrate());
                        }
                    }
                })
                .showDialog();
    }

    public void openLameWeb() {
        openWeb(App.getResString(R.string.settings_powered_by_lame_url));
    }

    public void openPlaymarketUrl() {
        openWeb(getPlaymarketUrl());
    }

    private String getPlaymarketUrl() {
        return Constants.PLAYMARKET_BASE_URL;
    }

    private void openWeb(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mUseEmbeddedPlayerObservable.removeOnPropertyChangedCallback(mUseEmbeddedPlayerCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUseEmbeddedPlayerObservable.addOnPropertyChangedCallback(mUseEmbeddedPlayerCallback);
    }

    private Observable.OnPropertyChangedCallback mUseEmbeddedPlayerCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            ApplicationManager.getInstance().updateUseEmbeddedPlayer(SettingsActivity.this, ((ObservableBoolean) observable).get());
        }
    };
}
