package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import javafx.collections.ObservableList;

/**
 * Service che contiene la logica applicativa relativa alla gestione delle tracce.
 *
 * Il controller usa questa classe per non contenere direttamente la business logic.
 * In questo modo si mantiene separata la gestione della UI dalla gestione del modello.
 */
public class TrackService {

    /**
     * Aggiunge una traccia al catalogo principale.
     *
     * @param tracks lista principale delle tracce
     * @param track traccia da aggiungere
     * @throws IllegalArgumentException se la traccia è null
     */
    public void addTrack(ObservableList<Track> tracks, Track track) {
        if (track == null) {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }

        tracks.add(track);
    }

    /**
     * Rimuove una traccia dal catalogo principale e da tutte le playlist.
     *
     * @param tracks lista principale delle tracce
     * @param playlists lista delle playlist esistenti
     * @param track traccia da rimuovere
     * @throws IllegalArgumentException se la traccia è null
     */
    public void removeTrack(ObservableList<Track> tracks,
                            ObservableList<Playlist> playlists,
                            Track track) {
        if (track == null) {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }

        tracks.remove(track);

        for (Playlist playlist : playlists) {
            playlist.removeTrack(track);
        }
    }

    /**
     * Aggiorna i campi modificabili di una traccia esistente.
     *
     * La durata non viene aggiornata perché, secondo i requisiti, è un dato fisso
     * calcolato o derivato dal file audio.
     *
     * @param originalTrack traccia già presente nel catalogo
     * @param editedTrack traccia contenente i nuovi valori
     * @throws IllegalArgumentException se una delle due tracce è null
     */
    public void updateEditableFields(Track originalTrack, Track editedTrack) {
        if (originalTrack == null || editedTrack == null) {
            throw new IllegalArgumentException("Le tracce non possono essere null.");
        }

        originalTrack.setTitle(editedTrack.getTitle());
        originalTrack.setAuthor(editedTrack.getAuthor());
        originalTrack.setGenre(editedTrack.getGenre());
        originalTrack.setYear(editedTrack.getYear());
    }
}