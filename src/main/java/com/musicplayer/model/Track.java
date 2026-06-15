package com.musicplayer.model;

import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
     * Costruttore vuoto richiesto da librerie di serializzazione/deserializzazione
     * come Jackson.
     */
    public Track() {
        this.tags = new HashSet<>();
    }

    /**
     * Crea una traccia senza file audio associato.
     *
     * Questo costruttore mantiene la compatibilità con il codice già esistente:
     * le tracce possono continuare a essere create anche senza audioFilePath.
     *
     * @param title titolo della traccia
     * @param author autore della traccia
     * @param length durata della traccia
     * @param genre genere musicale
     * @param year anno della traccia
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

    /**
     * Costruttore completo usato in deserializzazione JSON.
     *
     * @param audioFilePath percorso del file audio associato
     * @param title titolo della traccia
     * @param author autore della traccia
     * @param length durata della traccia
     * @param genre genere musicale
     * @param year anno della traccia
     * @param playedCount numero di riproduzioni accumulate
     * @param tags insieme dei tag associati alla traccia
     * @param deletedAt timestamp di eliminazione logica, oppure null
     */
    @JsonCreator
    public Track(@JsonProperty("audioFilePath") String audioFilePath,
                 @JsonProperty("title") String title,
                 @JsonProperty("author") String author,
                 @JsonProperty("length") String length,
                 @JsonProperty("genre") String genre,
                 @JsonProperty("year") int year,
                 @JsonProperty("playedCount") int playedCount,
                 @JsonProperty("tags") Set<Tag> tags,
                 @JsonProperty("deletedAt") LocalDateTime deletedAt) {
        this.audioFilePath = audioFilePath;
        this.title = title;
        this.author = author;
        this.length = length;
        this.genre = genre;
        this.year = year;
        this.playedCount = playedCount;
        this.tags = (tags != null) ? new HashSet<>(tags) : new HashSet<>();
        this.deletedAt = deletedAt;
    }

    /**
     * Restituisce il percorso del file audio associato.
     *
     * @return percorso del file audio, oppure null se assente
     */
    public String getAudioFilePath() {
        return audioFilePath;
    }

    /**
     * Imposta il percorso del file audio associato.
     *
     * @param audioFilePath nuovo percorso del file audio
     */
    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    /**
     * Restituisce il titolo della traccia.
     *
     * @return titolo della traccia
     */
    public String getTitle() {
        return title;
    }

    /**
     * Imposta il titolo della traccia.
     *
     * @param title nuovo titolo della traccia
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Restituisce l'autore della traccia.
     *
     * @return autore della traccia
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Imposta l'autore della traccia.
     *
     * @param author nuovo autore della traccia
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Restituisce la durata della traccia.
     *
     * @return durata della traccia
     */
    public String getLength() {
        return length;
    }

    /**
     * Imposta la durata della traccia.
     *
     * @param length nuova durata della traccia
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * Restituisce il genere musicale della traccia.
     *
     * @return genere musicale
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Imposta il genere musicale della traccia.
     *
     * @param genre nuovo genere musicale
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Restituisce l'anno della traccia.
     *
     * @return anno della traccia
     */
    public int getYear() {
        return year;
    }

    /**
     * Imposta l'anno della traccia.
     *
     * @param year nuovo anno della traccia
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Restituisce il numero di riproduzioni della traccia.
     *
     * @return numero di riproduzioni
     */
    public int getPlayedCount() {
        return playedCount;
    }

    /**
     * Imposta il numero di riproduzioni della traccia.
     *
     * @param playedCount nuovo numero di riproduzioni
     */
    public void setPlayedCount(int playedCount) {
        this.playedCount = playedCount;
    }

    /**
     * Incrementa di uno il numero di riproduzioni della traccia.
     */
    public void incrementPlayedCount() {
        playedCount++;
    }

    /**
     * Restituisce l'insieme dei tag associati alla traccia.
     *
     * @return insieme dei tag
     */
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Sostituisce l'insieme dei tag associati alla traccia.
     *
     * @param tags nuovo insieme di tag
     */
    public void setTags(Set<Tag> tags) {
        this.tags = (tags != null) ? new HashSet<>(tags) : new HashSet<>();
    }

    /**
     * Aggiunge un tag alla traccia.
     *
     * @param tag tag da aggiungere
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    /**
     * Rimuove un tag dalla traccia.
     *
     * @param tag tag da rimuovere
     */
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    /**
     * Verifica se la traccia possiede un determinato tag.
     *
     * @param tag tag da verificare
     * @return true se il tag è presente, false altrimenti
     */
    public boolean hasTag(Tag tag) {
        return this.tags.contains(tag);
    }

    /**
     * Verifica se la traccia ha un file audio associato.
     *
     * @return true se il percorso audio è valorizzato, false altrimenti
     */
    public boolean hasAudioFile() {
        return audioFilePath != null && !audioFilePath.trim().isEmpty();
    }

    /**
     * Restituisce la data e ora di eliminazione logica della traccia.
     *
     * @return timestamp di eliminazione, oppure null
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * Imposta la data e ora di eliminazione logica della traccia.
     *
     * @param deletedAt timestamp di eliminazione
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * Confronta due tracce in base a titolo e autore.
     *
     * @param obj oggetto da confrontare
     * @return true se le tracce sono considerate uguali
     */
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

    /**
     * Restituisce l'hash code coerente con equals.
     *
     * @return hash code della traccia
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
}