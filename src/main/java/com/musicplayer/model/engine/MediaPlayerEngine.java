package com.musicplayer.model.engine;

import com.musicplayer.model.PlayMode;
import com.musicplayer.model.Playlist;

import java.util.List;

public class MediaPlayerEngine implements PlayerObservable {
    private PlayerState currentState;

    private PlayStrategy currentPlayMode;

    private List<PlayerObserver> observers;

    private Playlist currentPlaylist; // freccia verso playlist in riproduzione

    private int currentindex;

    @Override public void addObserver(PlayerObserver observer) {}
    @Override public void removeObserver(PlayerObserver observer) {}
}
