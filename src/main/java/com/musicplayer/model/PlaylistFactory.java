package com.musicplayer.model;

import com.musicplayer.model.filter.GenreFilterStrategy;
import com.musicplayer.model.filter.TagFilterStrategy;
import com.musicplayer.model.filter.TrackFilterStrategy;
import com.musicplayer.model.filter.YearFilterStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistFactory {

    private Playlist createRandomPlaylist(List<Track> allTracks, TrackFilterStrategy strategy, String playlistName) {
        List<Track> filteredTracks = new ArrayList<>();

        for (Track track : allTracks) {
            if (strategy.matches(track)) {
                filteredTracks.add(track);
            }
        }

        Collections.shuffle(filteredTracks);
        int maxTracks = Math.min(10, filteredTracks.size());

        Playlist generatedPlaylist = new Playlist(playlistName);
        generatedPlaylist.setFilterStrategy(strategy);
        for (Track track : filteredTracks.subList(0, maxTracks)) {
            generatedPlaylist.addTrack(track);
        }

        return generatedPlaylist;
    }

    public Playlist createPlaylistByTag(List<Track> allTracks, Tag tag, String playlistName) {
        Set<Tag> requiredTags = new HashSet<>();
        requiredTags.add(tag);
        return createRandomPlaylist(allTracks, new TagFilterStrategy(requiredTags), playlistName);
    }

    public Playlist createPlaylistByGenre(List<Track> allTracks, String genre, String playlistName) {
        return createRandomPlaylist(allTracks, new GenreFilterStrategy(genre), playlistName);
    }

    public Playlist createPlaylistByYear(List<Track> allTracks, int year, String playlistName) {
        return createRandomPlaylist(allTracks, new YearFilterStrategy(year), playlistName);
    }
}