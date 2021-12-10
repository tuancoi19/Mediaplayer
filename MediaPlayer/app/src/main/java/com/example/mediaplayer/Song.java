package com.example.mediaplayer;

public class Song {
    private String s_name, s_path, s_album, s_artist;
    private int i_duration;

    public Song (String name, String path, String album, String artist, int duration) {
        this.s_name = name;
        this.s_path = path;
        this.s_album = album;
        this.s_artist = artist;
        this.i_duration = duration;
    }

    public String getS_name() {
        return s_name;
    }

    public String getS_path() {
        return s_path;
    }

    public String getS_album() {
        return s_album;
    }

    public String getS_artist() {
        return s_artist;
    }

    public int getI_duration() {
        return i_duration;
    }
}
