package com.musicplayer.model.filter;

import com.musicplayer.model.Track;

public class GenreFilterStrategy implements TrackFilterStrategy {
    private final String requiredGenre;

    public GenreFilterStrategy(String requiredGenre) {
        this.requiredGenre = requiredGenre != null ? requiredGenre.trim().toLowerCase() : "";
    }

    @Override
    public boolean matches(Track track) {
        if (requiredGenre.isEmpty() || track.getGenre() == null) {
            return false;
        }
        return track.getGenre().trim().toLowerCase().equals(requiredGenre);
    }
}