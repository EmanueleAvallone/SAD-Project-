package com.musicplayer.controller;

import com.musicplayer.model.Track;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTest {

    @Test
    void selectedTrackShouldBeStoredInPlayerController() {
        PlayerController playerController = new PlayerController();
        Track selectedTrack = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playerController.setSelectedTrack(selectedTrack);

        assertEquals(selectedTrack, playerController.getSelectedTrack());
    }

    @Test
    void playWithoutSelectedTrackShouldNotStartPlayback() {
        PlayerController playerController = new PlayerController();

        playerController.setSelectedTrack(null);
        playerController.handlePlay();

        assertNull(playerController.getPlaybackService().getCurrentTrack());
        assertFalse(playerController.getPlaybackService().isPlaying());
        assertEquals("Seleziona una traccia da riprodurre.", playerController.getLastStatusMessage());
    }

    @Test
    void stopPlaybackShouldResetControllerState() {
        PlayerController playerController = new PlayerController();
        Track selectedTrack = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playerController.setSelectedTrack(selectedTrack);
        playerController.stopPlayback();

        assertNull(playerController.getSelectedTrack());
        assertNull(playerController.getPlaybackService().getCurrentTrack());
        assertFalse(playerController.getPlaybackService().isPlaying());
    }
}