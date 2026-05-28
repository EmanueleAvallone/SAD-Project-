package com.musicplayer.model;

/**
 * Rappresenta una traccia audio nella libreria.
 */
public class Track {

    private String title;
    private String author;
    private String length;
    private String genre;
    private int year;
    private int playedCount;

    public Track(String title, String author, String length, String genre, int year) {
        this.title = title;
        this.author = author;
        this.length = length;
        this.genre = genre;
        this.year = year;
        this.playedCount = 0;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getLength() {
        return length;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public int getPlayedCount() {
        return playedCount;
    }

    public void incrementPlayedCount() {
        playedCount++;
    }
}