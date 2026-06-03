package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import javafx.collections.ObservableList;

/**
 * Service che contiene la logica applicativa relativa alla gestione delle playlist.
 *
 * Questa classe separa la business logic dai controller JavaFX.
 * Il controller deve occuparsi della UI, mentre il service valida ed esegue
 * le operazioni sul model.
 *
 * Responsabilità principali:
 * - creare playlist;
 * - rinominare playlist;
 * - eliminare playlist;
 * - aggiungere tracce a una playlist;
 * - rimuovere tracce da una playlist.
 */
public class PlaylistService {

    /**
     * Crea una nuova playlist e la aggiunge alla lista principale delle playlist.
     *
     * Questo metodo centralizza la validazione del nome della playlist,
     * evitando che il controllo venga duplicato nel controller.
     *
     * @param playlists lista principale delle playlist
     * @param name nome della nuova playlist
     * @return la playlist creata
     * @throws IllegalArgumentException se la lista è null, se il nome è vuoto
     *                                  o se esiste già una playlist con lo stesso nome
     */
    public Playlist createPlaylist(ObservableList<Playlist> playlists, String name) {
        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della playlist non può essere vuoto.");
        }

        String normalizedName = name.trim();

        boolean alreadyExists = playlists.stream()
                .anyMatch(playlist -> playlist.getName().equalsIgnoreCase(normalizedName));

        if (alreadyExists) {
            throw new IllegalArgumentException("Esiste già una playlist con questo nome.");
        }

        Playlist playlist = new Playlist(normalizedName);
        playlists.add(playlist);

        return playlist;
    }

    /**
     * Rinomina una playlist esistente.
     *
     * La validazione viene mantenuta nel service per non sovraccaricare il controller.
     * Non è consentito rinominare una playlist con nome vuoto o con un nome già usato
     * da un'altra playlist.
     *
     * @param playlists lista principale delle playlist
     * @param playlist playlist da rinominare
     * @param newName nuovo nome della playlist
     * @throws IllegalArgumentException se la lista è null, se la playlist non è selezionata,
     *                                  se il nome è vuoto o se il nome è duplicato
     */
    public void renamePlaylist(ObservableList<Playlist> playlists,
                               Playlist playlist,
                               String newName) {
        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
        }

        if (playlist == null) {
            throw new IllegalArgumentException("Seleziona una playlist da rinominare.");
        }

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nuovo nome della playlist non può essere vuoto.");
        }

        String normalizedName = newName.trim();

        boolean alreadyExists = playlists.stream()
                .anyMatch(existingPlaylist ->
                        existingPlaylist != playlist
                                && existingPlaylist.getName().equalsIgnoreCase(normalizedName)
                );

        if (alreadyExists) {
            throw new IllegalArgumentException("Esiste già una playlist con questo nome.");
        }

        playlist.rename(normalizedName);
    }

    /**
     * Elimina una playlist dalla lista principale.
     *
     * @param playlists lista principale delle playlist
     * @param playlist playlist da eliminare
     * @throws IllegalArgumentException se la lista è null o se nessuna playlist è selezionata
     */
    public void deletePlaylist(ObservableList<Playlist> playlists, Playlist playlist) {
        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
        }

        if (playlist == null) {
            throw new IllegalArgumentException("Seleziona una playlist da eliminare.");
        }

        playlists.remove(playlist);
    }

    /**
     * Valida la selezione necessaria per aggiungere una traccia a una playlist.
     *
     * @param playlist playlist selezionata
     * @param track traccia selezionata dalla libreria principale
     * @throws IllegalArgumentException se playlist o traccia non sono selezionate
     */
    public void validateTrackAdditionSelection(Playlist playlist, Track track) {
        if (playlist == null) {
            throw new IllegalArgumentException("Nessuna playlist selezionata.");
        }

        if (track == null) {
            throw new IllegalArgumentException("Nessuna traccia selezionata.");
        }
    }

    /**
     * Aggiunge una traccia alla playlist selezionata.
     *
     * @param playlist playlist di destinazione
     * @param track traccia da aggiungere
     * @throws IllegalArgumentException se playlist o traccia non sono selezionate
     */
    public void addTrackToPlaylist(Playlist playlist, Track track) {
        validateTrackAdditionSelection(playlist, track);
        playlist.addTrack(track);
    }

    /**
     * Valida la selezione necessaria per rimuovere una traccia da una playlist.
     *
     * @param playlist playlist selezionata
     * @param track traccia selezionata nella playlist
     * @throws IllegalArgumentException se playlist o traccia non sono selezionate
     */
    public void validateTrackRemovalSelection(Playlist playlist, Track track) {
        if (playlist == null) {
            throw new IllegalArgumentException("Nessuna playlist selezionata.");
        }

        if (track == null) {
            throw new IllegalArgumentException("Nessuna traccia selezionata nella playlist.");
        }
    }

    /**
     * Rimuove una traccia dalla playlist selezionata.
     *
     * @param playlist playlist da cui rimuovere la traccia
     * @param track traccia da rimuovere
     * @throws IllegalArgumentException se playlist o traccia non sono selezionate
     */
    public void removeTrackFromPlaylist(Playlist playlist, Track track) {
        validateTrackRemovalSelection(playlist, track);
        playlist.removeTrack(track);
    }

}