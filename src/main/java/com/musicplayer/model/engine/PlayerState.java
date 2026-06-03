package com.musicplayer.model.engine;

public interface PlayerState {
    void play(MediaPlayerEngine engine);
    void pause(MediaPlayerEngine engine);
    void stop(MediaPlayerEngine engine);
    String getName();
}
