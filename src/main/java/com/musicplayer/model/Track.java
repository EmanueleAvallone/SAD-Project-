package com.musicplayer.model;

import java.util.Set;

public class Track {
    private String author;
    private String title;
    private String genre;
    private int year;
    private int secondsduration;
    private int playCount;
    private String filePath;

    private Set<Tag> tags;
}
