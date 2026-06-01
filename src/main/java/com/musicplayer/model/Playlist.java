package com.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String name;
    private final List<Track> tracks;

    public Playlist(String name) {
        this.name = name;
        this.tracks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    //posso aggiungere solo tracce valide
    public void addTrack(Track track) {
        if (track == null) { //se aggiungo una traccia null
            throw new IllegalArgumentException("La traccia non può essere nulla.");
        }

        if (tracks.contains(track)) { //se aggiungo una traccia già presente
            throw new IllegalArgumentException("La traccia è già presente in questa playlist.");
        }

        tracks.add(track);
    }

    // Posso rimuovere solo tracce valide e presenti nella playlist
    /**
     * Rimuove una traccia dalla playlist.
     *
     * Se la traccia non è presente, il metodo non produce errori.
     * Questa scelta permette al TrackService di scorrere tutte le playlist
     * quando una traccia viene eliminata dal catalogo principale.
     *
     * @param track traccia da rimuovere
     * @throws IllegalArgumentException se la traccia è null
     */
    public void removeTrack(Track track) {
        if (track == null) {
            throw new IllegalArgumentException("La traccia da rimuovere non può essere nulla.");
        }

        tracks.remove(track);
    }

    @Override
    public String toString() {
        return name;
    }
}