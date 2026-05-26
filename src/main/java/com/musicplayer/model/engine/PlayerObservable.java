package com.musicplayer.model.engine;

public interface PlayerObservable {
    void addObserver(PlayerObserver observer);
    void removeObserver(PlayerObserver observer);
}
