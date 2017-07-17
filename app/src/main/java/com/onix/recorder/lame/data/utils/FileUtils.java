package com.onix.recorder.lame.data.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FileUtils {

    public static final int ATTEMPT_COUNT_ON_SAVING_FILE_WITH_THE_SAME_NAME = 10;

    public static String getDefaultFileName(String savingPath) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.getDefault());
        String formattedDate = sdfDate.format(Calendar.getInstance().getTime());
        String fileName = formattedDate.concat(Constants.MP3_EXTENTION);

        File existFile = new File(savingPath, fileName);
        if (existFile.exists()) {
            for (int i = 0; i < ATTEMPT_COUNT_ON_SAVING_FILE_WITH_THE_SAME_NAME; i++) {
                String uniqueEnding = String.format(Locale.getDefault(), "(%d)", i + 1);
                String tempFileName = fileName.replace(Constants.MP3_EXTENTION, uniqueEnding.concat(Constants.MP3_EXTENTION));

                existFile = new File(savingPath, tempFileName);
                if (!existFile.exists()) {
                    fileName = fileName.replace(Constants.MP3_EXTENTION, uniqueEnding.concat(Constants.MP3_EXTENTION));
                    break;
                }
            }
        }

        return fileName;
    }
}
