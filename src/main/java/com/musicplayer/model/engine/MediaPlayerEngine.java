package com.musicplayer.model.engine;

import com.musicplayer.model.PlayMode;
import com.musicplayer.model.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce lo stato interno del player musicale.
 * <p>
 * La classe si occupa di controllare le azioni principali del player,
 * come riproduzione, pausa e stop, utilizzando il pattern State.
 * Inoltre mantiene informazioni sulla playlist corrente, sull'indice
 * della traccia attuale e sulla modalità di riproduzione.
 * </p>
 * <p>
 * Implementa {@link PlayerObservable}, permettendo a più observer
 * di essere notificati quando cambia lo stato interno del player.
 * </p>
 */

public class MediaPlayerEngine implements PlayerObservable {
    private PlayerState currentState;
    private PlayStrategy currentPlayMode;
    private List<PlayerObserver> observers;
    private Playlist currentPlaylist;
    private int currentIndex;

    /**
     * Crea un nuovo player inizializzato nello stato {@code STOPPED}.
     * <p>
     * All'avvio non è presente alcuna playlist selezionata, l'indice
     * corrente è impostato a {@code 0} e la modalità di riproduzione
     * predefinita è sequenziale.
     * </p>
     */

    public MediaPlayerEngine() {
        this.currentState = new StoppedState();
        this.observers = new ArrayList<>();
        this.currentPlaylist = null;
        this.currentIndex = 0;
        this.currentPlayMode = new SequentialPlay();
    }

    /**
     * Imposta un nuovo stato del player e notifica gli observer registrati.
     *
     * @param newState il nuovo stato da assegnare al player
     */

    public void setState(PlayerState newState) {
        this.currentState = newState;
        notifyObservers();
    }

    /**
     * Restituisce lo stato corrente del player.
     *
     * @return lo stato corrente del player
     */

    public PlayerState getCurrentState() {
        return currentState;
    }

    /**
     * Restituisce il nome dello stato corrente del player.
     *
     * @return il nome dello stato corrente, ad esempio {@code STOPPED},
     * {@code PLAYING} o {@code PAUSED}
     */

    public String getCurrentStateName() {
        return currentState.getName();
    }

    /**
     * Avvia o riprende la riproduzione in base allo stato corrente.
     * <p>
     * Il comportamento effettivo dipende dall'implementazione dello stato
     * corrente.
     * </p>
     */

    public void play() {
        currentState.play(this);
    }

    /**
     * Mette in pausa la riproduzione in base allo stato corrente.
     * <p>
     * Se il player si trova in uno stato in cui la pausa non è valida,
     * lo stato rimane invariato.
     * </p>
     */

    public void pause() {
        currentState.pause(this);
    }

    /**
     * Ferma la riproduzione in base allo stato corrente.
     * <p>
     * Se il player è già fermo, lo stato rimane invariato.
     * </p>
     */

    public void stop() {
        currentState.stop(this);
    }

    /**
     * Restituisce la playlist attualmente selezionata.
     *
     * @return la playlist corrente, oppure {@code null} se nessuna playlist
     * è selezionata
     */

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    /**
     * Imposta la playlist corrente.
     * <p>
     * Quando viene impostata una nuova playlist, l'indice della traccia
     * corrente viene riportato a {@code 0} e gli observer vengono notificati.
     * </p>
     *
     * @param currentPlaylist la playlist da impostare come corrente
     */

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
        this.currentIndex = 0;
        notifyObservers();
    }

    /**
     * Restituisce l'indice della traccia corrente nella playlist.
     *
     * @return l'indice della traccia corrente
     */

    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Imposta l'indice della traccia corrente e notifica gli observer.
     *
     * @param currentIndex il nuovo indice della traccia corrente
     */

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        notifyObservers();
    }

    /**
     * Restituisce la modalità di riproduzione corrente.
     *
     * @return la strategia di riproduzione attualmente impostata
     */

    public PlayStrategy getCurrentPlayMode() {
        return currentPlayMode;
    }

    /**
     * Imposta la modalità di riproduzione corrente e notifica gli observer.
     *
     * @param currentPlayMode la nuova strategia di riproduzione
     */

    public void setCurrentPlayMode(PlayStrategy currentPlayMode) {
        this.currentPlayMode = currentPlayMode;
        notifyObservers();
    }

    /**
     * Aggiunge un observer alla lista degli oggetti da notificare.
     *
     * @param observer l'observer da registrare
     */

    @Override
    public void addObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    /**
     * Rimuove un observer dalla lista degli oggetti registrati.
     *
     * @param observer l'observer da rimuovere
     */

    @Override
    public void removeObserver(PlayerObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica tutti gli observer registrati passando l'istanza corrente
     * del player.
     */

    public void notifyObservers() {
        for (PlayerObserver observer : observers) {
            observer.update(this);
        }
    }
}
