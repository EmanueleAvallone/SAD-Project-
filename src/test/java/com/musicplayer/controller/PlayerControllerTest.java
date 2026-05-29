package com.musicplayer.controller;

import com.musicplayer.model.Track;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTest {

    @Test
    void playButtonShouldStartPlaybackWithSelectedTrack() {
        PlayerController playerController = new PlayerController();
        Track selectedTrack = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playerController.setSelectedTrack(selectedTrack);

        playerController.handlePlay();

        assertEquals(selectedTrack, playerController.getPlaybackService().getCurrentTrack());
        assertTrue(playerController.getPlaybackService().isPlaying());
        assertEquals(0, playerController.getPlaybackService().getCurrentTime());
        assertEquals(1, selectedTrack.getPlayedCount());
    }

    @Test
    void pauseButtonShouldPauseCurrentPlayback() {
        PlayerController playerController = new PlayerController();
        Track selectedTrack = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playerController.setSelectedTrack(selectedTrack);
        playerController.handlePlay();

        playerController.handlePause();

        assertEquals(selectedTrack, playerController.getPlaybackService().getCurrentTrack());
        assertFalse(playerController.getPlaybackService().isPlaying());
    }

    @Test
    void playButtonShouldNotStartPlaybackWhenNoTrackIsSelected() {
        PlayerController playerController = new PlayerController();

        playerController.setSelectedTrack(null);

        playerController.handlePlay();

        assertNull(playerController.getPlaybackService().getCurrentTrack());
        assertFalse(playerController.getPlaybackService().isPlaying());
        assertEquals(0, playerController.getPlaybackService().getCurrentTime());
    }

    @Test
    void selectedTrackShouldBeStoredInPlayerController() {
        PlayerController playerController = new PlayerController();
        Track selectedTrack = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playerController.setSelectedTrack(selectedTrack);

        assertEquals(selectedTrack, playerController.getSelectedTrack());
    }

    @Test
    void playAndPauseButtonsShouldModifyPlayerStateCorrectly() {
        PlayerController playerController = new PlayerController();
        Track selectedTrack = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playerController.setSelectedTrack(selectedTrack);

        playerController.handlePlay();

        assertEquals(selectedTrack, playerController.getPlaybackService().getCurrentTrack());
        assertTrue(playerController.getPlaybackService().isPlaying());

        playerController.handlePause();

        assertEquals(selectedTrack, playerController.getPlaybackService().getCurrentTrack());
        assertFalse(playerController.getPlaybackService().isPlaying());
    }
}