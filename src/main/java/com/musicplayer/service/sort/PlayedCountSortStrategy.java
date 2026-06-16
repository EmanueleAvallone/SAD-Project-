package com.musicplayer.service.sort;

import com.musicplayer.model.Track;

import java.util.Comparator;

/**
 * Strategia di ordinamento delle tracce per numero di riproduzioni.
 */
public class PlayedCountSortStrategy implements TrackSortStrategy {

    @Override
    public Comparator<Track> getComparator() {
        return Comparator.comparingInt(Track::getPlayedCount);
    }
}
