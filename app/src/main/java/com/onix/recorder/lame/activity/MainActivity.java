package com.onix.recorder.lame.activity;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.onix.recorder.lame.App;
import com.onix.recorder.lame.R;
import com.onix.recorder.lame.adapters.TrackRecordAdapter;
import com.onix.recorder.lame.components.audio.HomeAudioPlayer;
import com.onix.recorder.lame.components.audio.MediaBinder;
import com.onix.recorder.lame.components.recording.RecordBinder;
import com.onix.recorder.lame.data.helpers.ApplicationManager;
import com.onix.recorder.lame.data.helpers.ContextMenuHelper;
import com.onix.recorder.lame.data.helpers.TrackRecordProvider;
import com.onix.recorder.lame.data.media.interfaces.IAudioTrack;
import com.onix.recorder.lame.data.media.interfaces.IPlayerInitializationListener;
import com.onix.recorder.lame.data.model.TrackId3Tags;
import com.onix.recorder.lame.data.model.TrackRecord;
import com.onix.recorder.lame.data.utils.Constants;
import com.onix.recorder.lame.data.utils.NavigationHelper;
import com.onix.recorder.lame.databinding.ActivityMainBinding;
import com.onix.recorder.lame.dialog.RecordDialog;
import com.onix.recorder.lame.dialog.RenameDialog;
import com.onix.recorder.lame.enums.RecordingState;
import com.onix.recorder.lame.interfaces.IRecordInterface;
import com.onix.recorder.lame.interfaces.IRecordTimerListener;
import com.onix.recorder.lame.views.WidgetRecorder;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements IRecordInterface {

    private ActivityMainBinding mBinding;

    private ObservableBoolean mIsSearchMode = new ObservableBoolean();
    private ObservableBoolean mShowPermissionError = new ObservableBoolean();
    private ObservableBoolean mShowPlayer = new ObservableBoolean();

    private RxPermissions mRxPermissions;
    private RecordDialog mRecordDialog;

    private MediaBinder mMediaBinder;
    private RecordBinder mRecordBinder;
    private IRecordTimerListener mRecordTimeListener;

    private HomeAudioPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMediaServiceBinder().onCreate();
        getRecordServiceBinder().onCreate();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setHandler(this);
        mBinding.rvRecords.setAdapter(new TrackRecordAdapter());

        mRxPermissions = new RxPermissions(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_purple));
        }

        mPlayer = new HomeAudioPlayer(this);
        mBinding.rlPlayerContainer.addView(mPlayer.createView());
        mShowPlayer.set(false);

        initWidget();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getMediaServiceBinder().onStart();
        getRecordServiceBinder().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRxPermissions != null) {
            mRxPermissions.request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            mShowPermissionError.set(!granted);
                        }
                    });
        }

        mBinding.edtSearch.addTextChangedListener(mSearchWatcher);

        getAdapter().setItems(TrackRecordProvider.getAudioRecords());
        getAdapter().setItemClickListener(new TrackRecordAdapter.IRecordClickListener() {
            @Override
            public void onRecordClick(TrackRecord trackRecord) {
                actionPlayTrack(trackRecord);
            }

            @Override
            public void onMore(final View view, final TrackRecord trackRecord) {
                ContextMenuHelper.showMenu(view, MainActivity.this, R.menu.track_menu, new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_play:
                                actionPlayTrack(trackRecord);
                                break;

                            case R.id.menu_item_rename:
                                showRenameDialog(view.getContext(), trackRecord);
                                break;

                            case R.id.menu_item_delete:
                                removeRecord(trackRecord);
                                break;

                            case R.id.menu_item_share:
                                NavigationHelper.shareRecord(MainActivity.this, trackRecord);
                                break;
                        }

                        return false;
                    }
                });
            }
        });

        getRecordServiceBinder().setRecordInterface(this);
        if (getIntent() != null
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Constants.ACTION_STOP_RECORDING_FROM_WIDGET)) {
            onStopRecording((TrackRecord) getIntent().getSerializableExtra(Constants.EXTRA_RECORD));
            setIntent(null);
        }

        mPlayer.onResume(getMediaServiceBinder(), new IPlayerInitializationListener() {
            @Override
            public void onPlayerInitialized() {
                if (mPlayer != null) {
                    IAudioTrack currentTrack = mPlayer.getCurrentTrack();
                    if (currentTrack != null) {
                        mShowPlayer.set(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPlayer.pause();
        getRecordServiceBinder().setRecordInterface(null);
        mBinding.edtSearch.removeTextChangedListener(mSearchWatcher);
    }

    @Override
    protected void onStop() {
        super.onStop();

        getMediaServiceBinder().onStop();
        getRecordServiceBinder().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hideRecordDialog();
    }

    @Override
    public void onStartRecording() {
    }

    @Override
    public void onStopRecording(TrackRecord trackRecord) {
        hideRecordDialog();
        addId3Tags(trackRecord);
        showRenameDialog(this, trackRecord);
    }

    @Override
    public void onUpdateRecordingTime(String formattedTime, long unixSeconds) {
        if (!(mRecordDialog != null && mRecordDialog.isShowing()) && !App.isAppPaused()) {
            showRecordDialog(true);
        }

        if (mRecordTimeListener != null) {
            mRecordTimeListener.onTrackTime(formattedTime, unixSeconds);
        }
    }

    @Override
    public void onRecordError(Throwable e) {
        // TODO handle error (IOException, FileNotFoundException, etc.)
    }

    public ObservableBoolean getShowPlayer() {
        return mShowPlayer;
    }

    public ObservableBoolean getIsSearchMode() {
        return mIsSearchMode;
    }

    public ObservableBoolean getShowPermissionError() {
        return mShowPermissionError;
    }

    private void actionPlayTrack(TrackRecord trackRecord) {
        if (ApplicationManager.getInstance().getConfig(MainActivity.this).isEmbeddedPlayerEnable()) {
            mPlayer.play(trackRecord);
            mShowPlayer.set(true);
        } else {
            NavigationHelper.openThirdPartyPlayer(this, trackRecord);
        }
    }

    public MediaBinder getMediaServiceBinder() {
        if (mMediaBinder == null) {
            mMediaBinder = new MediaBinder(this);
        }

        return mMediaBinder;
    }

    public RecordBinder getRecordServiceBinder() {
        if (mRecordBinder == null) {
            mRecordBinder = new RecordBinder(getApplicationContext());
        }

        return mRecordBinder;
    }

    private void initWidget() {
        Intent widgetIntent = new Intent(Constants.ACTION_LISTENER_RECORDING_ONSTOP);

        int[] ids = AppWidgetManager.getInstance(getApplicationContext())
                .getAppWidgetIds(new ComponentName(getApplicationContext(), WidgetRecorder.class));

        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        widgetIntent.putExtra(Constants.EXTRA_RECORDING_STATE, RecordingState.STOPPED);

        getApplicationContext().sendBroadcast(widgetIntent);
    }

    private TrackRecordAdapter getAdapter() {
        TrackRecordAdapter adapter = (TrackRecordAdapter) mBinding.rvRecords.getAdapter();

        if (adapter == null) {
            throw new IllegalStateException();
        }

        return adapter;
    }

    private void hideRecordDialog() {
        if (mRecordDialog != null && mRecordDialog.isShowing()) {
            mRecordDialog.dismiss();
        }
    }

    private void showRenameDialog(Context context, TrackRecord trackRecord) {
        new RenameDialog(context, trackRecord)
                .attachListener(new RenameDialog.IRenameDialogListener() {
                    @Override
                    public void onRename(TrackRecord trackRecord, String updatedName) {
                        File from = new File(trackRecord.getFilePath(), trackRecord.getFileName());
                        File to = new File(trackRecord.getFilePath(), updatedName);
                        if (from.exists()) {
                            from.renameTo(to);
                            trackRecord.setFileName(updatedName);
                            TrackRecordProvider.saveTrackRecord(trackRecord);
                            getAdapter().updateItem(trackRecord);
                        } else {
                            getAdapter().remove(trackRecord);
                            TrackRecordProvider.remove(trackRecord);
                        }
                    }
                })
                .showDialog();
    }

    private void removeRecord(TrackRecord trackRecord) {
        if (trackRecord == null) {
            return;
        }

        File trackFile = new File(trackRecord.getRealFilePath());
        if (trackFile.exists()) {
            trackFile.delete();
        }

        TrackRecordProvider.remove(trackRecord);
        getAdapter().remove(trackRecord);
    }

    private TextWatcher mSearchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null) {
                getAdapter().getFilter().filter(s.toString());
            }
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvPermissionsError:
                NavigationHelper.openSettingsPermissions(this);
                break;

            case R.id.ivSort:
                ContextMenuHelper.showMenu(view, this, R.menu.sort_menu, new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Comparator<TrackRecord> sortComparator = null;

                        switch (item.getItemId()) {
                            case R.id.menu_item_sort_duration:
                                sortComparator = TrackRecord.COMPARATOR_DURATION;
                                break;

                            case R.id.menu_item_sort_alphabetical:
                                sortComparator = TrackRecord.COMPARATOR_ALPHABETICAL;
                                break;

                            case R.id.menu_item_sort_date:
                                sortComparator = TrackRecord.COMPARATOR_DATE;
                                break;
                        }

                        Collections.sort(getAdapter().getAdapterItems(), sortComparator);
                        getAdapter().notifyDataSetChanged();

                        return true;
                    }
                });
                break;

            case R.id.ivClose:
                getIsSearchMode().set(false);
                getAdapter().resetFilter();
                break;

            case R.id.ivSearch:
                getIsSearchMode().set(true);
                break;

            case R.id.ivSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.btnOpenRecordDialog:
                showRecordDialog(false);
                break;
        }
    }

    private void showRecordDialog(boolean isAlreadyRecording) {
        mRecordDialog = new RecordDialog(this, isAlreadyRecording)
                .attachListener(new RecordDialog.IRecordDialogListener() {
                    @Override
                    public void onStartRecord() {
                        if (getRecordServiceBinder().getRecordController() != null) {
                            getRecordServiceBinder().getRecordController().startRecording();
                        }
                    }

                    @Override
                    public void onStopRecord() {
                        if (getRecordServiceBinder().getRecordController() != null) {
                            getRecordServiceBinder().getRecordController().stopRecording();
                        }
                    }

                    @Override
                    public void onRegisterTimeListener(IRecordTimerListener listener) {
                        mRecordTimeListener = listener;
                    }
                })
                .showDialog();
    }

    private void addId3Tags(final TrackRecord trackRecord) {
        try {
            Mp3File mp3file = new Mp3File(trackRecord.getTempPath());

            ID3v2 id3v2Tag;
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
            } else {
                id3v2Tag = new ID3v24Tag();
                mp3file.setId3v2Tag(id3v2Tag);
            }

            TrackId3Tags trackId3Tags = ApplicationManager.getInstance().getConfig(this).getId3Tags(this);

            id3v2Tag.setArtist(trackId3Tags.getArtist());
            id3v2Tag.setComment(trackId3Tags.getComment());
            id3v2Tag.setTitle(trackId3Tags.getTitle());
            id3v2Tag.setYear(trackId3Tags.getYear());

            mp3file.save(trackRecord.getRealFilePath());

            File tempRecord = new File(trackRecord.getTempPath());
            if (tempRecord.exists()) {
                tempRecord.delete();
                tempRecord = null;
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException e) {
        }
    }
}


