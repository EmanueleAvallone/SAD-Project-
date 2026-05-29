package com.musicplayer.controller;

import com.musicplayer.model.engine.MediaPlayerEngine;
import com.musicplayer.model.engine.PlayerObserver;

public class PlayerController implements PlayerObserver {
    @Override
    public void update(MediaPlayerEngine engine) {
        // TODO: aggiornare la UI o lo stato del controller quando cambia il player
    }
}
