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
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void softDeleteTrackFromPlaylistsShouldRemoveTrackFromAllPlaylists() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Track firstTrack = new Track("Song A", "Artist A", "3:45", "Pop", 2024);
        Track secondTrack = new Track("Song B", "Artist B", "4:10", "Rock", 2023);

        Playlist playlistOne = new Playlist("Playlist one");
        Playlist playlistTwo = new Playlist("Playlist two");

        playlistOne.addTrack(firstTrack);
        playlistOne.addTrack(secondTrack);

        playlistTwo.addTrack(secondTrack);
        playlistTwo.addTrack(firstTrack);

        playlists.add(playlistOne);
        playlists.add(playlistTwo);

        playlistService.softDeleteTrackFromPlaylists(playlists, secondTrack);

        assertFalse(playlistOne.getTracks().contains(secondTrack));
        assertFalse(playlistTwo.getTracks().contains(secondTrack));

        assertEquals(1, playlistOne.getTracks().size());
        assertEquals(1, playlistTwo.getTracks().size());

        assertEquals(firstTrack, playlistOne.getTracks().get(0));
        assertEquals(firstTrack, playlistTwo.getTracks().get(0));
    }

    @Test
    void restorePendingDeletedTrackInPlaylistsShouldRestoreTrackAtOriginalPositions() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Track firstTrack = new Track("Song A", "Artist A", "3:45", "Pop", 2024);
        Track secondTrack = new Track("Song B", "Artist B", "4:10", "Rock", 2023);
        Track thirdTrack = new Track("Song C", "Artist C", "2:50", "Jazz", 2022);

        Playlist playlistOne = new Playlist("Playlist one");
        Playlist playlistTwo = new Playlist("Playlist two");

        playlistOne.addTrack(firstTrack);
        playlistOne.addTrack(secondTrack);
        playlistOne.addTrack(thirdTrack);

        playlistTwo.addTrack(secondTrack);
        playlistTwo.addTrack(thirdTrack);

        playlists.add(playlistOne);
        playlists.add(playlistTwo);

        playlistService.softDeleteTrackFromPlaylists(playlists, secondTrack);
        playlistService.restorePendingDeletedTrackInPlaylists(secondTrack);

        assertEquals(3, playlistOne.getTracks().size());
        assertEquals(firstTrack, playlistOne.getTracks().get(0));
        assertEquals(secondTrack, playlistOne.getTracks().get(1));
        assertEquals(thirdTrack, playlistOne.getTracks().get(2));

        assertEquals(2, playlistTwo.getTracks().size());
        assertEquals(secondTrack, playlistTwo.getTracks().get(0));
        assertEquals(thirdTrack, playlistTwo.getTracks().get(1));
    }

    @Test
    void softDeleteTrackFromPlaylistsShouldRejectNullPlaylistList() {
        PlaylistService playlistService = new PlaylistService();

        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.softDeleteTrackFromPlaylists(null, track)
        );
    }

    @Test
    void softDeleteTrackFromPlaylistsShouldRejectNullTrack() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.softDeleteTrackFromPlaylists(playlists, null)
        );
    }

    @Test
    void restorePendingDeletedTrackInPlaylistsShouldRejectNullTrack() {
        PlaylistService playlistService = new PlaylistService();

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.restorePendingDeletedTrackInPlaylists(null)
        );
    }

    @Test
    void softDeletePlaylistShouldRemovePlaylistFromVisibleListButKeepItPending() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Playlist firstPlaylist = new Playlist("Playlist A");
        Playlist secondPlaylist = new Playlist("Playlist B");

        Track trackOne = new Track("Song A", "Artist A", "3:45", "Pop", 2024);
        Track trackTwo = new Track("Song B", "Artist B", "4:10", "Rock", 2023);

        secondPlaylist.addTrack(trackOne);
        secondPlaylist.addTrack(trackTwo);

        playlists.add(firstPlaylist);
        playlists.add(secondPlaylist);

        playlistService.softDeletePlaylist(playlists, secondPlaylist);

        assertFalse(playlists.contains(secondPlaylist));
        assertEquals(1, playlists.size());
        assertEquals(firstPlaylist, playlists.get(0));

        assertTrue(playlistService.hasPendingDeletedPlaylist());
        assertEquals(secondPlaylist, playlistService.getPendingDeletedPlaylist());

        assertEquals(2, playlistService.getPendingDeletedPlaylist().getTracks().size());
        assertTrue(playlistService.getPendingDeletedPlaylist().getTracks().contains(trackOne));
        assertTrue(playlistService.getPendingDeletedPlaylist().getTracks().contains(trackTwo));
    }

    @Test
    void restorePendingDeletedPlaylistShouldRestorePlaylistWithItsTracksAtOriginalIndex() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Playlist firstPlaylist = new Playlist("Playlist A");
        Playlist secondPlaylist = new Playlist("Playlist B");
        Playlist thirdPlaylist = new Playlist("Playlist C");

        Track trackOne = new Track("Song A", "Artist A", "3:45", "Pop", 2024);
        Track trackTwo = new Track("Song B", "Artist B", "4:10", "Rock", 2023);

        secondPlaylist.addTrack(trackOne);
        secondPlaylist.addTrack(trackTwo);

        playlists.add(firstPlaylist);
        playlists.add(secondPlaylist);
        playlists.add(thirdPlaylist);

        playlistService.softDeletePlaylist(playlists, secondPlaylist);
        playlistService.restorePendingDeletedPlaylist(playlists);

        assertEquals(3, playlists.size());
        assertEquals(firstPlaylist, playlists.get(0));
        assertEquals(secondPlaylist, playlists.get(1));
        assertEquals(thirdPlaylist, playlists.get(2));

        Playlist restoredPlaylist = playlists.get(1);

        assertEquals("Playlist B", restoredPlaylist.getName());
        assertEquals(2, restoredPlaylist.getTracks().size());
        assertEquals(trackOne, restoredPlaylist.getTracks().get(0));
        assertEquals(trackTwo, restoredPlaylist.getTracks().get(1));

        assertFalse(playlistService.hasPendingDeletedPlaylist());
    }

    @Test
    void restorePendingDeletedPlaylistShouldDoNothingWhenThereIsNoPendingDeletion() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Playlist playlist = new Playlist("Playlist A");
        playlists.add(playlist);

        playlistService.restorePendingDeletedPlaylist(playlists);

        assertEquals(1, playlists.size());
        assertEquals(playlist, playlists.get(0));
        assertFalse(playlistService.hasPendingDeletedPlaylist());
    }

    @Test
    void clearPendingDeletedPlaylistShouldClearTemporaryDeletionState() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Playlist playlist = new Playlist("Playlist A");
        playlists.add(playlist);

        playlistService.softDeletePlaylist(playlists, playlist);

        assertTrue(playlistService.hasPendingDeletedPlaylist());

        playlistService.clearPendingDeletedPlaylist();

        assertFalse(playlistService.hasPendingDeletedPlaylist());
        assertNull(playlistService.getPendingDeletedPlaylist());
    }

    @Test
    void softDeletePlaylistShouldRejectNullPlaylistList() {
        PlaylistService playlistService = new PlaylistService();

        Playlist playlist = new Playlist("Playlist A");

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.softDeletePlaylist(null, playlist)
        );
    }

    @Test
    void softDeletePlaylistShouldRejectNullPlaylist() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.softDeletePlaylist(playlists, null)
        );
    }

    @Test
    void softDeletePlaylistShouldRejectPlaylistNotInList() {
        PlaylistService playlistService = new PlaylistService();

        ObservableList<Playlist> playlists = FXCollections.observableArrayList();

        Playlist playlist = new Playlist("Playlist A");

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.softDeletePlaylist(playlists, playlist)
        );
    }

    @Test
    void restorePendingDeletedPlaylistShouldRejectNullPlaylistList() {
        PlaylistService playlistService = new PlaylistService();

        assertThrows(
                IllegalArgumentException.class,
                () -> playlistService.restorePendingDeletedPlaylist(null)
        );
    }

    @Test
    void moveTrackInPlaylistShouldUpdateOrderCorrectly() {
        PlaylistService service = new PlaylistService();
        Playlist playlist = new Playlist("My Playlist");

        Track t1 = new Track("Song A", "Artist A", "3:00","Pop",2020);
        Track t2 = new Track("Song B", "Artist B", "3:10","rock",2000);
        Track t3 = new Track("Song C", "Artist C", "3:20","Pop",2020);
        Track t4 = new Track("Song D", "Artist D", "3:30","Rap",2023);

        playlist.addTrack(t1); // index 0
        playlist.addTrack(t2); // index 1
        playlist.addTrack(t3); // index 2
        playlist.addTrack(t4); // index 3

        // Spostiamo "Song D" dall'indice 3 all'indice 1
        service.moveTrackInPlaylist(playlist, 3, 1);

        assertEquals(t1, playlist.getTracks().get(0));
        assertEquals(t4, playlist.getTracks().get(1));
        assertEquals(t2, playlist.getTracks().get(2));
        assertEquals(t3, playlist.getTracks().get(3));
    }

    @Test
    void moveTrackInPlaylistShouldRejectOutOfBoundsIndices() {
        PlaylistService service = new PlaylistService();
        Playlist playlist = new Playlist("My Playlist");

        Track t1 = new Track("Song A", "Artist A", "3:00","Pop",2020);
        Track t2 = new Track("Song B", "Artist B", "3:10","rock",2000);
        playlist.addTrack(t1);
        playlist.addTrack(t2);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> service.moveTrackInPlaylist(playlist, 1, -1)
        );

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> service.moveTrackInPlaylist(playlist, 1, 1000)
        );

        // L'ordine originale non deve cambiare
        assertEquals(t1, playlist.getTracks().get(0));
        assertEquals(t2, playlist.getTracks().get(1));
    }

}
