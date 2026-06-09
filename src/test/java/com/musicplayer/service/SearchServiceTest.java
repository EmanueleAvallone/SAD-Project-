package com.musicplayer.service;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Tag;
import com.musicplayer.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    private SearchService searchService;
    private Track track1;
    private Track track2;
    private Track trackNullFields;
    private Playlist playlist1;
    private Playlist playlist2;
    private Playlist playlistNullName;

    @BeforeEach
    void setUp() {
        searchService = new SearchService();

        track1 = new Track("Shape of You", "Ed Sheeran", "3:53", "Pop", 2017);
        track1.getTags().add(Tag.FAV);

        track2 = new Track("Blinding Lights", "The Weeknd", "3:20", "Synthwave", 2019);
        track2.getTags().add(Tag.NEW);

        trackNullFields = new Track(null, null, "4:00", null, 2020);

        playlist1 = new Playlist("Favorites");
        playlist2 = new Playlist("Workout Mix");
        playlistNullName = new Playlist(null);
    }

    @Test
    void searchTracks_shouldReturnEmptyList_whenTracksIsNull() {
        List<Track> result = searchService.searchTracks(null, "ed");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchTracks_shouldReturnCopyOfTracks_whenQueryIsNull() {
        List<Track> input = List.of(track1, track2);
        List<Track> result = searchService.searchTracks(input, null);

        assertEquals(input, result);
    }

    @Test
    void searchTracks_shouldReturnCopyOfTracks_whenQueryIsBlank() {
        List<Track> input = List.of(track1, track2);
        List<Track> result = searchService.searchTracks(input, "   ");

        assertEquals(input, result);
    }

    @Test
    void searchTracks_shouldMatchTitle_caseInsensitive() {
        List<Track> result = searchService.searchTracks(List.of(track1, track2), "shape");

        assertEquals(1, result.size());
        assertEquals(track1, result.get(0));
    }

    @Test
    void searchTracks_shouldMatchAuthor_caseInsensitive() {
        List<Track> result = searchService.searchTracks(List.of(track1, track2), "weeknd");

        assertEquals(1, result.size());
        assertEquals(track2, result.get(0));
    }

    @Test
    void searchTracks_shouldMatchCombinedTitleAndAuthor() {
        List<Track> result = searchService.searchTracks(List.of(track1, track2), "ed sheeran");

        assertEquals(1, result.size());
        assertEquals(track1, result.get(0));
    }

    @Test
    void searchTracks_shouldTrimQuery() {
        List<Track> result = searchService.searchTracks(List.of(track1), "   shape   ");

        assertEquals(1, result.size());
        assertEquals(track1, result.get(0));
    }

    @Test
    void searchTracks_shouldHandleNullTrackFields() {
        List<Track> result = searchService.searchTracks(List.of(trackNullFields, track1), "shape");

        assertEquals(1, result.size());
        assertEquals(track1, result.get(0));
    }

    @Test
    void searchTracks_shouldReturnEmpty_whenNoMatch() {
        List<Track> result = searchService.searchTracks(List.of(track1, track2), "classical");

        assertTrue(result.isEmpty());
    }

    @Test
    void matchesTrack_shouldReturnFalse_whenTrackIsNull() {
        assertFalse(searchService.matchesTrack(null, "shape"));
    }

    @Test
    void matchesTrack_shouldReturnTrue_whenQueryIsBlank() {
        assertTrue(searchService.matchesTrack(track1, " "));
    }

    @Test
    void matchesTrack_shouldMatchTitle() {
        assertTrue(searchService.matchesTrack(track1, "shape"));
    }

    @Test
    void matchesTrack_shouldMatchAuthor() {
        assertTrue(searchService.matchesTrack(track1, "sheeran"));
    }

    @Test
    void matchesTrack_shouldReturnFalse_whenNoTrackMatch() {
        assertFalse(searchService.matchesTrack(track1, "classical"));
    }

    @Test
    void searchPlaylists_shouldReturnEmptyList_whenPlaylistsIsNull() {
        List<Playlist> result = searchService.searchPlaylists(null, "fav");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchPlaylists_shouldReturnCopyOfPlaylists_whenQueryIsNull() {
        List<Playlist> input = List.of(playlist1, playlist2);
        List<Playlist> result = searchService.searchPlaylists(input, null);

        assertEquals(input, result);
    }

    @Test
    void searchPlaylists_shouldReturnCopyOfPlaylists_whenQueryIsBlank() {
        List<Playlist> input = List.of(playlist1, playlist2);
        List<Playlist> result = searchService.searchPlaylists(input, "  ");

        assertEquals(input, result);
    }

    @Test
    void searchPlaylists_shouldMatchName_caseInsensitive() {
        List<Playlist> result = searchService.searchPlaylists(List.of(playlist1, playlist2), "workout");

        assertEquals(1, result.size());
        assertEquals(playlist2, result.get(0));
    }

    @Test
    void searchPlaylists_shouldTrimQuery() {
        List<Playlist> result = searchService.searchPlaylists(List.of(playlist1), "  favor  ");

        assertEquals(1, result.size());
        assertEquals(playlist1, result.get(0));
    }

    @Test
    void searchPlaylists_shouldHandleNullPlaylistName() {
        List<Playlist> result = searchService.searchPlaylists(List.of(playlistNullName, playlist1), "fav");

        assertEquals(1, result.size());
        assertEquals(playlist1, result.get(0));
    }

    @Test
    void matchesPlaylist_shouldReturnFalse_whenPlaylistIsNull() {
        assertFalse(searchService.matchesPlaylist(null, "fav"));
    }

    @Test
    void matchesPlaylist_shouldReturnTrue_whenQueryIsBlank() {
        assertTrue(searchService.matchesPlaylist(playlist1, " "));
    }

    @Test
    void matchesPlaylist_shouldMatchName() {
        assertTrue(searchService.matchesPlaylist(playlist1, "favor"));
    }

    @Test
    void matchesPlaylist_shouldReturnFalse_whenNoMatch() {
        assertFalse(searchService.matchesPlaylist(playlist1, "chill"));
    }
}