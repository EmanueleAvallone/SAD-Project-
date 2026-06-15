package com.musicplayer.persistence;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;

import java.util.List;

/**
 * Rappresenta lo stato completo dell'applicazione da serializzare su disco.
 * <p>
 * Questa classe funge da contenitore dati per l'esportazione della libreria,
 * delle playlist, degli elementi eliminati e del timestamp di esportazione.
 * </p>
 */
public class AppState {
    private List<Track> tracks;
    private List<Playlist> playlists;
    private List<Track> deletedTracks;
    private List<Playlist> deletedPlaylists;
    private String exportedAt;

    /**
     * Costruttore vuoto richiesto dalla serializzazione/deserializzazione.
     */
    public AppState() {
    }

    /**
     * Crea un nuovo stato dell'applicazione.
     *
     * @param tracks lista delle tracce presenti nella libreria
     * @param playlists lista delle playlist presenti nella libreria
     * @param deletedTracks lista delle tracce eliminate
     * @param deletedPlaylists lista delle playlist eliminate
     * @param exportedAt timestamp di esportazione in formato testuale
     */
    public AppState(List<Track> tracks,
                    List<Playlist> playlists,
                    List<Track> deletedTracks,
                    List<Playlist> deletedPlaylists,
                    String exportedAt) {
        this.tracks = tracks;
        this.playlists = playlists;
        this.deletedTracks = deletedTracks;
        this.deletedPlaylists = deletedPlaylists;
        this.exportedAt = exportedAt;
    }

    /**
     * Restituisce le tracce presenti nella libreria.
     *
     * @return lista delle tracce
     */
    public List<Track> getTracks() {
        return tracks;
    }

    /**
     * Imposta le tracce presenti nella libreria.
     *
     * @param tracks lista delle tracce
     */
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    /**
     * Restituisce le playlist presenti nella libreria.
     *
     * @return lista delle playlist
     */
    public List<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     * Imposta le playlist presenti nella libreria.
     *
     * @param playlists lista delle playlist
     */
    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    /**
     * Restituisce le tracce eliminate.
     *
     * @return lista delle tracce eliminate
     */
    public List<Track> getDeletedTracks() {
        return deletedTracks;
    }

    /**
     * Imposta le tracce eliminate.
     *
     * @param deletedTracks lista delle tracce eliminate
     */
    public void setDeletedTracks(List<Track> deletedTracks) {
        this.deletedTracks = deletedTracks;
    }

    /**
     * Restituisce le playlist eliminate.
     *
     * @return lista delle playlist eliminate
     */
    public List<Playlist> getDeletedPlaylists() {
        return deletedPlaylists;
    }

    /**
     * Imposta le playlist eliminate.
     *
     * @param deletedPlaylists lista delle playlist eliminate
     */
    public void setDeletedPlaylists(List<Playlist> deletedPlaylists) {
        this.deletedPlaylists = deletedPlaylists;
    }

    /**
     * Restituisce il timestamp di esportazione.
     *
     * @return data e ora di esportazione in formato testuale
     */
    public String getExportedAt() {
        return exportedAt;
    }

    /**
     * Imposta il timestamp di esportazione.
     *
     * @param exportedAt data e ora di esportazione in formato testuale
     */
    public void setExportedAt(String exportedAt) {
        this.exportedAt = exportedAt;
    }
}