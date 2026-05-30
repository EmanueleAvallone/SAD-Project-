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
            throw new IllegalArgumentException("Track cannot be null");
        }

        if (tracks.contains(track)) { //se aggiungo una traccia già presente
            throw new IllegalArgumentException("Track already exists in this playlist");
        }

        tracks.add(track);
    }

    public void removeTrack(Track track) {
        tracks.remove(track);
    }

    @Override
    public String toString() {
        return name;
    }
}