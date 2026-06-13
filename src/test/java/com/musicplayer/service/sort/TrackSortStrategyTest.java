package com.musicplayer.service.sort;

import com.musicplayer.model.Track;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import java.util.Comparator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrackSortStrategyTest {

    @Test
    void titleSortStrategyShouldSortTracksAscending() {
        TrackSortStrategy strategy = new TitleSortStrategy();

        Track trackA = new Track("Banana", "Author B", "3:00", "Pop", 2020);
        Track trackB = new Track("Apple", "Author A", "4:00", "Rock", 2021);
        Track trackC = new Track("Zebra", "Author C", "2:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator());

        assertEquals(trackB, tracks.get(0));
        assertEquals(trackA, tracks.get(1));
        assertEquals(trackC, tracks.get(2));
    }

    @Test
    void titleSortStrategyShouldSortTracksDescending() {
        TrackSortStrategy strategy = new TitleSortStrategy();

        Track trackA = new Track("Banana", "Author B", "3:00", "Pop", 2020);
        Track trackB = new Track("Apple", "Author A", "4:00", "Rock", 2021);
        Track trackC = new Track("Zebra", "Author C", "2:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator().reversed());

        assertEquals(trackC, tracks.get(0));
        assertEquals(trackA, tracks.get(1));
        assertEquals(trackB, tracks.get(2));
    }

    @Test
    void authorSortStrategyShouldSortTracksAscending() {
        TrackSortStrategy strategy = new AuthorSortStrategy();

        Track trackA = new Track("Song A", "Marco", "3:00", "Pop", 2020);
        Track trackB = new Track("Song B", "Anna", "4:00", "Rock", 2021);
        Track trackC = new Track("Song C", "Luca", "2:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator());

        assertEquals(trackB, tracks.get(0));
        assertEquals(trackC, tracks.get(1));
        assertEquals(trackA, tracks.get(2));
    }

    @Test
    void authorSortStrategyShouldSortTracksDescending() {
        TrackSortStrategy strategy = new AuthorSortStrategy();

        Track trackA = new Track("Song A", "Marco", "3:00", "Pop", 2020);
        Track trackB = new Track("Song B", "Anna", "4:00", "Rock", 2021);
        Track trackC = new Track("Song C", "Luca", "2:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator().reversed());

        assertEquals(trackA, tracks.get(0));
        assertEquals(trackC, tracks.get(1));
        assertEquals(trackB, tracks.get(2));
    }

    @Test
    void lengthSortStrategyShouldSortTracksAscending() {
        TrackSortStrategy strategy = new LengthSortStrategy();

        Track trackA = new Track("Song A", "Author A", "3:45", "Pop", 2020);
        Track trackB = new Track("Song B", "Author B", "1:30", "Rock", 2021);
        Track trackC = new Track("Song C", "Author C", "5:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator());

        assertEquals(trackB, tracks.get(0));
        assertEquals(trackA, tracks.get(1));
        assertEquals(trackC, tracks.get(2));
    }

    @Test
    void lengthSortStrategyShouldSortTracksDescending() {
        TrackSortStrategy strategy = new LengthSortStrategy();

        Track trackA = new Track("Song A", "Author A", "3:45", "Pop", 2020);
        Track trackB = new Track("Song B", "Author B", "1:30", "Rock", 2021);
        Track trackC = new Track("Song C", "Author C", "5:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator().reversed());

        assertEquals(trackC, tracks.get(0));
        assertEquals(trackA, tracks.get(1));
        assertEquals(trackB, tracks.get(2));
    }

    @Test
    void yearSortStrategyShouldSortTracksAscending() {
        TrackSortStrategy strategy = new YearSortStrategy();

        Track trackA = new Track("Song A", "Author A", "3:45", "Pop", 2024);
        Track trackB = new Track("Song B", "Author B", "1:30", "Rock", 2020);
        Track trackC = new Track("Song C", "Author C", "5:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator());

        assertEquals(trackB, tracks.get(0));
        assertEquals(trackC, tracks.get(1));
        assertEquals(trackA, tracks.get(2));
    }

    @Test
    void yearSortStrategyShouldSortTracksDescending() {
        TrackSortStrategy strategy = new YearSortStrategy();

        Track trackA = new Track("Song A", "Author A", "3:45", "Pop", 2024);
        Track trackB = new Track("Song B", "Author B", "1:30", "Rock", 2020);
        Track trackC = new Track("Song C", "Author C", "5:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator().reversed());

        assertEquals(trackA, tracks.get(0));
        assertEquals(trackC, tracks.get(1));
        assertEquals(trackB, tracks.get(2));
    }

    @Test
    void playedCountSortStrategyShouldSortTracksAscending() {
        TrackSortStrategy strategy = new PlayedCountSortStrategy();

        Track trackA = new Track("Song A", "Author A", "3:45", "Pop", 2024);
        Track trackB = new Track("Song B", "Author B", "1:30", "Rock", 2020);
        Track trackC = new Track("Song C", "Author C", "5:00", "Jazz", 2022);

        trackA.setPlayedCount(10);
        trackB.setPlayedCount(0);
        trackC.setPlayedCount(5);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));

        tracks.sort(strategy.getComparator());

        assertEquals(trackB, tracks.get(0));
        assertEquals(trackC, tracks.get(1));
        assertEquals(trackA, tracks.get(2));
    }

    @Test
    void playedCountSortStrategyShouldSortTracksDescending() {
        TrackSortStrategy strategy = new PlayedCountSortStrategy();

        Track trackA = new Track("Song A", "Author A", "3:45", "Pop", 2024);
        Track trackB = new Track("Song B", "Author B", "1:30", "Rock", 2020);
        Track trackC = new Track("Song C", "Author C", "5:00", "Jazz", 2022);

        trackA.setPlayedCount(10);
        trackB.setPlayedCount(0);
        trackC.setPlayedCount(5);

        List<Track> tracks = new ArrayList<>(List.of(trackA, trackB, trackC));
        tracks.sort(strategy.getComparator().reversed());

        assertEquals(trackA, tracks.get(0));
        assertEquals(trackC, tracks.get(1));
        assertEquals(trackB, tracks.get(2));
    }

    @Test
    void lengthSortStrategyShouldHandleInvalidOrEmptyLength() {
        TrackSortStrategy strategy = new LengthSortStrategy();

        Track invalidLengthTrack = new Track("Song A", "Author A", "wrong", "Pop", 2024);
        Track emptyLengthTrack = new Track("Song B", "Author B", "", "Rock", 2020);
        Track validLengthTrack = new Track("Song C", "Author C", "2:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(List.of(validLengthTrack, invalidLengthTrack, emptyLengthTrack));

        tracks.sort(strategy.getComparator());

        assertEquals(invalidLengthTrack, tracks.get(0));
        assertEquals(emptyLengthTrack, tracks.get(1));
        assertEquals(validLengthTrack, tracks.get(2));
    }

    @Test
    void sortStrategiesShouldHandleSingleElementList() {
        TrackSortStrategy strategy = new TitleSortStrategy();

        Track track = new Track("Only Song", "Only Author", "3:00", "Pop", 2024);

        List<Track> tracks = new ArrayList<>(List.of(track));

        tracks.sort(strategy.getComparator());

        assertEquals(1, tracks.size());
        assertEquals(track, tracks.get(0));
    }

    @Test
    void sortStrategiesShouldHandleEmptyList() {
        TrackSortStrategy strategy = new TitleSortStrategy();

        List<Track> tracks = new ArrayList<>();

        tracks.sort(strategy.getComparator());

        assertEquals(0, tracks.size());
    }

    @Test
    void playlistOrderSortStrategyShouldSortTracksByOriginalPlaylistOrderAscending() {
        Track trackA = new Track("It's my life", "Bon Jovi", "03:46", "Pop Rock", 1984);
        Track trackB = new Track("Summertime Sadness", "Lana Del Rey", "03:00", "Pop Rock", 1984);
        Track trackC = new Track("Billie Jean", "Michael Jackson", "04:35", "Pop Rock", 1982);
        Track trackD = new Track("Stairway To Heaven", "Led Zeppelin", "06:46", "Rock", 1972);

        List<Track> originalOrder = List.of(trackA, trackB, trackC, trackD);

        TrackSortStrategy strategy = new PlaylistOrderSortStrategy(originalOrder);

        List<Track> visibleTracks = new ArrayList<>(List.of(trackD, trackC, trackA, trackB));

        visibleTracks.sort(strategy.getComparator());

        assertEquals(trackA, visibleTracks.get(0));
        assertEquals(trackB, visibleTracks.get(1));
        assertEquals(trackC, visibleTracks.get(2));
        assertEquals(trackD, visibleTracks.get(3));
    }

    @Test
    void playlistOrderSortStrategyShouldSortTracksByOriginalPlaylistOrderDescending() {
        Track trackA = new Track("It's my life", "Bon Jovi", "03:46", "Pop Rock", 1984);
        Track trackB = new Track("Summertime Sadness", "Lana Del Rey", "03:00", "Pop Rock", 1984);
        Track trackC = new Track("Billie Jean", "Michael Jackson", "04:35", "Pop Rock", 1982);
        Track trackD = new Track("Stairway To Heaven", "Led Zeppelin", "06:46", "Rock", 1972);

        List<Track> originalOrder = List.of(trackA, trackB, trackC, trackD);

        TrackSortStrategy strategy = new PlaylistOrderSortStrategy(originalOrder);

        List<Track> visibleTracks = new ArrayList<>(List.of(trackA, trackB, trackC, trackD));

        visibleTracks.sort(strategy.getComparator().reversed());

        assertEquals(trackD, visibleTracks.get(0));
        assertEquals(trackC, visibleTracks.get(1));
        assertEquals(trackB, visibleTracks.get(2));
        assertEquals(trackA, visibleTracks.get(3));
    }

    @Test
    void playlistOrderSortStrategyShouldPlaceUnknownTracksAtTheEnd() {
        Track trackA = new Track("It's my life", "Bon Jovi", "03:46", "Pop Rock", 1984);
        Track trackB = new Track("Summertime Sadness", "Lana Del Rey", "03:00", "Pop Rock", 1984);
        Track unknownTrack = new Track("Unknown", "Unknown Author", "01:00", "Pop", 2024);

        List<Track> originalOrder = List.of(trackA, trackB);

        TrackSortStrategy strategy = new PlaylistOrderSortStrategy(originalOrder);

        List<Track> visibleTracks = new ArrayList<>(List.of(unknownTrack, trackB, trackA));

        visibleTracks.sort(strategy.getComparator());

        assertEquals(trackA, visibleTracks.get(0));
        assertEquals(trackB, visibleTracks.get(1));
        assertEquals(unknownTrack, visibleTracks.get(2));
    }

    @Test
    void titleSortStrategyShouldHandleNullOrEmptyTitles() {
        TrackSortStrategy strategy = new TitleSortStrategy();

        Track nullTitleTrack = new Track(null, "Author A", "3:00", "Pop", 2020);
        Track emptyTitleTrack = new Track("", "Author B", "4:00", "Rock", 2021);
        Track validTitleTrack = new Track("Zebra", "Author C", "2:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(
                List.of(validTitleTrack, nullTitleTrack, emptyTitleTrack)
        );

        tracks.sort(strategy.getComparator());

        assertEquals(nullTitleTrack, tracks.get(0));
        assertEquals(emptyTitleTrack, tracks.get(1));
        assertEquals(validTitleTrack, tracks.get(2));
    }

    @Test
    void authorSortStrategyShouldHandleNullOrEmptyAuthors() {
        TrackSortStrategy strategy = new AuthorSortStrategy();

        Track nullAuthorTrack = new Track("Song A", null, "3:00", "Pop", 2020);
        Track emptyAuthorTrack = new Track("Song B", "", "4:00", "Rock", 2021);
        Track validAuthorTrack = new Track("Song C", "Zebra Author", "2:00", "Jazz", 2022);

        List<Track> tracks = new ArrayList<>(
                List.of(validAuthorTrack, nullAuthorTrack, emptyAuthorTrack)
        );

        tracks.sort(strategy.getComparator());

        assertEquals(nullAuthorTrack, tracks.get(0));
        assertEquals(emptyAuthorTrack, tracks.get(1));
        assertEquals(validAuthorTrack, tracks.get(2));
    }

    @Test
    void lengthSortStrategyShouldHandleNullEmptyAndInvalidLength() {
        TrackSortStrategy strategy = new LengthSortStrategy();

        Track nullLengthTrack = new Track("Song A", "Author A", null, "Pop", 2020);
        Track emptyLengthTrack = new Track("Song B", "Author B", "", "Rock", 2021);
        Track invalidLengthTrack = new Track("Song C", "Author C", "wrong", "Jazz", 2022);
        Track validLengthTrack = new Track("Song D", "Author D", "2:00", "Pop", 2023);

        List<Track> tracks = new ArrayList<>(
                List.of(validLengthTrack, invalidLengthTrack, emptyLengthTrack, nullLengthTrack)
        );

        tracks.sort(strategy.getComparator());

        assertEquals(invalidLengthTrack, tracks.get(0));
        assertEquals(emptyLengthTrack, tracks.get(1));
        assertEquals(nullLengthTrack, tracks.get(2));
        assertEquals(validLengthTrack, tracks.get(3));
    }

    @Test
    void playedCountSortStrategyShouldHandleZeroPlays() {
        TrackSortStrategy strategy = new PlayedCountSortStrategy();

        Track zeroPlaysTrack = new Track("Song A", "Author A", "3:00", "Pop", 2020);
        Track fivePlaysTrack = new Track("Song B", "Author B", "4:00", "Rock", 2021);

        zeroPlaysTrack.setPlayedCount(0);
        fivePlaysTrack.setPlayedCount(5);

        List<Track> tracks = new ArrayList<>(List.of(fivePlaysTrack, zeroPlaysTrack));

        tracks.sort(strategy.getComparator());

        assertEquals(zeroPlaysTrack, tracks.get(0));
        assertEquals(fivePlaysTrack, tracks.get(1));
    }


}
