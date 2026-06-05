package com.musicplayer.model.engine;

import com.musicplayer.model.PlaylistFactory;
import com.musicplayer.model.Track;
import com.musicplayer.model.Tag;
import com.musicplayer.model.Playlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PlaylistFactoryTest {

    private PlaylistFactory factory;
    private List<Track> testTracks;

    @BeforeEach
    public void setUp() {
        factory = new PlaylistFactory();
        testTracks = new ArrayList<>();

        // 1. Traccia solo con tag FAV
        Track t1 = new Track("Song FAV", "Artist A", "3:00", "Pop", 2020);
        t1.addTag(Tag.FAV);

        // 2. Traccia con due tag: FAV e NEW
        Track t2 = new Track("Song FAV and NEW", "Artist B", "3:30", "Rock", 2021);
        t2.addTag(Tag.FAV);
        t2.addTag(Tag.NEW);

        // 3. Traccia solo con tag EXPLICIT
        Track t3 = new Track("Song EXPLICIT", "Artist C", "4:00", "Rap", 2022);
        t3.addTag(Tag.EXPLICIT);

        // 4. Traccia senza alcun tag
        Track t4 = new Track("Song Without Tags", "Artist D", "2:30", "Jazz", 2019);

        // Popoliamo la lista finta di brani per il test
        testTracks.add(t1);
        testTracks.add(t2);
        testTracks.add(t3);
        testTracks.add(t4);
    }

    /**
     * Verifica che la factory estragga solo ed esclusivamente i brani
     * che possiedono il tag richiesto.
     */
    @Test
    public void testCreatePlaylistByTag_ExtractsOnlyCorrectTracks() {
        // Generiamo una playlist filtrando per FAV
        Playlist playlist = factory.createPlaylistByTag(testTracks, Tag.FAV, "Test Playlist FAV");

        assertNotNull(playlist, "La playlist generata non dovrebbe essere null");
        assertEquals("Test Playlist FAV", playlist.getName());

        // Ci aspettiamo esattamente 2 brani (t1 e t2 hanno il tag FAV)
        assertEquals(2, playlist.getTracks().size(), "La playlist dovrebbe contenere esattamente 2 brani");

        // Verifichiamo che ogni brano nella playlist abbia effettivamente il tag FAV
        for (Track track : playlist.getTracks()) {
            assertTrue(track.hasTag(Tag.FAV), "Ogni brano estratto deve avere il tag FAV");
        }
    }

    /**
     * Verifica che la funzione restituisca una playlist con una lista di brani vuota
     * se nessun brano nella libreria possiede quel determinato tag.
     */
    @Test
    public void testCreatePlaylistByTag_ReturnsEmptyPlaylistIfNoTracksMatch() {
        // Rimuoviamo l'unica canzone EXPLICIT dalla lista per simulare l'assenza totale del tag
        testTracks.removeIf(track -> track.hasTag(Tag.EXPLICIT));

        // Proviamo a generare una playlist con il tag EXPLICIT
        Playlist playlist = factory.createPlaylistByTag(testTracks, Tag.EXPLICIT, "Test Empty");

        assertNotNull(playlist, "La playlist generata non dovrebbe essere null");

        // La lista dei brani dentro la playlist deve essere vuota
        assertTrue(playlist.getTracks().isEmpty(), "La playlist dovrebbe essere vuota se nessun brano ha quel tag");
    }

    @Test
    public void testCreatePlaylistByGenre_CaseInsensitiveMatches() {
        // Setup per test genere
        Track t1 = new Track("Song 1", "Artist A", "3:00", "rock", 2020);
        Track t2 = new Track("Song 2", "Artist B", "3:30", " ROCK ", 2021); // Spazi e maiuscole
        Track t3 = new Track("Song 3", "Artist C", "4:00", "Pop", 2022);
        testTracks.clear();
        testTracks.add(t1); testTracks.add(t2); testTracks.add(t3);

        Playlist playlist = factory.createPlaylistByGenre(testTracks, "Rock", "Mix Rock");

        assertEquals(2, playlist.getTracks().size(), "Deve trovare 2 brani Rock ignorando il case");
    }

    @Test
    public void testCreatePlaylistByYear_MatchesCorrectly() {
        Track t1 = new Track("Song 1", "A", "3:00", "Pop", 1999);
        Track t2 = new Track("Song 2", "B", "3:30", "Rock", 1999);
        Track t3 = new Track("Song 3", "C", "4:00", "Jazz", 2005);
        testTracks.clear();
        testTracks.add(t1); testTracks.add(t2); testTracks.add(t3);

        Playlist playlist = factory.createPlaylistByYear(testTracks, 1999, "Mix 1999");

        assertEquals(2, playlist.getTracks().size(), "Deve trovare 2 brani del 1999");
    }
}