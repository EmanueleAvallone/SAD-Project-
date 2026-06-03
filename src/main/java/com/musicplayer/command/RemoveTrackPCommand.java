package com.musicplayer.command;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import com.musicplayer.service.PlaylistService;

public class RemoveTrackPCommand implements Command{

    private final PlaylistService playlistService;
    private final Playlist playlist;
    private final Track track;

    public RemoveTrackPCommand(PlaylistService playlistService, Playlist playlist, Track track) {
        this.playlistService = playlistService;
        this.playlist = playlist;
        this.track = track;
    }

    @Override
    public void execute() {
        playlistService.removeTrackFromPlaylist(playlist, track);
    }

    @Override
    public void undo() {
        playlistService.addTrackToPlaylist(playlist, track);
    }
}
