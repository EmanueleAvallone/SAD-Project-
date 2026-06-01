package com.musicplayer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrackTest {

    @Test
    void constructorShouldAssignAllFieldsCorrectly() {
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        assertEquals("Song A", track.getTitle());
        assertEquals("Artist A", track.getAuthor());
        assertEquals("3:45", track.getLength());
        assertEquals("Pop", track.getGenre());
        assertEquals(2024, track.getYear());
        assertEquals(0, track.getPlayedCount());
    }

    @Test
    void settersShouldUpdateEditableFields() {
        Track track = new Track("Old title", "Old artist", "3:45", "Pop", 2024);

        track.setTitle("New title");
        track.setAuthor("New artist");
        track.setGenre("Rock");
        track.setYear(2025);

        assertEquals("New title", track.getTitle());
        assertEquals("New artist", track.getAuthor());
        assertEquals("Rock", track.getGenre());
        assertEquals(2025, track.getYear());
    }

    @Test
    void incrementPlayedCountShouldIncreaseCounter() {
        Track track = new Track("Song A", "Artist A", "3:45", "Pop", 2024);

        track.incrementPlayedCount();
        track.incrementPlayedCount();

        assertEquals(2, track.getPlayedCount());
    }
}