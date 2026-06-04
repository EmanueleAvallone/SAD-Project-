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
     * Posizioni originali della traccia rimossa temporaneamente dalle playlist.
     *
     * La chiave rappresenta la playlist da cui la traccia è stata rimossa,
     * mentre il valore rappresenta la posizione originale della traccia
     * all'interno di quella playlist.
     */
    private final java.util.Map<Playlist, Integer> pendingDeletedTrackPlaylistIndexes = new java.util.LinkedHashMap<>();

    /**
     * Playlist attualmente in attesa di conferma dell'eliminazione.
     *
     * Quando l'utente elimina una playlist, questa viene rimossa dalla lista
     * visibile, ma viene conservata temporaneamente. In questo modo può essere
     * ripristinata tramite il pulsante "Annulla" dello snackbar.
     *
     * Poiché viene conservato l'oggetto Playlist completo, vengono mantenute
     * anche tutte le tracce contenute al suo interno.
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

    /**
     * Rimuove temporaneamente una traccia da tutte le playlist in cui è presente.
     * <p>
     * Questo metodo viene usato insieme alla soft delete della Track Library:
     * quando una traccia viene rimossa temporaneamente dal catalogo principale,
     * deve sparire anche dalle playlist visualizzate nell'interfaccia.
     * </p>
     * <p>
     * La traccia non viene persa definitivamente: per ogni playlist in cui era
     * presente viene salvata la posizione originale, così da poterla reinserire
     * correttamente se l'utente preme "Annulla" nello snackbar.
     * </p>
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
     * <p>
     * Se la traccia era presente in una o più playlist prima della soft delete,
     * viene reinserita nelle stesse playlist e nella stessa posizione originale.
     * </p>
     * <p>
     * Dopo il ripristino, la memoria temporanea delle posizioni viene svuotata,
     * perché non esiste più alcuna eliminazione pendente da annullare.
     * </p>
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
     * Rimuove temporaneamente una playlist dalla lista visibile.
     * <p>
     * Questo metodo realizza la logica di soft delete per le playlist:
     * la playlist viene rimossa dalla lista osservabile mostrata
     * nell'interfaccia, quindi sparisce dalla sidebar, ma non viene ancora
     * cancellata definitivamente dal sistema.
     * </p>
     * <p>
     * La playlist viene conservata interamente in memoria temporanea insieme
     * alla sua posizione originale. In questo modo, se l'utente preme
     * "Annulla", la playlist può essere ripristinata nello stesso punto e
     * con tutte le tracce che conteneva prima dell'eliminazione.
     * </p>
     *
     * @param playlists lista principale delle playlist
     * @param playlist playlist da rimuovere temporaneamente
     * @throws IllegalArgumentException se la lista o la playlist sono null
     * @throws IllegalArgumentException se la playlist non è presente nella lista
     */
    public void softDeletePlaylist(ObservableList<Playlist> playlists, Playlist playlist) {
        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
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
     * Ripristina nella lista visibile la playlist rimossa temporaneamente.
     * <p>
     * Se esiste una playlist in attesa di conferma dell'eliminazione, questa
     * viene reinserita nella lista principale nella posizione in cui si trovava
     * prima della rimozione temporanea.
     * </p>
     * <p>
     * La playlist viene ripristinata come oggetto completo, quindi conserva
     * anche tutte le tracce che erano state aggiunte al suo interno.
     * Dopo il ripristino, lo stato temporaneo viene svuotato.
     * </p>
     *
     * @param playlists lista principale delle playlist
     * @throws IllegalArgumentException se la lista è null
     */
    public void restorePendingDeletedPlaylist(ObservableList<Playlist> playlists) {
        if (playlists == null) {
            throw new IllegalArgumentException("La lista delle playlist non può essere null.");
        }

        if (!hasPendingDeletedPlaylist()) {
            return;
        }

        int restoreIndex = pendingDeletedPlaylistIndex;

        if (restoreIndex < 0 || restoreIndex > playlists.size()) {
            restoreIndex = playlists.size();
        }

        playlists.add(restoreIndex, pendingDeletedPlaylist);

        clearPendingDeletedPlaylist();
    }

    /**
     * Svuota lo stato temporaneo relativo all'eliminazione pendente
     * di una playlist.
     * <p>
     * Questo metodo viene usato quando l'eliminazione viene annullata
     * oppure quando diventa definitiva alla scadenza dello snackbar.
     * </p>
     */
    public void clearPendingDeletedPlaylist() {
        pendingDeletedPlaylist = null;
        pendingDeletedPlaylistIndex = -1;
    }
}