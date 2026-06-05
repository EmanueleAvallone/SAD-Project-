package com.musicplayer.model;

import com.musicplayer.model.filter.TagFilterStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistFactory {

    /**
     * Genera una playlist casuale filtrata per uno specifico Tag.
     *
     * @param allTracks La lista di tutte le tracce disponibili
     * @param tag Il tag richiesto (es. FAV, NEW)
     * @param playlistName Il nome da assegnare alla nuova playlist
     * @return Una nuova Playlist popolata con un massimo di 10 tracce casuali
     */
    public Playlist createPlaylistByTag(List<Track> allTracks, Tag tag, String playlistName) {

        Set<Tag> requiredTags = new HashSet<>();
        requiredTags.add(tag);


        TagFilterStrategy strategy = new TagFilterStrategy(requiredTags);
        List<Track> filteredTracks = new ArrayList<>();

        for (Track track : allTracks) {
            if (strategy.matches(track)) {
                filteredTracks.add(track);
            }
        }

        Collections.shuffle(filteredTracks);

        int maxTracks = Math.min(10, filteredTracks.size());
        List<Track> selectedTracks = filteredTracks.subList(0, maxTracks);

        Playlist generatedPlaylist = new Playlist(playlistName);
        for (Track track : selectedTracks) {
            generatedPlaylist.addTrack(track);
        }

        return generatedPlaylist;
    }
}