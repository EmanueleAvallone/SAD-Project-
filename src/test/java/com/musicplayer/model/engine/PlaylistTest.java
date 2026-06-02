package com.musicplayer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlaylistTest {

    @Test
    void constructorShouldAssignNameAndCreateEmptyTrackList() {
        Playlist playlist = new Playlist("My playlist");

        assertEquals("My playlist", playlist.getName());
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    void addTrackShouldInsertTrackIntoPlaylist() {
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        playlist.addTrack(track);

        assertEquals(1, playlist.getTracks().size());
        assertEquals(track, playlist.getTracks().get(0));
    }

    @Test
    void removeTrackShouldRemoveTrackFromPlaylist() {
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        playlist.addTrack(track);
        playlist.removeTrack(track);

        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    void renameShouldUpdatePlaylistName() {
        Playlist playlist = new Playlist("Old name");

        playlist.rename("New name");

        assertEquals("New name", playlist.getName());
    }


    @Test
    void addTrackShouldRejectNullTrack() {
        Playlist playlist = new Playlist("My playlist");

        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(null));
        assertTrue(playlist.getTracks().isEmpty());
    }


    @Test
    void addTrackShouldRejectDuplicateTrack() {
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        playlist.addTrack(track);

        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(track));
        assertEquals(1, playlist.getTracks().size());
        assertEquals(track, playlist.getTracks().get(0));
    }


    @Test
    void addTrackShouldKeepTrackAssociatedWithSelectedPlaylist() {
        Playlist selectedPlaylist = new Playlist("Selected playlist");
        Playlist anotherPlaylist = new Playlist("Another playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        selectedPlaylist.addTrack(track);

        assertEquals(1, selectedPlaylist.getTracks().size());
        assertTrue(selectedPlaylist.getTracks().contains(track));
        assertTrue(anotherPlaylist.getTracks().isEmpty());
    }


    @Test
    void addTrackToPlaylistShouldNotRemoveTrackFromMainLibrary() {
        java.util.List<Track> mainLibrary = new java.util.ArrayList<>();
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        mainLibrary.add(track);

        playlist.addTrack(track);

        assertEquals(1, mainLibrary.size());
        assertTrue(mainLibrary.contains(track));
        assertEquals(1, playlist.getTracks().size());
        assertTrue(playlist.getTracks().contains(track));
    }


    @Test
    void removeTrackShouldRejectNullTrack() {
        Playlist playlist = new Playlist("My playlist");

        assertThrows(IllegalArgumentException.class, () -> playlist.removeTrack(null));
        assertTrue(playlist.getTracks().isEmpty());
    }


    @Test
    void removeTrackShouldRejectTrackNotInPlaylist() {
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        assertThrows(IllegalArgumentException.class, () -> playlist.removeTrack(track));
        assertTrue(playlist.getTracks().isEmpty());
    }


    @Test
    void removeTrackFromPlaylistShouldNotRemoveTrackFromMainLibrary() {
        java.util.List<Track> mainLibrary = new java.util.ArrayList<>();
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        mainLibrary.add(track);
        playlist.addTrack(track);

        playlist.removeTrack(track);

        assertTrue(playlist.getTracks().isEmpty());
        assertEquals(1, mainLibrary.size());
        assertTrue(mainLibrary.contains(track));
    }

}