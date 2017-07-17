package com.onix.recorder.lame.core;

public class LameCore {

    static {
        System.loadLibrary("mp3lame");
    }

    public LameCore(LameBuilder builder) {
        initialize(builder);
    }

    private void initialize(LameBuilder builder) {
        initialize(builder.getInSampleRate(),
                builder.getOutChannel(),
                builder.getOutSampleRate(),
                builder.getOutBitrate(),
                builder.getScaleInput(),
                getIntForMode(builder.getMode()),
                getIntForVbrMode(builder.getVbrMode()),
                builder.getQuality(),
                builder.getVbrQuality(),
                builder.getAbrMeanBitrate(),
                builder.getLowpassFreq(),
                builder.getHighpassFreq(),
                builder.getId3tagTitle(),
                builder.getId3tagArtist(),
                builder.getId3tagAlbum(),
                builder.getId3tagYear(),
                builder.getId3tagComment());
    }

    public int encode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf) {
        return lameEncode(buffer_l, buffer_r, samples, mp3buf);
    }

    public int encodeBufferInterLeaved(short[] pcm, int samples, byte[] mp3buf) {
        return encodeBufferInterleaved(pcm, samples, mp3buf);
    }

    public int flush(byte[] mp3buf) {
        return lameFlush(mp3buf);
    }

    public void close() {
        lameClose();
    }

    private static native void initialize(int inSamplerate,
                                          int outChannel,
                                          int outSamplerate,
                                          int outBitrate,
                                          float scaleInput,
                                          int mode,
                                          int vbrMode,
                                          int quality,
                                          int vbrQuality,
                                          int abrMeanBitrate,
                                          int lowpassFreq,
                                          int highpassFreq,
                                          String id3tagTitle,
                                          String id3tagArtist,
                                          String id3tagAlbum,
                                          String id3tagYear,
                                          String id3tagComment);

    private native static int lameEncode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf);

    private native static int encodeBufferInterleaved(short[] pcm, int samples, byte[] mp3buf);

    private native static int lameFlush(byte[] mp3buf);

    private native static void lameClose();

    ////UTILS
    private static int getIntForMode(LameBuilder.Mode mode) {
        switch (mode) {
            case STEREO:
                return 0;

            case JSTEREO:
                return 1;

            case MONO:
                return 3;

            case DEFAULT:
                return 4;
        }

        return -1;
    }

    private static int getIntForVbrMode(LameBuilder.VbrMode mode) {
        switch (mode) {
            case VBR_OFF:
                return 0;

            case VBR_RH:
                return 2;

            case VBR_ABR:
                return 3;

            case VBR_MTRH:
                return 4;

            case VBR_DEFAUT:
                return 6;
        }

        return -1;
    }
}
