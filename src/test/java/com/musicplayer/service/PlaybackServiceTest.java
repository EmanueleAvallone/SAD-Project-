package com.musicplayer.service;

import com.musicplayer.model.Track;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlaybackServiceTest {

    @Test
    void playTrackShouldStartPlaybackWithValidTrack() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);

        assertEquals(track, playbackService.getCurrentTrack());
        assertEquals(0, playbackService.getCurrentTime());
        assertEquals(210, playbackService.getDuration());
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
        playbackService.advanceOneSecond();
        playbackService.advanceOneSecond();

        playbackService.stopTrack();

        assertEquals(track, playbackService.getCurrentTrack());
        assertEquals(0, playbackService.getCurrentTime());
        assertFalse(playbackService.isPlaying());
    }

    @Test
    void resetTrackShouldClearEntirePlaybackState() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);
        playbackService.advanceOneSecond();

        playbackService.resetTrack();

        assertNull(playbackService.getCurrentTrack());
        assertEquals(0, playbackService.getCurrentTime());
        assertEquals(0, playbackService.getDuration());
        assertFalse(playbackService.isPlaying());
    }

    @Test
    void playTrackShouldThrowExceptionWhenTrackIsNull() {
        PlaybackService playbackService = new PlaybackService();

        assertThrows(IllegalArgumentException.class, () -> playbackService.playTrack(null));
        assertNull(playbackService.getCurrentTrack());
        assertFalse(playbackService.isPlaying());
        assertEquals(0, playbackService.getCurrentTime());
        assertEquals(0, playbackService.getDuration());
    }

    @Test
    void advanceOneSecondShouldIncreaseCurrentTimeWhilePlaying() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);
        playbackService.advanceOneSecond();
        playbackService.advanceOneSecond();

        assertEquals(2, playbackService.getCurrentTime());
        assertTrue(playbackService.isPlaying());
    }

    @Test
    void advanceOneSecondShouldNotIncreaseCurrentTimeWhenPaused() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);
        playbackService.pauseTrack();
        playbackService.advanceOneSecond();

        assertEquals(0, playbackService.getCurrentTime());
        assertFalse(playbackService.isPlaying());
    }

    @Test
    void resumeTrackShouldContinuePlaybackWithoutResettingCurrentTime() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Song", "Artist", "03:30", "Pop", 2024);

        playbackService.playTrack(track);
        playbackService.advanceOneSecond();
        playbackService.advanceOneSecond();
        playbackService.pauseTrack();

        playbackService.resumeTrack();

        assertEquals(2, playbackService.getCurrentTime());
        assertTrue(playbackService.isPlaying());
        assertEquals(track, playbackService.getCurrentTrack());
    }

    @Test
    void playbackShouldStopAutomaticallyWhenTrackEnds() {
        PlaybackService playbackService = new PlaybackService();
        Track track = new Track("Short Song", "Artist", "00:02", "Pop", 2024);

        playbackService.playTrack(track);

        playbackService.advanceOneSecond();
        playbackService.advanceOneSecond();
        playbackService.advanceOneSecond();

        assertEquals(2, playbackService.getCurrentTime());
        assertFalse(playbackService.isPlaying());
    }

    @Test
    void playTrackShouldRestartFromZeroWhenANewTrackStarts() {
        PlaybackService playbackService = new PlaybackService();
        Track firstTrack = new Track("Song A", "Artist A", "03:30", "Pop", 2024);
        Track secondTrack = new Track("Song B", "Artist B", "04:00", "Rock", 2023);

        playbackService.playTrack(firstTrack);
        playbackService.advanceOneSecond();
        playbackService.advanceOneSecond();

        playbackService.playTrack(secondTrack);

        assertEquals(secondTrack, playbackService.getCurrentTrack());
        assertEquals(0, playbackService.getCurrentTime());
        assertEquals(240, playbackService.getDuration());
        assertTrue(playbackService.isPlaying());
    }
    @Test
    void nextTrackShouldMoveToNextTrackWhenAvailable() {
        PlaybackService playbackService = new PlaybackService();

        Track firstTrack = new Track("Song A", "Artist A", "03:30", "Pop", 2024);
        Track secondTrack = new Track("Song B", "Artist B", "04:00", "Rock", 2023);

        playbackService.setCurrentQueue(java.util.List.of(firstTrack, secondTrack));
        playbackService.playTrack(firstTrack);
        playbackService.advanceOneSecond();

        playbackService.nextTrack();

        assertEquals(secondTrack, playbackService.getCurrentTrack());
        assertEquals(0, playbackService.getCurrentTime());
        assertEquals(240, playbackService.getDuration());
        assertTrue(playbackService.isPlaying());
        assertEquals(1, playbackService.getCurrentTrackIndex());
    }
    @Test
    void nextTrackShouldStopPlaybackWhenThereIsNoNextTrack() {
        PlaybackService playbackService = new PlaybackService();

        Track onlyTrack = new Track("Song A", "Artist A", "03:30", "Pop", 2024);

        playbackService.setCurrentQueue(java.util.List.of(onlyTrack));
        playbackService.playTrack(onlyTrack);
        playbackService.advanceOneSecond();

        playbackService.nextTrack();

        assertEquals(onlyTrack, playbackService.getCurrentTrack());
        assertEquals(0, playbackService.getCurrentTime());
        assertFalse(playbackService.isPlaying());
        assertEquals(0, playbackService.getCurrentTrackIndex());
    }
    @Test
    void previousTrack_shouldGoToPreviousTrack_whenPreviousExists() {
        PlaybackService service = new PlaybackService();

        Track track1 = new Track("Song A", "Artist A", "3:00","Pop",2020);
        Track track2 = new Track("Song B", "Artist B", "4:00","rock",2000);

        service.setCurrentQueue(List.of(track1, track2));
        service.playTrack(track2);
        service.setCurrentQueue(List.of(track1, track2));

        service.previousTrack();

        assertNotNull(service.getCurrentTrack());
        assertEquals(track1, service.getCurrentTrack());
        assertEquals(0, service.getCurrentTrackIndex());
        assertEquals(0, service.getCurrentTime());
        assertTrue(service.isPlaying());
    }

    @Test
    void previousTrack_shouldRestartCurrentTrack_whenCurrentIsFirst() {
        PlaybackService service = new PlaybackService();

        Track track1 = new Track("Song A", "Artist A", "3:00","Pop",2020);

        service.setCurrentQueue(List.of(track1));
        service.playTrack(track1);
        service.setCurrentQueue(List.of(track1));
        service.advanceOneSecond();
        service.advanceOneSecond();

        service.previousTrack();

        assertEquals(track1, service.getCurrentTrack());
        assertEquals(0, service.getCurrentTrackIndex());
        assertEquals(0, service.getCurrentTime());
    }
    @Test
    void nextTrack_shouldStopAndStayOnLastTrack_whenCurrentIsLast() {
        PlaybackService service = new PlaybackService();

        Track t1 = new Track("Song A", "Artist A", "3:00","Pop",2020);
        Track t2 = new Track("Song B", "Artist B", "4:00","rock",2000);

        service.setCurrentQueue(List.of(t1, t2));
        service.playTrack(t2);
        service.setCurrentQueue(List.of(t1, t2));

        assertDoesNotThrow(service::nextTrack);
        assertEquals(t2, service.getCurrentTrack());
        assertEquals(1, service.getCurrentTrackIndex());
        assertEquals(0, service.getCurrentTime());
        assertFalse(service.isPlaying());
    }

    @Test
    void previousTrack_shouldRestartFirstTrack_whenCurrentIsFirst() {
        PlaybackService service = new PlaybackService();

        Track t1 = new Track("Song A", "Artist A", "3:00","Pop",2020);

        service.setCurrentQueue(List.of(t1));
        service.playTrack(t1);
        service.setCurrentQueue(List.of(t1));
        service.advanceOneSecond();

        assertDoesNotThrow(service::previousTrack);
        assertEquals(t1, service.getCurrentTrack());
        assertEquals(0, service.getCurrentTrackIndex());
        assertEquals(0, service.getCurrentTime());
    }

    @Test
    void nextTrack_shouldDoNothing_whenQueueIsEmpty() {
        PlaybackService service = new PlaybackService();

        assertDoesNotThrow(service::nextTrack);
        assertNull(service.getCurrentTrack());
        assertEquals(-1, service.getCurrentTrackIndex());
        assertFalse(service.isPlaying());
    }

    @Test
    void previousTrack_shouldDoNothing_whenQueueIsEmpty() {
        PlaybackService service = new PlaybackService();

        assertDoesNotThrow(service::previousTrack);
        assertNull(service.getCurrentTrack());
        assertEquals(-1, service.getCurrentTrackIndex());
        assertFalse(service.isPlaying());
    }
    @Test
    void shuffleRemainingQueue_shouldKeepCurrentTrackAndPreserveAllRemainingTracks() {
        PlaybackService service = new PlaybackService();

        Track t1 = new Track("Song A", "Artist A", "3:00","Pop",2020);
        Track t2 = new Track("Song B", "Artist B", "3:10","rock",2000);
        Track t3 = new Track("Song C", "Artist C", "3:20","Pop",2020);
        Track t4 = new Track("Song D", "Artist D", "3:30","rock",2000);

        List<Track> originalQueue = List.of(t1, t2, t3, t4);

        service.setCurrentQueue(originalQueue);
        service.playTrack(t2);
        service.setCurrentQueue(originalQueue);

        service.shuffleRemainingQueue();

        List<Track> shuffledQueue = service.getCurrentQueue();

        assertEquals(4, shuffledQueue.size());
        assertEquals(t2, service.getCurrentTrack());
        assertEquals(t2, shuffledQueue.get(1));
        assertEquals(t1, shuffledQueue.get(0));

        Set<Track> expectedTracks = new HashSet<>(originalQueue);
        Set<Track> actualTracks = new HashSet<>(shuffledQueue);

        assertEquals(expectedTracks, actualTracks);
        assertEquals(expectedTracks.size(), shuffledQueue.size());
    }
}