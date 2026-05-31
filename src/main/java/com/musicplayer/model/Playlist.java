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
    public void removeTrack(Track track) {
        if (track == null) { // se provo a rimuovere una traccia null
            throw new IllegalArgumentException("La traccia da rimuovere non può essere nulla.");
        }

        if (!tracks.contains(track)) { // se la traccia non è nella playlist
            throw new IllegalArgumentException("La traccia non è presente in questa playlist.");
        }

        tracks.remove(track);
    }

    @Override
    public String toString() {
        return name;
    }
}