package com.onix.recorder.lame.data.utils;

public class Constants {

    public static final String PREFS_CONFIG = "PREFS_CONFIG";
    public static final int BLINKING_TIMEOUT = 300;
    public static final int UNDEFINED_INT = -1;
    public static final String MP3_EXTENTION = ".mp3";

    public static final String EXTRA_UPDATE_RECORD_TIME = "EXTRA_UPDATE_RECORD_TIME";
    public static final String EXTRA_RECORDING_STATE = "EXTRA_RECORDING_STATE";
    public static final String EXTRA_TIMER_STATE = "EXTRA_TIMER_STATE";
    public static final String EXTRA_RECORD = "EXTRA_RECORD";

    public static final String EXTRA_ERROR = "EXTRA_ERROR";
    public static final String EXTRA_ERROR_PERMISSION = "EXTRA_ERROR_PERMISSION";

    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_START_RECORDING = "ACTION_START_RECORDING";
    public static final String ACTION_STOP_RECORDING = "ACTION_STOP_RECORDING";
    public static final String ACTION_STOP_RECORDING_FROM_WIDGET = "ACTION_STOP_RECORDING_FROM_WIDGET";
    public static final String ACTION_ABORT_RECORDING = "ACTION_ABORT_RECORDING";
    public static final String ACTION_ERROR = "ACTION_ERROR";

    public static final String ACTION_LISTENER_RECORDING_ONSTART = "ACTION_LISTENER_RECORDING_ONSTART";
    public static final String ACTION_LISTENER_RECORDING_ONSTOP = "ACTION_LISTENER_RECORDING_ONSTOP";

    public static final long DEFAULT_MAX_RECORD_LENGTH = 1000 * 60 * 60; // 1 hour
    public static final long DEFAULT_TICK_LENGTH = 1000; // 1 seconds

    public static final String MEDIA_CACHE_SUB_DIR = "tracks";

    public static final String PLAYMARKET_BASE_URL = "https://play.google.com/store/apps/developer?id=Onix-Systems%20Team";
}
