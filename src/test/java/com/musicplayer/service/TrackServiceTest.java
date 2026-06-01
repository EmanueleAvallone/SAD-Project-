package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrackServiceTest {

    private final TrackService trackService = new TrackService();

    @Test
    void addTrackShouldAddTrackToCatalog() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        trackService.addTrack(tracks, track);

        assertEquals(1, tracks.size());
        assertEquals(track, tracks.get(0));
    }

    @Test
    void addTrackShouldRejectNullTrack() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        assertThrows(IllegalArgumentException.class, () ->
                trackService.addTrack(tracks, null)
        );
    }

    @Test
    void removeTrackShouldRemoveTrackFromCatalog() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);
        tracks.add(track);

        trackService.removeTrack(tracks, playlists, track);

        assertTrue(tracks.isEmpty());
    }

    @Test
    void removeTrackShouldRemoveTrackFromAllPlaylists() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        Playlist playlistOne = new Playlist("Playlist one");
        Playlist playlistTwo = new Playlist("Playlist two");

        playlistOne.addTrack(track);
        playlistTwo.addTrack(track);

        tracks.add(track);
        playlists.add(playlistOne);
        playlists.add(playlistTwo);

        trackService.removeTrack(tracks, playlists, track);

        assertTrue(tracks.isEmpty());
        assertTrue(playlistOne.getTracks().isEmpty());
        assertTrue(playlistTwo.getTracks().isEmpty());
    }

    @Test
    void updateEditableFieldsShouldUpdateTrackButNotLength() {
        Track originalTrack = new Track("Old title", "Old artist", "3:45", "Pop", 2024);
        Track editedTrack = new Track("New title", "New artist", "4:20", "Rock", 2025);

        trackService.updateEditableFields(originalTrack, editedTrack);

        assertEquals("New title", originalTrack.getTitle());
        assertEquals("New artist", originalTrack.getAuthor());
        assertEquals("Rock", originalTrack.getGenre());
        assertEquals(2025, originalTrack.getYear());

        assertEquals("3:45", originalTrack.getLength());
    }

    @Test
    void createTrackShouldCreateTrackWhenDataIsValid() {
        Track track = trackService.createTrack(
                "Song A",
                "Artist A",
                "3:45",
                "Pop",
                "2024"
        );

        assertEquals("Song A", track.getTitle());
        assertEquals("Artist A", track.getAuthor());
        assertEquals("3:45", track.getLength());
        assertEquals("Pop", track.getGenre());
        assertEquals(2024, track.getYear());
    }

    @Test
    void createTrackShouldRejectInvalidYear() {
        assertThrows(IllegalArgumentException.class, () ->
                trackService.createTrack(
                        "Song A",
                        "Artist A",
                        "3:45",
                        "Pop",
                        "abcd"
                )
        );
    }

    @Test
    void createTrackShouldRejectInvalidLength() {
        assertThrows(IllegalArgumentException.class, () ->
                trackService.createTrack(
                        "Song A",
                        "Artist A",
                        "wrong",
                        "Pop",
                        "2024"
                )
        );
    }

    @Test
    void createTrackShouldRejectEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () ->
                trackService.createTrack(
                        "",
                        "Artist A",
                        "3:45",
                        "Pop",
                        "2024"
                )
        );
    }
}