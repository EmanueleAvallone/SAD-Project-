package com.musicplayer.service;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;

public class PlaylistService {

    public void validateTrackAdditionSelection(Playlist playlist, Track track) {
        if (playlist == null) {
            throw new IllegalArgumentException("No playlist selected");
        }

        if (track == null) {
            throw new IllegalArgumentException("No track selected");
        }
    }
}
