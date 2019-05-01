package com.example.matt.avaloncompanionapp;

public class TTSSegment {
    public enum TTSSegment_Type {
        PAUSE, TEXT
    };

    private TTSSegment_Type type;
    private Long pauseDuration;
    private String text;

    public TTSSegment(Long pauseDuration) {
        this.type = TTSSegment_Type.PAUSE;
        this.pauseDuration = pauseDuration;
        this.text = null;
    }

    public TTSSegment(String speech) {
        this.type = TTSSegment_Type.TEXT;
        this.pauseDuration = null;
        this.text = speech;
    }

    public TTSSegment_Type getType() {
        return type;
    }

    public Long getPauseDuration() {
        return pauseDuration;
    }

    public String getText() {
        return text;
    }
}