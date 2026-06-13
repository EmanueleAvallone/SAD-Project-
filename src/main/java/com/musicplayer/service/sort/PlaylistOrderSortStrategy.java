package com.musicplayer.service.sort;
import com.musicplayer.model.Track;

import java.util.Comparator;
import java.util.List;

/**
 * Strategia di ordinamento delle tracce secondo la posizione originale
 * all'interno di una playlist.
 * <p>
 * Questa strategia verrà usata nella Selected Playlist per la colonna "#".
 * </p>
 */
public class PlaylistOrderSortStrategy implements TrackSortStrategy {

    private final List<Track> originalOrder;

    public PlaylistOrderSortStrategy(List<Track> originalOrder) {
        this.originalOrder = originalOrder;
    }

    @Override
    public Comparator<Track> getComparator() {
        return Comparator.comparingInt(this::getOriginalIndex);
    }

    private int getOriginalIndex(Track track) {

        if (originalOrder == null || track == null) {
            return Integer.MAX_VALUE;
        }

        int index = originalOrder.indexOf(track);

        if (index < 0) {
            return Integer.MAX_VALUE;
        }

        return index;
    }
}
