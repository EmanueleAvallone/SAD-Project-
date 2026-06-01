package com.musicplayer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}