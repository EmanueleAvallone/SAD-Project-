package com.musicplayer.model.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaPlayerEngineStateTest {

    @Test
    void shouldHandlePlayerStateTransitionsCorrectly() {
        MediaPlayerEngine engine = new MediaPlayerEngine();

        assertEquals("STOPPED", engine.getCurrentStateName());

        engine.play();
        assertEquals("PLAYING", engine.getCurrentStateName());

        engine.pause();
        assertEquals("PAUSED", engine.getCurrentStateName());

        engine.play();
        assertEquals("PLAYING", engine.getCurrentStateName());

        engine.stop();
        assertEquals("STOPPED", engine.getCurrentStateName());

        engine.pause();
        assertEquals("STOPPED", engine.getCurrentStateName());
    }
}