package com.musicplayer.service;

import com.musicplayer.model.Track;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaybackServiceTest {
    @Test
    void playTrackShouldStartPlaybackWithValidTrack() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);

        assertEquals(track, playbackService.getCurrentTrack());
        assertEquals(0, playbackService.getCurrentTime());
        assertTrue(playbackService.isPlaying());
        assertEquals(1, track.getPlayedCount());
    }

    @Test
    void pauseTrackShouldPauseCurrentPlayback() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);
        playbackService.pauseTrack();

        assertEquals(track, playbackService.getCurrentTrack());
        assertFalse(playbackService.isPlaying());
    }

    @Test
    void stopTrackShouldStopAndResetCurrentTime() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);
        playbackService.stopTrack();

        assertEquals(track, playbackService.getCurrentTrack());
        assertEquals(0, playbackService.getCurrentTime());
        assertFalse(playbackService.isPlaying());
    }

    @Test
    void playTrackShouldThrowExceptionWhenTrackIsNull() {
        PlaybackService playbackService = new PlaybackService();

        assertThrows(IllegalArgumentException.class, () -> playbackService.playTrack(null));
        assertNull(playbackService.getCurrentTrack());
        assertFalse(playbackService.isPlaying());
        assertEquals(0, playbackService.getCurrentTime());
    }
    @Test
    void playerShouldStartAndPauseSimulatedPlayback() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);

        assertEquals(track, playbackService.getCurrentTrack());
        assertTrue(playbackService.isPlaying());

        playbackService.pauseTrack();

        assertEquals(track, playbackService.getCurrentTrack());
        assertFalse(playbackService.isPlaying());
    }
}