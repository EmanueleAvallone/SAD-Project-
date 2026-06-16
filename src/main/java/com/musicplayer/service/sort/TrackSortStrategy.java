package com.musicplayer.service.sort;

import com.musicplayer.model.Track;

import java.util.Comparator;

/**
 * Interfaccia comune per le strategie di ordinamento delle tracce.
 * <p>
 * Ogni strategia concreta definisce un criterio diverso di ordinamento,
 * ad esempio titolo, autore, durata, anno o numero di riproduzioni.
 * </p>
 */
public interface TrackSortStrategy {

    /**
     * Restituisce il comparatore associato alla strategia di ordinamento.
     *
     * @return comparatore per ordinare le tracce secondo il criterio scelto
     */
    Comparator<Track> getComparator();
}
