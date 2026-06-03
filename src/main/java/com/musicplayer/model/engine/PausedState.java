package com.musicplayer.model.engine;

public class PausedState implements PlayerState {

    @Override
    public void play(MediaPlayerEngine engine) {
        engine.setState(new PlayingState());
    }

    @Override
    public void pause(MediaPlayerEngine engine) {
        // già in pausa, nessuna transizione
    }

    @Override
    public void stop(MediaPlayerEngine engine) {
        engine.setState(new StoppedState());
    }

    @Override
    public String getName() {
        return "PAUSED";
    }
}