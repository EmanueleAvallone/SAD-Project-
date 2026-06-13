package com.musicplayer.model;

import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

/**
 * Rappresenta una traccia audio nella libreria.
 */
public class Track {

    private String audioFilePath;
    private String title;
    private String author;
    private String length;
    private String genre;
    private int year;
    private int playedCount;
    private Set<Tag> tags;
    private LocalDateTime deletedAt;

    /**
     * Crea una traccia senza file audio associato.
     *
     * Questo costruttore mantiene la compatibilità con il codice già esistente:
     * le tracce possono continuare a essere create anche senza audioFilePath.
     */
    public Track(String title, String author, String length, String genre, int year) {
        this(title, author, length, genre, year, null);
    }

    /**
     * Crea una traccia con eventuale file audio associato.
     *
     * @param title titolo della traccia
     * @param author autore della traccia
     * @param length durata della traccia
     * @param genre genere musicale
     * @param year anno della traccia
     * @param audioFilePath percorso del file audio associato, oppure null
     */
    public Track(String title,
                 String author,
                 String length,
                 String genre,
                 int year,
                 String audioFilePath) {

        this.title = title;
        this.author = author;
        this.length = length;
        this.genre = genre;
        this.year = year;
        this.audioFilePath = audioFilePath;

        this.playedCount = 0;
        this.tags = new HashSet<>();
        this.deletedAt = null;
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

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public boolean hasAudioFile() {
        return audioFilePath != null && !audioFilePath.trim().isEmpty();
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}