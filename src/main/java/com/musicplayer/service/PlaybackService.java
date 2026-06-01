package com.musicplayer.service;

import com.musicplayer.model.Track;

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

    /**
     * Crea un nuovo service di riproduzione inizialmente fermo.
     */
    public PlaybackService() {
        this.currentTrack = null;
        this.currentTime = 0;
        this.duration = 0;
        this.playing = false;
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
    private int parseDuration(String length) {
        if (length == null || !length.contains(":")) {
            return 0;
        }

        String[] parts = length.split(":");
        int minutes = Integer.parseInt(parts[0].trim());
        int seconds = Integer.parseInt(parts[1].trim());

        return minutes * 60 + seconds;
    }
}