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
 *
 * In particolare, il service supporta anche una logica di eliminazione
 * temporanea: una traccia può essere rimossa dalla lista visibile senza essere
 * cancellata immediatamente dal sistema, così da permettere all'utente di
 * annullare l'operazione entro un breve intervallo di tempo.
 */
public class TrackService {
    /**
     * Traccia attualmente in attesa di conferma dell'eliminazione.
     *
     * Quando l'utente elimina una traccia, questa viene rimossa dalla lista
     * visibile, ma viene conservata temporaneamente in questo campo. In questo
     * modo l'applicazione può ancora ripristinarla se l'utente preme "Annulla"
     * prima della scadenza dello snackbar.
     */
    private Track pendingDeletedTrack;

    /**
     * Posizione originale della traccia rimossa temporaneamente.
     *
     * Questo indice consente di reinserire la traccia nella stessa posizione
     * in cui si trovava prima dell'eliminazione temporanea.
     */
    private int pendingDeletedTrackIndex = -1;

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
                             String yearText,
                             String audioFilePath) {
        validateTextField(title, "The track title is required.");
        validateTextField(author, "The author of the track is required.");
        validateTextField(length, "The duration of the track is mandatory.");
        validateTextField(genre, "The genre of the track is mandatory.");
        validateTextField(yearText, "The track year is mandatory.");

        int year = parseAndValidateYear(yearText);
        validateLength(length);
        validateAudioFilePath(audioFilePath);
        return new Track(
                title.trim(),
                author.trim(),
                length.trim(),
                genre.trim(),
                year,
                normalizeAudioFilePath(audioFilePath)
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
     * Sposta una traccia nel cestino (Soft Delete di dominio).
     * Rimuove la traccia dal catalogo principale e da tutte le playlist in cui è presente,
     * registra il momento esatto dell'eliminazione e la inserisce nella collezione del cestino.
     *
     * @param tracks    Lista principale delle tracce
     * @param playlists Lista di tutte le playlist
     * @param trashList Lista delle tracce nel cestino
     * @param track     Traccia da spostare nel cestino
     * @throws IllegalArgumentException se uno dei parametri è null
     */
    public void moveToTrash(ObservableList<Track> tracks,
                            ObservableList<Playlist> playlists,
                            ObservableList<Track> trashList,
                            Track track) {
        if (tracks == null || playlists == null || trashList == null || track == null) {
            throw new IllegalArgumentException("I parametri per lo spostamento nel cestino non possono essere null.");
        }

        tracks.remove(track);

        for (Playlist playlist : playlists) {
            if (playlist.getTracks().contains(track)) {
                playlist.removeTrack(track);
            }
        }

        track.setDeletedAt(java.time.LocalDateTime.now());

        trashList.add(track);
    }

    /**
     * Rimuove temporaneamente una traccia dal catalogo visibile.
     * <p>
     * Questo metodo realizza la logica di soft delete: la traccia viene rimossa
     * dalla lista osservabile mostrata nell'interfaccia, quindi scompare dalla
     * tabella, ma non viene ancora cancellata definitivamente dal sistema.
     * </p>
     * <p>
     * La traccia e la sua posizione originale vengono salvate temporaneamente,
     * così da poter essere recuperate se l'utente sceglie di annullare
     * l'eliminazione tramite lo snackbar.
     * </p>
     *
     * @param tracks catalogo visibile delle tracce
     * @param track traccia da rimuovere temporaneamente
     * @throws IllegalArgumentException se il catalogo è null
     * @throws IllegalArgumentException se la traccia è null
     * @throws IllegalArgumentException se la traccia non è presente nel catalogo
     */
    public void softDeleteTrack(ObservableList<Track> tracks, Track track) {
        if (tracks == null) {
            throw new IllegalArgumentException("Il catalogo delle tracce non può essere null.");
        }

        if (track == null) {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }

        int trackIndex = tracks.indexOf(track);

        if (trackIndex < 0) {
            throw new IllegalArgumentException("La traccia non è presente nella libreria.");
        }

        pendingDeletedTrack = track;
        pendingDeletedTrackIndex = trackIndex;

        tracks.remove(track);
    }

    /**
     * Verifica se esiste una traccia rimossa temporaneamente e non ancora
     * confermata o annullata.
     *
     * @return true se esiste una rimozione temporanea pendente, false altrimenti
     */
    public boolean hasPendingDeletedTrack() {
        return pendingDeletedTrack != null;
    }

    /**
     * Restituisce la traccia rimossa temporaneamente.
     *
     * @return traccia in attesa di conferma dell'eliminazione, oppure null
     */
    public Track getPendingDeletedTrack() {
        return pendingDeletedTrack;
    }

    /**
     * Restituisce la posizione originale della traccia rimossa temporaneamente.
     *
     * @return indice originale della traccia, oppure -1 se non esiste una
     *         rimozione temporanea pendente
     */
    public int getPendingDeletedTrackIndex() {
        return pendingDeletedTrackIndex;
    }

    /**
     * Svuota lo stato temporaneo relativo all'eliminazione pendente.
     * <p>
     * Questo metodo verrà usato quando l'eliminazione viene annullata
     * oppure quando diventa definitiva.
     * </p>
     */
    public void clearPendingDeletedTrack() {
        pendingDeletedTrack = null;
        pendingDeletedTrackIndex = -1;
    }

    /**
     * Ripristina nel catalogo visibile la traccia rimossa temporaneamente.
     * <p>
     * Se esiste una traccia in attesa di conferma dell'eliminazione, questa viene
     * reinserita nella lista principale nella posizione in cui si trovava prima
     * della rimozione temporanea.
     * </p>
     * <p>
     * Dopo il ripristino, lo stato temporaneo dell'eliminazione viene svuotato,
     * perché non esiste più alcuna operazione pendente da confermare o annullare.
     * </p>
     *
     * @param tracks catalogo visibile delle tracce
     * @throws IllegalArgumentException se il catalogo è null
     */
    public void restorePendingDeletedTrack(ObservableList<Track> tracks) {
        if (tracks == null) {
            throw new IllegalArgumentException("Il catalogo delle tracce non può essere null.");
        }

        if (!hasPendingDeletedTrack()) {
            return;
        }

        int restoreIndex = pendingDeletedTrackIndex;

        if (restoreIndex < 0 || restoreIndex > tracks.size()) {
            restoreIndex = tracks.size();
        }

        tracks.add(restoreIndex, pendingDeletedTrack);
        clearPendingDeletedTrack();
    }

    /**
     * Aggiorna i campi modificabili di una traccia esistente, inclusi i Tag.
     */
    public void updateEditableFields(Track originalTrack, Track editedTrack) {
        if (originalTrack == null || editedTrack == null) {
            throw new IllegalArgumentException("Le tracce non possono essere null.");
        }

        originalTrack.setTitle(editedTrack.getTitle());
        originalTrack.setAuthor(editedTrack.getAuthor());
        originalTrack.setGenre(editedTrack.getGenre());
        originalTrack.setYear(editedTrack.getYear());
        originalTrack.setAudioFilePath(editedTrack.getAudioFilePath());
        if (editedTrack.getTags() != null) {
            originalTrack.setTags(new java.util.HashSet<>(editedTrack.getTags()));
        } else {
            originalTrack.getTags().clear();
        }
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

    /**
     * Estrae la classifica delle tracce più ascoltate.
     * Ordina le tracce in base al playedCount in modo decrescente e restituisce
     * solo le prime "limit" tracce. Esclude le tracce mai ascoltate (playedCount == 0).
     *
     * @param tracks la lista principale delle tracce
     * @param limit numero massimo di tracce da restituire (es. 10)
     * @return lista delle tracce più ascoltate
     */
    public java.util.List<Track> getTopPlayedTracks(ObservableList<Track> tracks, int limit) {
        if (tracks == null) {
            throw new IllegalArgumentException("Il catalogo delle tracce non può essere null.");
        }

        return tracks.stream()
                .filter(track -> track.getPlayedCount() > 0)
                .sorted((t1, t2) -> Integer.compare(t2.getPlayedCount(), t1.getPlayedCount()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Ripristina una traccia dal cestino alla libreria principale.
     * Rimuove la traccia dalla collezione del cestino, azzera il timestamp
     * di eliminazione e la reinserisce nel catalogo visibile.
     *
     * @param tracks    Lista principale delle tracce
     * @param trashList Lista delle tracce nel cestino
     * @param track     Traccia da ripristinare
     * @throws IllegalArgumentException se uno dei parametri è null o se la traccia non è nel cestino
     */
    public void restoreFromTrash(ObservableList<Track> tracks,
                                 ObservableList<Track> trashList,
                                 Track track) {
        if (tracks == null || trashList == null || track == null) {
            throw new IllegalArgumentException("The restore parameters cannot be null.");
        }

        if (!trashList.contains(track)) {
            throw new IllegalArgumentException("The selected track is not in the recycle bin.");
        }

        trashList.remove(track);

        track.setDeletedAt(null);

        tracks.add(track);
    }
    private void validateAudioFilePath(String audioFilePath) {
        if (audioFilePath == null || audioFilePath.trim().isEmpty()) {
            return;
        }

        java.io.File file = new java.io.File(audioFilePath.trim());

        if (!file.exists()) {
            throw new IllegalArgumentException("The selected audio file does not exist.");
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("The selected path is not a valid file.");
        }

        String lowerCaseName = file.getName().toLowerCase();

        if (!lowerCaseName.endsWith(".mp3")) {
            throw new IllegalArgumentException("Only audio files in .mp3 format are supported.");
        }
    }

    private String normalizeAudioFilePath(String audioFilePath) {
        if (audioFilePath == null || audioFilePath.trim().isEmpty()) {
            return null;
        }

        return audioFilePath.trim();
    }
}