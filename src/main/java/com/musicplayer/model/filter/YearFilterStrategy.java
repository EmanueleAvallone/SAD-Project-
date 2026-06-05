package com.musicplayer.model.filter;

import com.musicplayer.model.Track;

public class YearFilterStrategy implements TrackFilterStrategy {
    private final int requiredYear;

    public YearFilterStrategy(int requiredYear) {
        this.requiredYear = requiredYear;
    }

    @Override
    public boolean matches(Track track) {
        // Ritorna true solo se l'anno corrisponde e se è un anno valido (es. maggiore di 0)
        return track.getYear() > 0 && track.getYear() == requiredYear;
    }
}