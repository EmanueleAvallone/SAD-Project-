package com.musicplayer.model.engine;

public class StoppedState implements PlayerState {

    @Override
    public void play(MediaPlayerEngine engine) {
        engine.setState(new PlayingState());
    }

    @Override
    public void pause(MediaPlayerEngine engine) {
        // transizione non valida: nessuna azione
    }

    @Override
    public void stop(MediaPlayerEngine engine) {
        // già fermo: nessuna azione
    }

    @Override
    public String getName() {
        return "STOPPED";
    }
}