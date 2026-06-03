package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @Test
    void removeTrackFromPlaylist_shouldDecreaseSizeAndShiftFollowingTracks() {
        PlaylistService service = new PlaylistService();
        Playlist playlist = new Playlist("My Playlist");

        Track t1 = new Track("Song A", "Artist A", "3:00","Pop",2020);
        Track t2 = new Track("Song B", "Artist B", "3:10","rock",2000);
        Track t3 = new Track("Song C", "Artist C", "3:20","Pop",2020);

        playlist.addTrack(t1);
        playlist.addTrack(t2);
        playlist.addTrack(t3);

        service.removeTrackFromPlaylist(playlist, t2);

        assertEquals(2, playlist.getTracks().size());
        assertEquals(t1, playlist.getTracks().get(0));
        assertEquals(t3, playlist.getTracks().get(1));
        assertFalse(playlist.getTracks().contains(t2));
    }
    @Test
    void addTrackToPlaylist_shouldThrowException_whenTrackAlreadyExists() {
        PlaylistService service = new PlaylistService();
        Playlist playlist = new Playlist("My Playlist");

        Track track = new Track("Song A", "Artist A", "3:00","Pop",2020);

        service.addTrackToPlaylist(playlist, track);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addTrackToPlaylist(playlist, track)
        );

        assertEquals("La traccia è già presente in questa playlist.", exception.getMessage());
        assertEquals(1, playlist.getTracks().size());
    }
    @Test
    void createPlaylistShouldCreatePlaylistAndAddItToList() {
        PlaylistService playlistService = new PlaylistService();
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Playlist createdPlaylist = playlistService.createPlaylist(playlists, "My Playlist");

        assertEquals("My Playlist", createdPlaylist.getName());
        assertEquals(1, playlists.size());
        assertTrue(playlists.contains(createdPlaylist));
        assertEquals(createdPlaylist, playlists.get(0));
    }
    @Test
    void createPlaylistShouldRejectDuplicateName() {
        PlaylistService playlistService = new PlaylistService();
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        playlistService.createPlaylist(playlists, "My Playlist");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.createPlaylist(playlists, "My Playlist")
        );

        assertEquals("Esiste già una playlist con questo nome.", exception.getMessage());
        assertEquals(1, playlists.size());
    }
    @Test
    void createPlaylistShouldCreatePlaylistWhenNameIsValid() {
        PlaylistService playlistService = new PlaylistService();
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Playlist createdPlaylist = playlistService.createPlaylist(playlists, "My Playlist");

        assertEquals("My Playlist", createdPlaylist.getName());
        assertEquals(1, playlists.size());
        assertTrue(playlists.contains(createdPlaylist));
        assertEquals(createdPlaylist, playlists.get(0));
    }

    @Test
    void createPlaylistShouldRejectEmptyName() {
        PlaylistService playlistService = new PlaylistService();
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.createPlaylist(playlists, "")
        );

        assertEquals("Il nome della playlist non può essere vuoto.", exception.getMessage());
        assertTrue(playlists.isEmpty());
    }


}
