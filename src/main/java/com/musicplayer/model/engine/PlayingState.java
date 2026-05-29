package com.musicplayer.model.engine;

public class PlayingState implements PlayerState {

    @Override
    public void play(MediaPlayerEngine engine) {
        // già in riproduzione, nessuna transizione
    }

    @Override
    public void pause(MediaPlayerEngine engine) {
        engine.setState(new PausedState());
    }

    @Override
    public void stop(MediaPlayerEngine engine) {
        engine.setState(new StoppedState());
    }

    @Override
    public String getName() {
        return "PLAYING";
    }
}