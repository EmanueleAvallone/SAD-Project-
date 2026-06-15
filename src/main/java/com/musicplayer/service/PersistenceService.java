package com.musicplayer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import com.musicplayer.persistence.AppState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Gestisce la persistenza dello stato dell'applicazione su disco.
 * <p>
 * Il servizio si occupa di esportare in formato JSON l'intero stato della
 * libreria, incluse tracce, playlist, elementi eliminati e informazioni
 * di esportazione.
 * </p>
 */
public class PersistenceService {

    private final ObjectMapper objectMapper;
    /**
     * Percorso locale predefinito usato per esportare lo stato della libreria.
     * <p>
     * Il file viene salvato nella cartella {@code data} del progetto con nome
     * {@code music-player-state.json}.
     * </p>
     */
    private static final Path DEFAULT_EXPORT_PATH =
            Path.of("data", "music-player-state.json");
    /**
     * Crea una nuova istanza del servizio di persistenza.
     * <p>
     * L'oggetto {@link ObjectMapper} viene configurato per produrre JSON
     * leggibile con indentazione.
     * </p>
     */
    public PersistenceService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Serializza lo stato della libreria in una stringa JSON.
     * <p>
     * Se uno dei parametri in ingresso è {@code null}, viene sostituito con
     * una lista vuota per evitare errori di serializzazione.
     * </p>
     *
     * @param tracks lista delle tracce presenti nella libreria
     * @param playlists lista delle playlist presenti nella libreria
     * @param deletedTracks lista delle tracce eliminate
     * @param deletedPlaylists lista delle playlist eliminate
     * @return rappresentazione JSON dello stato completo dell'applicazione
     * @throws IOException se si verifica un errore durante la serializzazione
     */
    public String exportToJsonString(List<Track> tracks,
                                     List<Playlist> playlists,
                                     List<Track> deletedTracks,
                                     List<Playlist> deletedPlaylists) throws IOException {

        AppState state = new AppState(
                tracks == null ? List.of() : List.copyOf(tracks),
                playlists == null ? List.of() : List.copyOf(playlists),
                deletedTracks == null ? List.of() : List.copyOf(deletedTracks),
                deletedPlaylists == null ? List.of() : List.copyOf(deletedPlaylists),
                OffsetDateTime.now().toString()
        );

        return objectMapper.writeValueAsString(state);
    }

    /**
     * Scrive il contenuto fornito su file.
     * <p>
     * Questo metodo è separato per facilitare i test unitari e consentire
     * la simulazione di errori di scrittura.
     * </p>
     *
     * @param outputPath percorso del file di destinazione
     * @param content contenuto da scrivere
     * @throws IOException se la scrittura su disco fallisce
     */
    protected void writeToFile(Path outputPath, String content) throws IOException {
        Files.writeString(outputPath, content);
    }

    /**
     * Esporta lo stato corrente della libreria nel file locale predefinito.
     * <p>
     * Questo metodo usa il percorso definito in {@link #DEFAULT_EXPORT_PATH}
     * e delega l'effettiva scrittura a {@link #exportToFile(Path, List, List, List, List)}.
     * </p>
     *
     * @param tracks lista delle tracce presenti nella libreria
     * @param playlists lista delle playlist presenti nella libreria
     * @param deletedTracks lista delle tracce eliminate
     * @param deletedPlaylists lista delle playlist eliminate
     * @throws IOException se si verifica un errore durante la serializzazione
     *                     o la scrittura su disco
     */
    public void exportToDefaultFile(List<Track> tracks,
                                    List<Playlist> playlists,
                                    List<Track> deletedTracks,
                                    List<Playlist> deletedPlaylists) throws IOException {
        exportToFile(DEFAULT_EXPORT_PATH, tracks, playlists, deletedTracks, deletedPlaylists);
    }
    /**
     * Esporta lo stato della libreria su file in formato JSON.
     * <p>
     * Il metodo crea eventuali directory mancanti nel percorso di destinazione
     * e poi scrive il file risultante sul disco.
     * </p>
     *
     * @param outputPath percorso del file di esportazione
     * @param tracks lista delle tracce presenti nella libreria
     * @param playlists lista delle playlist presenti nella libreria
     * @param deletedTracks lista delle tracce eliminate
     * @param deletedPlaylists lista delle playlist eliminate
     * @throws IOException se si verifica un errore durante la creazione delle directory
     *                     o durante la scrittura del file
     * @throws IllegalArgumentException se {@code outputPath} è {@code null}
     */
    public void exportToFile(Path outputPath,
                             List<Track> tracks,
                             List<Playlist> playlists,
                             List<Track> deletedTracks,
                             List<Playlist> deletedPlaylists) throws IOException {

        if (outputPath == null) {
            throw new IllegalArgumentException("Il percorso di output non può essere null");
        }

        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        String json = exportToJsonString(tracks, playlists, deletedTracks, deletedPlaylists);
        writeToFile(outputPath, json);
    }
}