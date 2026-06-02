package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlaylistServiceTest {

    @Test
    void removeTrackFromPlaylistShouldRemoveTrackFromSelectedPlaylist() {

        PlaylistService playlistService = new PlaylistService();
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        playlist.addTrack(track);
        playlistService.removeTrackFromPlaylist(playlist, track);

        assertFalse(playlist.getTracks().contains(track));
        assertTrue(playlist.getTracks().isEmpty());
    }


    @Test
    void removeTrackFromPlaylistShouldNotRemoveTrackFromMainLibrary() {

        PlaylistService playlistService = new PlaylistService();
        List<Track> mainLibrary = new ArrayList<>();
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        mainLibrary.add(track);
        playlist.addTrack(track);
        playlistService.removeTrackFromPlaylist(playlist, track);

        assertFalse(playlist.getTracks().contains(track));
        assertEquals(1, mainLibrary.size());
        assertTrue(mainLibrary.contains(track));
    }


    @Test

    void removeTrackFromPlaylistShouldRejectNullPlaylist() {

        PlaylistService playlistService = new PlaylistService();
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.removeTrackFromPlaylist(null, track)
        );
    }


    @Test

    void removeTrackFromPlaylistShouldRejectNullTrack() {

        PlaylistService playlistService = new PlaylistService();
        Playlist playlist = new Playlist("My playlist");

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.removeTrackFromPlaylist(playlist, null)
        );
    }


    @Test
    void removeTrackFromPlaylistShouldRejectTrackNotInPlaylist() {

        PlaylistService playlistService = new PlaylistService();
        Playlist playlist = new Playlist("My playlist");
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.removeTrackFromPlaylist(playlist, track)
        );

        assertTrue(playlist.getTracks().isEmpty());
    }

}
