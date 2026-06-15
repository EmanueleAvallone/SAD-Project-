package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.PlaylistFactory;
import com.musicplayer.model.Tag;
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
 * - rimuovere tracce da una playlist;
 * - gestire soft delete e ripristino di playlist e tracce.
 */
public class PlaylistService {

    /**
     * Posizioni originali della traccia rimossa temporaneamente dalle playlist.
     *
     * La chiave rappresenta la playlist da cui la traccia è stata rimossa,
     * mentre il valore rappresenta la posizione originale della traccia
     * all'interno di quella playlist.
     */
    private final java.util.Map<Playlist, Integer> pendingDeletedTrackPlaylistIndexes =
            new java.util.LinkedHashMap<>();

    /**
     * Playlist attualmente in attesa di conferma dell'eliminazione.
     *
     * Quando l'utente elimina una playlist, questa viene rimossa dalla lista
     * visibile, ma viene conservata temporaneamente. In questo modo può essere
     * ripristinata tramite il pulsante "Annulla" dello snackbar.
     */
    private Playlist pendingDeletedPlaylist;

    /**
     * Posizione originale della playlist rimossa temporaneamente.
     *
     * Questo indice consente di reinserire la playlist nella stessa posizione
     * in cui si trovava prima dell'eliminazione temporanea.
     */
    private int pendingDeletedPlaylistIndex = -1;

    /**
     * Crea una nuova playlist e la aggiunge alla lista principale delle playlist.
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

    /**
     * Rimuove temporaneamente una traccia da tutte le playlist in cui è presente.
     *
     * @param playlists lista principale delle playlist
     * @param track traccia da rimuovere temporaneamente dalle playlist
     * @throws IllegalArgumentException se la lista delle playlist o la traccia sono null
     */
    public void softDeleteTrackFromPlaylists(ObservableList<Playlist> playlists, Track track) {
        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
        }

        if (track == null) {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }

        pendingDeletedTrackPlaylistIndexes.clear();

        for (Playlist playlist : playlists) {
            int trackIndex = playlist.getTracks().indexOf(track);

            if (trackIndex >= 0) {
                pendingDeletedTrackPlaylistIndexes.put(playlist, trackIndex);
                playlist.removeTrack(track);
            }
        }
    }

    /**
     * Ripristina nelle playlist la traccia rimossa temporaneamente.
     *
     * @param track traccia da ripristinare nelle playlist
     * @throws IllegalArgumentException se la traccia è null
     */
    public void restorePendingDeletedTrackInPlaylists(Track track) {
        if (track == null) {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }

        for (java.util.Map.Entry<Playlist, Integer> entry : pendingDeletedTrackPlaylistIndexes.entrySet()) {
            Playlist playlist = entry.getKey();
            int originalIndex = entry.getValue();

            if (originalIndex < 0 || originalIndex > playlist.getTracks().size()) {
                originalIndex = playlist.getTracks().size();
            }

            playlist.getTracks().add(originalIndex, track);
        }

        clearPendingDeletedTrackFromPlaylists();
    }

    /**
     * Svuota la memoria temporanea relativa alle playlist coinvolte
     * nella soft delete di una traccia.
     */
    public void clearPendingDeletedTrackFromPlaylists() {
        pendingDeletedTrackPlaylistIndexes.clear();
    }

    /**
     * Rimuove temporaneamente una playlist dalla lista visibile e la inserisce
     * nel cestino delle playlist.
     * <p>
     * La playlist viene salvata anche come eliminazione pendente, così da poter
     * essere ripristinata tramite snackbar mantenendo la posizione originale.
     * </p>
     *
     * @param playlists lista principale delle playlist
     * @param deletedPlaylists lista delle playlist presenti nel cestino
     * @param playlist playlist da rimuovere temporaneamente
     * @throws IllegalArgumentException se una delle liste è null, se la playlist è null
     *                                  o se la playlist non è presente nella lista principale
     */
    public void softDeletePlaylist(ObservableList<Playlist> playlists,
                                   ObservableList<Playlist> deletedPlaylists,
                                   Playlist playlist) {
        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
        }

        if (deletedPlaylists == null) {
            throw new IllegalArgumentException("La lista del cestino playlist non può essere null.");
        }

        if (playlist == null) {
            throw new IllegalArgumentException("Seleziona una playlist da eliminare.");
        }

        int playlistIndex = playlists.indexOf(playlist);

        if (playlistIndex < 0) {
            throw new IllegalArgumentException("La playlist non è presente nella lista.");
        }

        pendingDeletedPlaylist = playlist;
        pendingDeletedPlaylistIndex = playlistIndex;

        playlists.remove(playlist);

        if (!deletedPlaylists.contains(playlist)) {
            deletedPlaylists.add(playlist);
        }
    }

    /**
     * Verifica se esiste una playlist rimossa temporaneamente e non ancora
     * confermata o annullata.
     *
     * @return true se esiste una playlist in attesa di conferma o annullamento,
     *         false altrimenti
     */
    public boolean hasPendingDeletedPlaylist() {
        return pendingDeletedPlaylist != null;
    }

    /**
     * Restituisce la playlist rimossa temporaneamente.
     *
     * @return playlist in attesa di conferma dell'eliminazione, oppure null
     */
    public Playlist getPendingDeletedPlaylist() {
        return pendingDeletedPlaylist;
    }

    /**
     * Ripristina nella lista visibile la playlist rimossa temporaneamente
     * e la rimuove dal cestino playlist.
     *
     * @param playlists lista principale delle playlist
     * @param deletedPlaylists lista delle playlist presenti nel cestino
     * @throws IllegalArgumentException se una delle liste è null
     */
    public void restorePendingDeletedPlaylist(ObservableList<Playlist> playlists,
                                              ObservableList<Playlist> deletedPlaylists) {
        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
        }

        if (deletedPlaylists == null) {
            throw new IllegalArgumentException("La lista del cestino playlist non può essere null.");
        }

        if (!hasPendingDeletedPlaylist()) {
            return;
        }

        int restoreIndex = pendingDeletedPlaylistIndex;

        if (restoreIndex < 0 || restoreIndex > playlists.size()) {
            restoreIndex = playlists.size();
        }

        playlists.add(restoreIndex, pendingDeletedPlaylist);
        deletedPlaylists.remove(pendingDeletedPlaylist);

        clearPendingDeletedPlaylist();
    }

    /**
     * Svuota lo stato temporaneo relativo all'eliminazione pendente
     * di una playlist.
     * <p>
     * La playlist resta nel cestino se l'eliminazione è stata confermata.
     * </p>
     */
    public void clearPendingDeletedPlaylist() {
        pendingDeletedPlaylist = null;
        pendingDeletedPlaylistIndex = -1;
    }

    /**
     * Genera una playlist casuale basata su un tag e la aggiunge alla lista.
     *
     * @param playlists lista osservabile delle playlist
     * @param allTracks lista di tutte le tracce della libreria
     * @param tag tag selezionato per il filtro
     * @return playlist generata
     * @throws IllegalArgumentException se non ci sono tracce con quel tag
     */
    public Playlist generatePlaylistByTag(ObservableList<Playlist> playlists,
                                          java.util.List<Track> allTracks,
                                          Tag tag) {
        if (allTracks == null || allTracks.isEmpty()) {
            throw new IllegalArgumentException("La libreria è vuota, impossibile generare la playlist.");
        }

        PlaylistFactory factory = new PlaylistFactory();

        String baseName = "Mix " + tag.name();
        String finalName = baseName;
        int counter = 1;

        while (true) {
            String checkName = finalName;
            boolean exists = playlists.stream().anyMatch(p -> p.getName().equalsIgnoreCase(checkName));
            if (!exists) {
                break;
            }
            finalName = baseName + " (" + counter + ")";
            counter++;
        }

        Playlist generatedPlaylist = factory.createPlaylistByTag(allTracks, tag, finalName);

        if (generatedPlaylist.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Non ci sono tracce con il tag " + tag.name() + ".");
        }

        playlists.add(generatedPlaylist);

        return generatedPlaylist;
    }

    /**
     * Genera una playlist filtrando per genere.
     *
     * @param playlists lista osservabile delle playlist
     * @param allTracks lista di tutte le tracce della libreria
     * @param genre genere selezionato
     * @return playlist generata
     * @throws IllegalArgumentException se la libreria è vuota o non ci sono brani del genere richiesto
     */
    public Playlist generatePlaylistByGenre(ObservableList<Playlist> playlists,
                                            java.util.List<Track> allTracks,
                                            String genre) {
        if (allTracks == null || allTracks.isEmpty()) {
            throw new IllegalArgumentException("La libreria è vuota.");
        }

        PlaylistFactory factory = new PlaylistFactory();
        String baseName = "Mix " + (genre.substring(0, 1).toUpperCase() + genre.substring(1).toLowerCase());

        String finalName = baseName;
        int counter = 1;

        while (true) {
            String checkName = finalName;
            if (playlists.stream().noneMatch(p -> p.getName().equalsIgnoreCase(checkName))) {
                break;
            }
            finalName = baseName + " (" + counter + ")";
            counter++;
        }

        Playlist generatedPlaylist = factory.createPlaylistByGenre(allTracks, genre, finalName);

        if (generatedPlaylist.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Nessun brano trovato per il genere: " + genre);
        }

        playlists.add(generatedPlaylist);
        return generatedPlaylist;
    }

    /**
     * Genera una playlist filtrando per anno.
     *
     * @param playlists lista osservabile delle playlist
     * @param allTracks lista di tutte le tracce della libreria
     * @param year anno selezionato
     * @return playlist generata
     * @throws IllegalArgumentException se la libreria è vuota o non ci sono brani per l'anno richiesto
     */
    public Playlist generatePlaylistByYear(ObservableList<Playlist> playlists,
                                           java.util.List<Track> allTracks,
                                           int year) {
        if (allTracks == null || allTracks.isEmpty()) {
            throw new IllegalArgumentException("La libreria è vuota.");
        }

        PlaylistFactory factory = new PlaylistFactory();
        String baseName = "Classics " + year;

        String finalName = baseName;
        int counter = 1;

        while (true) {
            String checkName = finalName;
            if (playlists.stream().noneMatch(p -> p.getName().equalsIgnoreCase(checkName))) {
                break;
            }
            finalName = baseName + " (" + counter + ")";
            counter++;
        }

        Playlist generatedPlaylist = factory.createPlaylistByYear(allTracks, year, finalName);

        if (generatedPlaylist.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Nessun brano trovato per l'anno: " + year);
        }

        playlists.add(generatedPlaylist);
        return generatedPlaylist;
    }

    /**
     * Sposta una traccia all'interno della playlist selezionata.
     *
     * @param playlist playlist in cui effettuare lo spostamento
     * @param currentIndex posizione attuale della traccia
     * @param targetIndex nuova posizione desiderata
     * @throws IllegalArgumentException se la playlist è nulla
     * @throws IndexOutOfBoundsException se gli indici sono fuori dal range della playlist
     */
    public void moveTrackInPlaylist(Playlist playlist, int currentIndex, int targetIndex) {
        if (playlist == null) {
            throw new IllegalArgumentException("Nessuna playlist selezionata per lo spostamento.");
        }

        int size = playlist.getTracks().size();

        if (currentIndex < 0 || currentIndex >= size) {
            throw new IndexOutOfBoundsException(
                    "Impossibile spostare: l'indice di partenza " + currentIndex + " è fuori limite."
            );
        }

        if (targetIndex < 0 || targetIndex >= size) {
            throw new IndexOutOfBoundsException(
                    "Impossibile spostare: l'indice di destinazione " + targetIndex + " è fuori limite."
            );
        }

        playlist.moveTrack(currentIndex, targetIndex);
    }

    /**
     * Aggiorna le playlist dinamiche se la traccia modificata ha cambiato i suoi tag.
     *
     * @param playlists lista delle playlist
     * @param updatedTrack traccia aggiornata
     */
    public void refreshSmartPlaylists(ObservableList<Playlist> playlists, Track updatedTrack) {
        if (playlists == null || updatedTrack == null) {
            return;
        }

        for (Playlist playlist : playlists) {
            com.musicplayer.model.filter.TrackFilterStrategy strategy = playlist.getFilterStrategy();

            if (strategy != null) {
                boolean matches = strategy.matches(updatedTrack);
                boolean contains = playlist.getTracks().contains(updatedTrack);

                if (matches && !contains) {
                    playlist.addTrack(updatedTrack);
                } else if (!matches && contains) {
                    playlist.removeTrack(updatedTrack);
                }
            }
        }
    }
}