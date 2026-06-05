package com.musicplayer.model;

import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Tag> tags;

    public Track(String title, String author, String length, String genre, int year) {
        this.title = title;
        this.author = author;
        this.length = length;
        this.genre = genre;
        this.year = year;
        this.playedCount = 0;
        this.tags = new HashSet<>();
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

    //aggiungo i setter per la modifica della Traccia.
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPlayedCount(int playedCount) {
        this.playedCount = playedCount;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
    
    public boolean hasTag(Tag tag) {
        return this.tags.contains(tag);
    }

    //per controllare se due tracce sono duplicate (titolo e autore uguali)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Track other)) {
            return false;
        }

        return Objects.equals(title, other.title) && Objects.equals(author, other.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
}