package com.musicplayer.service;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;

public class PlaylistService {

    public void validateTrackAdditionSelection(Playlist playlist, Track track) {
        if (playlist == null) {
            throw new IllegalArgumentException("Nessuna playlist selezionata");
        }

        if (track == null) {
            throw new IllegalArgumentException("Nessuna traccia selezionata");
        }
    }

    public void addTrackToPlaylist(Playlist playlist, Track track) {
        validateTrackAdditionSelection(playlist, track);
        playlist.addTrack(track);
    }

    public void validateTrackRemovalSelection(Playlist playlist, Track track) {
        if (playlist == null) {
            throw new IllegalArgumentException("Nessuna playlist selezionata.");
        }

        if (track == null) {
            throw new IllegalArgumentException("Nessuna traccia selezionata nella playlist.");
        }
    }

    public void removeTrackFromPlaylist(Playlist playlist, Track track) {
        validateTrackRemovalSelection(playlist, track);
        playlist.removeTrack(track);
    }
}
