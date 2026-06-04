package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

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

    @Test
    void getTopPlayedTracksShouldReturnOrderedListAndExcludeZeroPlays() {
        ObservableList<Track> mockTracks = FXCollections.observableArrayList();

        Track t1 = new Track("Canzone Poco Ascoltata", "Autore A", "3:00", "Pop", 2020);
        Track t2 = new Track("Hit dell'Anno", "Autore B", "4:00", "Rock", 2021);
        Track t3 = new Track("Mai Ascoltata", "Autore C", "2:30", "Jazz", 2019);
        Track t4 = new Track("Canzone Media", "Autore D", "3:15", "Pop", 2022);

        t1.setPlayedCount(5);
        t2.setPlayedCount(150);
        t3.setPlayedCount(0);
        t4.setPlayedCount(20);

        mockTracks.addAll(t1, t2, t3, t4);

        List<Track> topTracks = trackService.getTopPlayedTracks(mockTracks, 2);

        assertEquals(2, topTracks.size(), "La dimensione della classifica deve essere esattamente 2");
        assertEquals("Hit dell'Anno", topTracks.get(0).getTitle(), "Il brano più ascoltato deve essere primo");
        assertEquals("Canzone Media", topTracks.get(1).getTitle(), "Il secondo brano più ascoltato deve essere secondo");
    }

    @Test
    void softDeleteTrackShouldRemoveTrackFromVisibleListButKeepItPending() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        Track firstTrack = new Track("Song A", "Artist A", "3:45", "Pop", 2024);
        Track secondTrack = new Track("Song B", "Artist B", "4:10", "Rock", 2023);

        tracks.add(firstTrack);
        tracks.add(secondTrack);

        trackService.softDeleteTrack(tracks, firstTrack);

        assertFalse(tracks.contains(firstTrack));
        assertEquals(1, tracks.size());
        assertEquals(secondTrack, tracks.get(0));

        assertTrue(trackService.hasPendingDeletedTrack());
        assertSame(firstTrack, trackService.getPendingDeletedTrack());
        assertEquals(0, trackService.getPendingDeletedTrackIndex());
    }

    @Test
    void softDeleteTrackShouldRejectNullTrackList() {
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        assertThrows(
                IllegalArgumentException.class,
                () -> trackService.softDeleteTrack(null, track)
        );
    }

    @Test
    void softDeleteTrackShouldRejectNullTrack() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        assertThrows(
                IllegalArgumentException.class,
                () -> trackService.softDeleteTrack(tracks, null)
        );
    }

    @Test
    void softDeleteTrackShouldRejectTrackNotInList() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        assertThrows(
                IllegalArgumentException.class,
                () -> trackService.softDeleteTrack(tracks, track)
        );
    }

    @Test
    void restorePendingDeletedTrackShouldRestoreTrackAtOriginalIndex() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        Track firstTrack = new Track("Song A", "Artist A", "3:45", "Pop", 2024);
        Track secondTrack = new Track("Song B", "Artist B", "4:10", "Rock", 2023);
        Track thirdTrack = new Track("Song C", "Artist C", "2:50", "Jazz", 2022);

        tracks.add(firstTrack);
        tracks.add(secondTrack);
        tracks.add(thirdTrack);

        trackService.softDeleteTrack(tracks, secondTrack);
        trackService.restorePendingDeletedTrack(tracks);

        assertEquals(3, tracks.size());
        assertEquals(firstTrack, tracks.get(0));
        assertEquals(secondTrack, tracks.get(1));
        assertEquals(thirdTrack, tracks.get(2));

        assertFalse(trackService.hasPendingDeletedTrack());
    }

    @Test
    void restorePendingDeletedTrackShouldDoNothingWhenThereIsNoPendingDeletion() {
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);
        tracks.add(track);

        trackService.restorePendingDeletedTrack(tracks);

        assertEquals(1, tracks.size());
        assertEquals(track, tracks.get(0));
        assertFalse(trackService.hasPendingDeletedTrack());
    }

    @Test
    void restorePendingDeletedTrackShouldRejectNullTrackList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> trackService.restorePendingDeletedTrack(null)
        );
    }
}