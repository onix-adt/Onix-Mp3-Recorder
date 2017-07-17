package com.onix.recorder.lame.core;

public class LameBuilder {

    enum Mode {
        STEREO, JSTEREO, MONO, DEFAULT
    }

    enum VbrMode {
        VBR_OFF, VBR_RH, VBR_MTRH, VBR_ABR, VBR_DEFAUT
    }

    private int inSampleRate;
    private int outSampleRate;
    private int outBitrate;
    private int outChannel;
    private int quality;
    private int vbrQuality;
    private int abrMeanBitrate;
    private int lowpassFreq;
    private int highpassFreq;
    private float scaleInput;
    private Mode mode;
    private VbrMode vbrMode;
    private String id3tagTitle;
    private String id3tagArtist;
    private String id3tagAlbum;
    private String id3tagComment;
    private String id3tagYear;

    public int getInSampleRate() {
        return inSampleRate;
    }

    public int getOutSampleRate() {
        return outSampleRate;
    }

    public int getOutBitrate() {
        return outBitrate;
    }

    public int getOutChannel() {
        return outChannel;
    }

    public int getQuality() {
        return quality;
    }

    public int getVbrQuality() {
        return vbrQuality;
    }

    public int getAbrMeanBitrate() {
        return abrMeanBitrate;
    }

    public int getLowpassFreq() {
        return lowpassFreq;
    }

    public int getHighpassFreq() {
        return highpassFreq;
    }

    public float getScaleInput() {
        return scaleInput;
    }

    public Mode getMode() {
        return mode;
    }

    public VbrMode getVbrMode() {
        return vbrMode;
    }

    public String getId3tagTitle() {
        return id3tagTitle;
    }

    public String getId3tagArtist() {
        return id3tagArtist;
    }

    public String getId3tagAlbum() {
        return id3tagAlbum;
    }

    public String getId3tagComment() {
        return id3tagComment;
    }

    public String getId3tagYear() {
        return id3tagYear;
    }

    public LameBuilder() {
        this.id3tagTitle = null;
        this.id3tagAlbum = null;
        this.id3tagArtist = null;
        this.id3tagComment = null;
        this.id3tagYear = null;

        this.inSampleRate = 44100;
        this.outChannel = 2;
        this.outBitrate = 128;
        this.scaleInput = 1;

        this.quality = 5;
        this.mode = Mode.DEFAULT;
        this.vbrMode = VbrMode.VBR_OFF;
        this.vbrQuality = 5;
        this.abrMeanBitrate = 128;
    }

    public LameBuilder setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public LameBuilder setInSampleRate(int inSampleRate) {
        this.inSampleRate = inSampleRate;
        return this;
    }

    public LameBuilder setOutSampleRate(int outSampleRate) {
        this.outSampleRate = outSampleRate;
        return this;
    }

    public LameBuilder setOutBitrate(int bitrate) {
        this.outBitrate = bitrate;
        return this;
    }

    public LameBuilder setOutChannels(int channels) {
        this.outChannel = channels;
        return this;
    }

    public LameBuilder setId3tagTitle(String title) {
        this.id3tagTitle = title;
        return this;
    }

    public LameBuilder setId3tagArtist(String artist) {
        this.id3tagArtist = artist;
        return this;
    }

    public LameBuilder setId3tagAlbum(String album) {
        this.id3tagAlbum = album;
        return this;
    }

    public LameBuilder setId3tagComment(String comment) {
        this.id3tagComment = comment;
        return this;
    }

    public LameBuilder setId3tagYear(String year) {
        this.id3tagYear = year;
        return this;
    }

    public LameBuilder setScaleInput(float scaleAmount) {
        this.scaleInput = scaleAmount;
        return this;
    }

    public LameBuilder setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public LameBuilder setVbrMode(VbrMode mode) {
        this.vbrMode = mode;
        return this;
    }

    public LameBuilder setVbrQuality(int quality) {
        this.vbrQuality = quality;
        return this;
    }

    public LameBuilder setAbrMeanBitrate(int bitrate) {
        this.abrMeanBitrate = bitrate;
        return this;
    }

    public LameBuilder setLowpassFreqency(int freq) {
        this.lowpassFreq = freq;
        return this;
    }

    public LameBuilder setHighpassFreqency(int freq) {
        this.highpassFreq = freq;
        return this;
    }

    public LameCore build() {
        return new LameCore(this);
    }
}
