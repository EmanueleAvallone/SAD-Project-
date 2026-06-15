package com.musicplayer.persistence;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta lo stato persistito dell'applicazione.
 *
 * Questa classe contiene tutti i dati necessari per salvare e
 * ripristinare una sessione del music player:
 * - tracce presenti nella libreria;
 * - playlist attive;
 * - tracce eliminate presenti nel cestino;
 * - playlist eliminate presenti nel cestino;
 * - timestamp dell'esportazione/salvataggio.
 *
 * È progettata per essere serializzata e deserializzata tramite Jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppState {

    private List<Track> tracks;
    private List<Playlist> playlists;
    private List<Track> deletedTracks;
    private List<Playlist> deletedPlaylists;
    private String exportedAt;

    /**
     * Costruttore vuoto richiesto da Jackson per la deserializzazione.
     */
    public AppState() {
        this.tracks = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.deletedTracks = new ArrayList<>();
        this.deletedPlaylists = new ArrayList<>();
    }

    /**
     * Costruisce uno stato completo dell'applicazione.
     *
     * Il campo {@code exportedAt} viene usato come nome canonico in serializzazione.
     * In deserializzazione viene accettato anche il nome legacy {@code savedAt},
     * per mantenere la compatibilità con eventuali file salvati in precedenza.
     *
     * @param tracks tracce presenti nella libreria
     * @param playlists playlist attive
     * @param deletedTracks tracce presenti nel cestino
     * @param deletedPlaylists playlist presenti nel cestino
     * @param exportedAt timestamp del salvataggio/esportazione
     */
    @JsonCreator
    public AppState(
            @JsonProperty("tracks") List<Track> tracks,
            @JsonProperty("playlists") List<Playlist> playlists,
            @JsonProperty("deletedTracks") List<Track> deletedTracks,
            @JsonProperty("deletedPlaylists") List<Playlist> deletedPlaylists,
            @JsonProperty("exportedAt")
            @JsonAlias({"savedAt", "exportedAt"}) String exportedAt) {

        this.tracks = tracks != null ? new ArrayList<>(tracks) : new ArrayList<>();
        this.playlists = playlists != null ? new ArrayList<>(playlists) : new ArrayList<>();
        this.deletedTracks = deletedTracks != null ? new ArrayList<>(deletedTracks) : new ArrayList<>();
        this.deletedPlaylists = deletedPlaylists != null ? new ArrayList<>(deletedPlaylists) : new ArrayList<>();
        this.exportedAt = exportedAt;
    }

    /**
     * Restituisce le tracce presenti nella libreria.
     *
     * @return lista delle tracce attive
     */
    public List<Track> getTracks() {
        return tracks;
    }

    /**
     * Imposta le tracce presenti nella libreria.
     *
     * @param tracks nuova lista delle tracce attive
     */
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks != null ? new ArrayList<>(tracks) : new ArrayList<>();
    }

    /**
     * Restituisce le playlist attive.
     *
     * @return lista delle playlist attive
     */
    public List<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     * Imposta le playlist attive.
     *
     * @param playlists nuova lista delle playlist attive
     */
    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists != null ? new ArrayList<>(playlists) : new ArrayList<>();
    }

    /**
     * Restituisce le tracce presenti nel cestino.
     *
     * @return lista delle tracce eliminate
     */
    public List<Track> getDeletedTracks() {
        return deletedTracks;
    }

    /**
     * Imposta le tracce presenti nel cestino.
     *
     * @param deletedTracks nuova lista delle tracce eliminate
     */
    public void setDeletedTracks(List<Track> deletedTracks) {
        this.deletedTracks = deletedTracks != null ? new ArrayList<>(deletedTracks) : new ArrayList<>();
    }

    /**
     * Restituisce le playlist presenti nel cestino.
     *
     * @return lista delle playlist eliminate
     */
    public List<Playlist> getDeletedPlaylists() {
        return deletedPlaylists;
    }

    /**
     * Imposta le playlist presenti nel cestino.
     *
     * @param deletedPlaylists nuova lista delle playlist eliminate
     */
    public void setDeletedPlaylists(List<Playlist> deletedPlaylists) {
        this.deletedPlaylists = deletedPlaylists != null ? new ArrayList<>(deletedPlaylists) : new ArrayList<>();
    }

    /**
     * Restituisce il timestamp dell'ultimo salvataggio/esportazione.
     *
     * @return timestamp in formato stringa
     */
    public String getExportedAt() {
        return exportedAt;
    }

    /**
     * Imposta il timestamp dell'ultimo salvataggio/esportazione.
     *
     * @param exportedAt timestamp in formato stringa
     */
    public void setExportedAt(String exportedAt) {
        this.exportedAt = exportedAt;
    }
}