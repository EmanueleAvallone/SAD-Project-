package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import javafx.collections.ObservableList;

/**
 * Service che contiene la logica applicativa relativa alla gestione delle tracce.
 *
 * Questa classe separa la business logic dai controller JavaFX.
 * Il controller gestisce gli eventi della UI, mentre il service valida,
 * crea, aggiorna e rimuove gli oggetti del model.
 */
public class TrackService {

    /**
     * Crea una nuova traccia validando prima tutti i dati inseriti dall'utente.
     *
     * @param title titolo della traccia
     * @param author autore della traccia
     * @param length durata della traccia, nel formato mm:ss oppure in secondi
     * @param genre genere musicale
     * @param yearText anno in formato testuale
     * @return nuova traccia valida
     * @throws IllegalArgumentException se uno dei campi non è valido
     */
    public Track createTrack(String title,
                             String author,
                             String length,
                             String genre,
                             String yearText) {
        validateTextField(title, "Il titolo della traccia è obbligatorio.");
        validateTextField(author, "L'autore della traccia è obbligatorio.");
        validateTextField(length, "La durata della traccia è obbligatoria.");
        validateTextField(genre, "Il genere della traccia è obbligatorio.");
        validateTextField(yearText, "L'anno della traccia è obbligatorio.");

        int year = parseAndValidateYear(yearText);
        validateLength(length);

        return new Track(
                title.trim(),
                author.trim(),
                length.trim(),
                genre.trim(),
                year
        );
    }

    /**
     * Aggiunge una traccia al catalogo principale.
     *
     * @param tracks lista principale delle tracce
     * @param track traccia da aggiungere
     * @throws IllegalArgumentException se la lista o la traccia sono null
     */
    public void addTrack(ObservableList<Track> tracks, Track track) {
        if (tracks == null) {
            throw new IllegalArgumentException("Il catalogo delle tracce non può essere null.");
        }

        if (track == null) {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }

        tracks.add(track);
    }

    /**
     * Rimuove una traccia dal catalogo principale e da tutte le playlist.
     *
     * @param tracks lista principale delle tracce
     * @param playlists lista delle playlist esistenti
     * @param track traccia da rimuovere
     * @throws IllegalArgumentException se uno dei parametri principali è null
     */
    public void removeTrack(ObservableList<Track> tracks,
                            ObservableList<Playlist> playlists,
                            Track track) {
        if (tracks == null) {
            throw new IllegalArgumentException("Il catalogo delle tracce non può essere null.");
        }

        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
        }

        if (track == null) {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }

        tracks.remove(track);

        for (Playlist playlist : playlists) {
            if (playlist.getTracks().contains(track)) {
                playlist.removeTrack(track);
            }
        }
    }

    /**
     * Aggiorna i campi modificabili di una traccia esistente.
     *
     * La durata non viene aggiornata perché, secondo i requisiti,
     * è un dato fisso derivato dal file audio.
     *
     * @param originalTrack traccia già presente nel catalogo
     * @param editedTrack traccia contenente i nuovi valori
     * @throws IllegalArgumentException se una delle due tracce è null
     */
    public void updateEditableFields(Track originalTrack, Track editedTrack) {
        if (originalTrack == null || editedTrack == null) {
            throw new IllegalArgumentException("Le tracce non possono essere null.");
        }

        originalTrack.setTitle(editedTrack.getTitle());
        originalTrack.setAuthor(editedTrack.getAuthor());
        originalTrack.setGenre(editedTrack.getGenre());
        originalTrack.setYear(editedTrack.getYear());
    }

    /**
     * Verifica che un campo testuale sia presente.
     *
     * @param value valore da controllare
     * @param errorMessage messaggio da mostrare in caso di errore
     */
    private void validateTextField(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Converte e valida l'anno della traccia.
     *
     * @param yearText anno in formato testuale
     * @return anno numerico
     */
    private int parseAndValidateYear(String yearText) {
        int year;

        try {
            year = Integer.parseInt(yearText.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("L'anno deve essere un numero valido.");
        }

        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("L'anno deve essere compreso tra 1900 e 2100.");
        }

        return year;
    }

    /**
     * Valida la durata della traccia.
     *
     * Sono accettati due formati:
     * - secondi, ad esempio 225;
     * - minuti:secondi, ad esempio 3:45.
     *
     * @param length durata da validare
     */
    private void validateLength(String length) {
        String value = length.trim();

        if (value.matches("\\d+")) {
            int seconds = Integer.parseInt(value);

            if (seconds <= 0) {
                throw new IllegalArgumentException("La durata deve essere maggiore di zero.");
            }

            return;
        }

        if (value.matches("\\d+:[0-5]\\d")) {
            return;
        }

        throw new IllegalArgumentException("La durata deve essere nel formato mm:ss oppure in secondi.");
    }
}