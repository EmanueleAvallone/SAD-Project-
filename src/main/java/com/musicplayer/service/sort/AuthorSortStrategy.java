package com.musicplayer.service.sort;

import com.musicplayer.model.Track;

import java.util.Comparator;

/**
 * Strategia di ordinamento delle tracce per autore.
 */
public class AuthorSortStrategy implements TrackSortStrategy {

    @Override
    public Comparator<Track> getComparator() {
        return Comparator.comparing(
                track -> safeText(track.getAuthor()),
                String.CASE_INSENSITIVE_ORDER
        );
    }

    private String safeText(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
