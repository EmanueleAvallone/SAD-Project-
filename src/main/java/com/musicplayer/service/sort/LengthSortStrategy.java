package com.musicplayer.service.sort;

import com.musicplayer.model.Track;

import java.util.Comparator;

/**
 * Strategia di ordinamento delle tracce per durata.
 * <p>
 * La durata viene convertita in secondi per evitare ordinamenti lessicografici
 * errati sulle stringhe.
 * </p>
 */
public class LengthSortStrategy implements TrackSortStrategy {

    @Override
    public Comparator<Track> getComparator() {
        return Comparator.comparingInt(track -> parseDuration(track.getLength()));
    }

    /**
     * Converte una durata in secondi.
     * <p>
     * Sono supportati i formati:
     * - mm:ss
     * - numero intero interpretato come minuti
     * </p>
     * <p>
     * Se il valore è nullo, vuoto o non valido, viene restituito 0.
     * </p>
     *
     * @param length durata della traccia
     * @return durata in secondi
     */

    private int parseDuration(String length) {
        if (length == null || length.trim().isEmpty()) {
            return 0;
        }

        String value = length.trim();

        try {
            if (!value.contains(":")) {
                int minutes = Integer.parseInt(value);
                return minutes * 60;
            }

            String[] parts = value.split(":");

            if (parts.length != 2) {
                return 0;
            }

            int minutes = Integer.parseInt(parts[0].trim());
            int seconds = Integer.parseInt(parts[1].trim());
            return minutes * 60 + seconds;
        } catch (NumberFormatException exception) {

            return 0;
        }
    }
}
