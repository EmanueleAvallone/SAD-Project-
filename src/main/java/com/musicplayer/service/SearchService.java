package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service responsabile della ricerca testuale su tracce e playlist.
 * <p>
 * Le ricerche sono case-insensitive e ignorano spazi iniziali/finali.
 * Il servizio offre sia metodi di ricerca che restituiscono una lista filtrata,
 * sia metodi booleani di supporto per verificare la corrispondenza di un singolo
 * elemento con una query.
 * </p>
 */
public class SearchService {

    /**
     * Cerca le tracce che corrispondono alla query indicata.
     * <p>
     * La ricerca viene effettuata su titolo e autore della traccia.
     * Il confronto è case-insensitive e la query viene normalizzata
     * rimuovendo spazi iniziali e finali.
     * </p>
     *
     * <p>
     * Se la lista in input è {@code null}, il metodo restituisce una lista vuota.
     * Se la query è {@code null} o vuota, il metodo restituisce una copia della
     * lista originale senza applicare filtri.
     * </p>
     *
     * @param tracks lista di tracce da filtrare
     * @param query testo da cercare
     * @return lista delle tracce che corrispondono alla query, oppure una lista
     *         vuota se l'input è {@code null}
     */
    public List<Track> searchTracks(List<Track> tracks, String query) {
        if (tracks == null) {
            return List.of();
        }

        if (query == null || query.isBlank()) {
            return List.copyOf(tracks);
        }

        return tracks.stream()
                .filter(track -> matchesTrack(track, query))
                .collect(Collectors.toList());
    }

    /**
     * Verifica se una traccia corrisponde alla query indicata.
     * <p>
     * Il confronto viene effettuato su titolo, autore e combinazione
     * titolo-autore, in modo case-insensitive.
     * </p>
     *
     * <p>
     * Se la traccia è {@code null}, il metodo restituisce {@code false}.
     * Se la query è {@code null} o vuota, il metodo restituisce {@code true}.
     * </p>
     *
     * @param track traccia da confrontare
     * @param query testo da cercare
     * @return {@code true} se la traccia corrisponde alla query, altrimenti {@code false}
     */
    public boolean matchesTrack(Track track, String query) {
        if (track == null) {
            return false;
        }

        if (query == null || query.isBlank()) {
            return true;
        }

        String normalizedQuery = normalize(query);

        String title = normalize(track.getTitle());
        String author = normalize(track.getAuthor());
        String combined = (title + " " + author).trim();

        return title.contains(normalizedQuery)
                || author.contains(normalizedQuery)
                || combined.contains(normalizedQuery);
    }

    /**
     * Cerca le playlist che corrispondono alla query indicata.
     * <p>
     * La ricerca viene effettuata sul nome della playlist.
     * Il confronto è case-insensitive e la query viene normalizzata
     * rimuovendo spazi iniziali e finali.
     * </p>
     *
     * <p>
     * Se la lista in input è {@code null}, il metodo restituisce una lista vuota.
     * Se la query è {@code null} o vuota, il metodo restituisce una copia della
     * lista originale senza applicare filtri.
     * </p>
     *
     * @param playlists lista di playlist da filtrare
     * @param query testo da cercare
     * @return lista delle playlist che corrispondono alla query, oppure una lista
     *         vuota se l'input è {@code null}
     */
    public List<Playlist> searchPlaylists(List<Playlist> playlists, String query) {
        if (playlists == null) {
            return List.of();
        }

        if (query == null || query.isBlank()) {
            return List.copyOf(playlists);
        }

        return playlists.stream()
                .filter(playlist -> matchesPlaylist(playlist, query))
                .collect(Collectors.toList());
    }

    /**
     * Verifica se una playlist corrisponde alla query indicata.
     * <p>
     * Il confronto viene effettuato sul nome della playlist in modo
     * case-insensitive.
     * </p>
     *
     * <p>
     * Se la playlist è {@code null}, il metodo restituisce {@code false}.
     * Se la query è {@code null} o vuota, il metodo restituisce {@code true}.
     * </p>
     *
     * @param playlist playlist da confrontare
     * @param query testo da cercare
     * @return {@code true} se la playlist corrisponde alla query, altrimenti {@code false}
     */
    public boolean matchesPlaylist(Playlist playlist, String query) {
        if (playlist == null) {
            return false;
        }

        if (query == null || query.isBlank()) {
            return true;
        }

        String normalizedQuery = normalize(query);
        String name = normalize(playlist.getName());

        return name.contains(normalizedQuery);
    }

    /**
     * Normalizza una stringa per la ricerca.
     * <p>
     * La normalizzazione converte il valore in minuscolo usando {@link Locale#ROOT}
     * e rimuove gli spazi iniziali e finali.
     * Se il valore è {@code null}, restituisce una stringa vuota.
     * </p>
     *
     * @param value stringa da normalizzare
     * @return stringa normalizzata, oppure stringa vuota se il valore è {@code null}
     */
    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }
}