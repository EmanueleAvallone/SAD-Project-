package com.musicplayer.service;

import com.musicplayer.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsabile della gestione della riproduzione simulata dei brani.
 * <p>
 * Questa classe non riproduce audio reale, ma mantiene lo stato logico
 * della riproduzione: traccia corrente, tempo corrente, durata simulata
 * e stato di riproduzione.
 * </p>
 */
public class PlaybackService {

    private Track currentTrack;
    private int currentTime;
    private int duration;
    private boolean playing;
    private List<Track> currentQueue;
    private int currentTrackIndex;

    /**
     * Crea un nuovo service di riproduzione inizialmente fermo.
     */
    public PlaybackService() {
        this.currentTrack = null;
        this.currentTime = 0;
        this.duration = 0;
        this.playing = false;
        this.currentQueue = new ArrayList<>();
        this.currentTrackIndex = -1;

    }
    /**
     * Avvia la riproduzione simulata della traccia indicata.
     *
     * @param track la traccia da riprodurre
     * @throws IllegalArgumentException se la traccia è {@code null}
     */
    public void playTrack(Track track) {
        if (track == null) {
            throw new IllegalArgumentException("La traccia non può essere null");
        }

        this.currentTrack = track;
        this.currentTime = 0;
        this.duration = parseDuration(track.getLength());
        this.playing = true;
        track.incrementPlayedCount();
    }

    /**
     * Mette in pausa la riproduzione corrente.
     * <p>
     * La traccia corrente e il tempo corrente vengono mantenuti.
     * </p>
     */
    public void pauseTrack() {
        this.playing = false;
    }

    /**
     * Ferma la riproduzione corrente e riporta il tempo all'inizio.
     */
    public void stopTrack() {
        this.playing = false;
        this.currentTime = 0;
    }

    /**
     * Resetta completamente lo stato della riproduzione simulata.
     */
    public void resetTrack() {
        this.currentTrack = null;
        this.currentTime = 0;
        this.duration = 0;
        this.playing = false;
    }

    /**
     * Restituisce la traccia attualmente selezionata.
     *
     * @return la traccia corrente, oppure {@code null} se non presente
     */
    public Track getCurrentTrack() {
        return currentTrack;
    }

    /**
     * Restituisce il tempo corrente simulato.
     *
     * @return il tempo corrente
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * Restituisce la durata simulata della traccia.
     *
     * @return la durata simulata
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Indica se una traccia è attualmente in riproduzione.
     *
     * @return {@code true} se la riproduzione è attiva, altrimenti {@code false}
     */
    public boolean isPlaying() {
        return playing;
    }
    /**
     * Avanza la riproduzione simulata di un secondo.
     * <p>
     * Il metodo incrementa il tempo corrente solo se la riproduzione è attiva
     * e se esiste una traccia corrente. Quando il tempo corrente raggiunge la
     * durata totale della traccia, la riproduzione viene fermata.
     * </p>
     *
     * <p>
     * Se non è in corso alcuna riproduzione oppure non è presente una traccia,
     * il metodo non esegue alcuna operazione.
     * </p>
     */
    public void advanceOneSecond() {
        if (!playing || currentTrack == null) {
            return;
        }

        if (currentTime < getDuration()) {
            currentTime++;
        } else {
            playing = false;
        }
    }
    /**
     * Converte una durata espressa nel formato {@code mm:ss} in secondi totali.
     * <p>
     * Se la stringa è {@code null} oppure non contiene il separatore {@code ":"},
     * il metodo restituisce {@code 0}.
     * </p>
     *
     * <p>
     * Esempio: {@code "3:21"} viene convertito in {@code 201} secondi.
     * </p>
     *
     * @param length durata della traccia nel formato {@code mm:ss}
     * @return durata totale espressa in secondi, oppure {@code 0} se il formato
     *         non è valido
     * @throws NumberFormatException se i valori di minuti o secondi non sono numerici
     */
    private int parseDuration(String length) {
        if (length == null || length.isBlank()) {
            return 0;
        }

        String value = length.trim();

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
    }
    /**
     * Riprende la riproduzione della traccia corrente dal tempo già raggiunto.
     * <p>
     * Il metodo non riporta il tempo a zero: riattiva soltanto lo stato di
     * riproduzione se esiste una traccia corrente e se il tempo corrente è ancora
     * inferiore alla durata totale.
     * </p>
     *
     * <p>
     * Questo metodo è pensato per il comportamento di "resume" dopo una pausa.
     * </p>
     */
    public void resumeTrack() {
        if (currentTrack != null && currentTime < duration) {
            this.playing = true;
        }
    }
    /**
     * Imposta la coda attualmente in riproduzione.
     *
     * @param queue lista delle tracce da considerare come coda corrente
     */
    public void setCurrentQueue(List<Track> queue) {
        if (queue == null) {
            this.currentQueue = new ArrayList<>();
            this.currentTrackIndex = -1;
            return;
        }

        this.currentQueue = new ArrayList<>(queue);

        if (currentTrack == null) {
            this.currentTrackIndex = -1;
        } else {
            this.currentTrackIndex = currentQueue.indexOf(currentTrack);
        }
    }
    /**
     * Restituisce la coda attualmente associata alla riproduzione.
     *
     * @return copia della coda corrente
     */
    public List<Track> getCurrentQueue() {
        return new ArrayList<>(currentQueue);
    }
    /**
     * Restituisce l'indice della traccia corrente nella coda.
     *
     * @return indice corrente, oppure {@code -1} se non disponibile
     */
    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }
    /**
     * Passa alla traccia successiva nella coda corrente, se disponibile.
     * <p>
     * Se esiste una traccia successiva, essa diventa la nuova traccia corrente
     * e la riproduzione riparte dal suo inizio. Se invece la traccia corrente
     * è l'ultima della coda oppure non esiste una coda valida, la riproduzione
     * viene fermata.
     * </p>
     */
    public void nextTrack() {
        if (currentQueue == null || currentQueue.isEmpty() || currentTrack == null) {
            return;
        }

        int index = currentQueue.indexOf(currentTrack);

        if (index >= 0 && index < currentQueue.size() - 1) {
            Track nextTrack = currentQueue.get(index + 1);
            this.currentTrackIndex = index + 1;
            this.currentTrack = nextTrack;
            this.currentTime = 0;
            this.duration = parseDuration(nextTrack.getLength());
            this.playing = true;
        } else {
            this.currentTrackIndex = index;
            this.playing = false;
            this.currentTime = 0;
        }
    }
    /**
     * Torna alla traccia precedente nella coda corrente oppure
     * riporta il brano corrente all'inizio.
     * <p>
     * Comportamento:
     * - se non esiste una coda valida o non c'è traccia corrente, non fa nulla;
     * - se la traccia corrente non è la prima, passa alla precedente;
     * - se la traccia corrente è la prima, resta su quella e riporta il tempo a 0.
     * </p>
     */
    public void previousTrack() {
        if (currentQueue == null || currentQueue.isEmpty() || currentTrack == null) {
            return;
        }

        int index = currentQueue.indexOf(currentTrack);

        if (currentTime > 3) {
            this.currentTime = 0;
            return;
        }

        if (index <= 0) {
            this.currentTrackIndex = index == -1 ? -1 : 0;
            this.currentTime = 0;
            if (index == 0) {
                this.currentTrack = currentQueue.get(0);
                this.duration = parseDuration(currentTrack.getLength());
            }
            return;
        }

        Track previousTrack = currentQueue.get(index - 1);
        this.currentTrackIndex = index - 1;
        this.currentTrack = previousTrack;
        this.currentTime = 0;
        this.duration = parseDuration(previousTrack.getLength());
        this.playing = true;
    }
}