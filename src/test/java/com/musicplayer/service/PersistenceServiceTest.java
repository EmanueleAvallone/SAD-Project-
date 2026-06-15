package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Tag;
import com.musicplayer.model.Track;
import com.musicplayer.persistence.AppState;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceServiceTest {

    @Test
    void exportToJsonString_shouldContainExpectedData() throws IOException {
        PersistenceService service = new PersistenceService();

        Track track1 = new Track("Numb", "Linkin Park", "3:05", "Rock", 2003);
        track1.getTags().add(Tag.FAV);

        Track track2 = new Track("Halo", "Beyonce", "4:21", "Pop", 2008);

        Playlist playlist = new Playlist("Preferite");
        playlist.getTracks().add(track1);
        playlist.getTracks().add(track2);

        String json = service.exportToJsonString(
                List.of(track1, track2),
                List.of(playlist),
                List.of(),
                List.of()
        );

        assertNotNull(json);
        assertTrue(json.contains("Numb"));
        assertTrue(json.contains("Linkin Park"));
        assertTrue(json.contains("Preferite"));
        assertTrue(json.contains("Halo"));
        assertTrue(json.contains("tracks"));
        assertTrue(json.contains("playlists"));
    }

    @Test
    void exportToFile_shouldWriteExpectedContent() throws IOException {
        PersistenceService service = new PersistenceService();

        Track track = new Track("Viva La Vida", "Coldplay", "4:02", "Pop", 2008);
        Playlist playlist = new Playlist("Daily Mix");
        playlist.getTracks().add(track);

        Path tempFile = Files.createTempFile("music-player-state", ".json");

        service.exportToFile(
                tempFile,
                List.of(track),
                List.of(playlist),
                List.of(),
                List.of()
        );

        String content = Files.readString(tempFile);

        assertTrue(content.contains("Viva La Vida"));
        assertTrue(content.contains("Coldplay"));
        assertTrue(content.contains("Daily Mix"));
    }
    @Test
    void exportToFile_shouldThrowIOException_whenWritingFails() {
        PersistenceService service = new PersistenceService() {
            @Override
            protected void writeToFile(Path outputPath, String content) throws IOException {
                throw new IOException("Scrittura fallita");
            }
        };

        Track track = new Track("Believer", "Imagine Dragons", "3:24", "Rock", 2017);

        IOException exception = assertThrows(IOException.class, () ->
                service.exportToFile(
                        Path.of("data/test.json"),
                        List.of(track),
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        assertEquals("Scrittura fallita", exception.getMessage());
    }
    @Test
    void importFromFile_shouldRestoreExactModel_whenJsonIsValid() throws Exception {
        String json = """
                {
                  "tracks": [
                    {
                      "audioFilePath": null,
                      "title": "Numb",
                      "author": "Linkin Park",
                      "length": "3:07",
                      "genre": "Rock",
                      "year": 2003,
                      "playedCount": 5,
                      "tags": ["FAV"],
                      "deletedAt": null
                    }
                  ],
                  "playlists": [
                    {
                      "name": "Preferiti",
                      "tracks": [
                        {
                          "audioFilePath": null,
                          "title": "Numb",
                          "author": "Linkin Park",
                          "length": "3:07",
                          "genre": "Rock",
                          "year": 2003,
                          "playedCount": 5,
                          "tags": ["FAV"],
                          "deletedAt": null
                        }
                      ]
                    }
                  ],
                  "deletedTracks": [],
                  "deletedPlaylists": [],
                  "savedAt": "2026-06-15T16:00:00Z"
                }
                """;

        Path tempFile = Files.createTempFile("music-state-valid", ".json");
        Files.writeString(tempFile, json);

        PersistenceService service = new PersistenceService();
        AppState state = service.importFromFile(tempFile);

        assertNotNull(state);
        assertEquals(1, state.getTracks().size());
        assertEquals(1, state.getPlaylists().size());

        Track track = state.getTracks().get(0);
        assertEquals("Numb", track.getTitle());
        assertEquals("Linkin Park", track.getAuthor());
        assertEquals("Rock", track.getGenre());
        assertEquals(2003, track.getYear());
        assertEquals(5, track.getPlayedCount());

        Playlist playlist = state.getPlaylists().get(0);
        assertEquals("Preferiti", playlist.getName());
        assertEquals(1, playlist.getTracks().size());
        assertEquals("Numb", playlist.getTracks().get(0).getTitle());
    }
}