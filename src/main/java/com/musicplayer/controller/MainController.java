package com.musicplayer.controller;

import com.musicplayer.command.CommandManager;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import com.musicplayer.service.PlaylistService;
import com.musicplayer.service.SearchService;
import com.musicplayer.service.TrackService;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller principale dell'applicazione Music Playlist Manager.
 * Questa classe coordina la schermata principale dell'applicazione.
 * Per rispettare la separazione delle responsabilità, MainController non contiene
 * la logica specifica delle singole sezioni, ma delega:
 * - a LibraryController la gestione della Track Library, dei filtri e della sezione Most Played;
 * - a PlaylistController la gestione della sidebar playlist e della selected playlist;
 * - a PlayerController la gestione della riproduzione simulata;
 * - ai service la business logic vera e propria.
 * MainController mantiene invece la responsabilità dello snackbar globale,
 * perché è un componente trasversale della schermata principale.
 */
public class MainController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Track> trackTableView;

    @FXML
    private TableColumn<Track, String> trackTitleColumn;

    @FXML
    private TableColumn<Track, String> trackAuthorColumn;

    @FXML
    private TableColumn<Track, String> trackLengthColumn;

    @FXML
    private TableColumn<Track, String> trackGenreColumn;

    @FXML
    private TableColumn<Track, Integer> trackYearColumn;

    @FXML
    private TableColumn<Track, String> trackTagsColumn;

    @FXML
    private TableColumn<Track, Integer> trackPlayCountColumn;

    @FXML
    private TableView<Track> playlistTrackTableView;

    @FXML
    private TableColumn<Track, Integer> playlistTrackOrderColumn;

    @FXML
    private TableColumn<Track, String> playlistTrackTitleColumn;

    @FXML
    private TableColumn<Track, String> playlistTrackAuthorColumn;

    @FXML
    private TableColumn<Track, String> playlistTrackLengthColumn;

    @FXML
    private TableColumn<Track, String> playlistTrackGenreColumn;

    @FXML
    private PlayerController playerControlController;

    /**
     * Controller della sidebar playlist inclusa tramite:
     * <fx:include fx:id="playlistSection" source="PlaylistView.fxml"/>
     * JavaFX crea automaticamente questo campo aggiungendo "Controller"
     * all'fx:id dell'include.
     */
    @FXML
    private PlaylistController playlistSectionController;

    @FXML
    private ScrollPane mostPlayedScrollPane;

    @FXML
    private VBox topTracksContainer;

    @FXML
    private VBox emptyMostPlayedView;

    @FXML
    private CheckBox favouriteTagCheckBox;

    @FXML
    private CheckBox explicitTagCheckBox;

    @FXML
    private CheckBox newReleaseTagCheckBox;

    @FXML
    private HBox undoSnackbar;

    @FXML
    private Label snackbarMessageLabel;

    @FXML
    private Button snackbarUndoButton;
    @FXML
    private TextField searchField;

    /**
     * Catalogo principale delle tracce.
     */
    private final ObservableList<Track> tracks = FXCollections.observableArrayList();

    /**
     * Lista principale delle playlist.
     */
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    /**
     * Lista osservabile delle tracce appartenenti alla playlist selezionata.
     */
    private final ObservableList<Track> selectedPlaylistTracks = FXCollections.observableArrayList();

    /**
     * Service per la logica applicativa delle tracce.
     */
    private final TrackService trackService = new TrackService();

    /**
     * Service per la logica applicativa delle playlist.
     */
    private final PlaylistService playlistService = new PlaylistService();

    /**
     * Gestore dei comandi annullabili.
     */
    private final CommandManager commandManager = new CommandManager();

    /**
     * Controller di sezione per Track Library, filtri e Most Played.
     */
    private final LibraryController librarySectionController = new LibraryController();

    /**
     * Timer usato per nascondere lo snackbar dopo alcuni secondi.
     */
    private PauseTransition snackbarTimer;

    /**
     * Azione da eseguire se l'utente preme Undo nello snackbar.
     */
    private Runnable pendingUndoAction;

    /**
     * Azione da eseguire se lo snackbar scade senza Undo.
     */
    private Runnable pendingConfirmAction;
    /**
     * Gestore di Ricerca.
     */
    private final SearchService searchService = new SearchService();

    /**
     * Inizializza la schermata principale e collega i controller di sezione.
     */
    @FXML
    private void initialize() {
        initializePlaylistSection();
        initializeLibrarySection();
        configurePlaybackRefresh();

        hideUndoSnackbar();

        if (statusLabel != null) {
            statusLabel.setText("Applicazione avviata correttamente.");
        }
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            librarySectionController.applySearch(newValue);
            playlistSectionController.applySearch(newValue);
        });
    }

    /**
     * Inizializza la sezione playlist delegando a PlaylistController.
     * La sidebar delle playlist è ora definita in PlaylistView.fxml, mentre
     * la tabella "Selected playlist" rimane nella view principale. Per questo
     * motivo MainController passa al PlaylistController solo i riferimenti
     * condivisi necessari.
     */
    private void initializePlaylistSection() {
        playlistSectionController.initializeSection(
                trackTableView,
                playlistTrackTableView,
                playlistTrackOrderColumn,
                playlistTrackTitleColumn,
                playlistTrackAuthorColumn,
                playlistTrackLengthColumn,
                playlistTrackGenreColumn,
                statusLabel,
                playlists,
                selectedPlaylistTracks,
                playerControlController,
                playlistService,
                commandManager,
                searchService
        );

        playlistSectionController.setUndoSnackbarHandler(this::showUndoSnackbar);
    }

    /**
     * Inizializza la sezione libreria delegando a LibraryController.
     */
    private void initializeLibrarySection() {
        librarySectionController.initializeSection(
                trackTableView,
                trackTitleColumn,
                trackAuthorColumn,
                trackLengthColumn,
                trackGenreColumn,
                trackYearColumn,
                trackTagsColumn,
                trackPlayCountColumn,
                favouriteTagCheckBox,
                explicitTagCheckBox,
                newReleaseTagCheckBox,
                mostPlayedScrollPane,
                topTracksContainer,
                emptyMostPlayedView,
                statusLabel,
                trackService,
                playlistService,
                tracks,
                playlists,
                playerControlController,
                playlistSectionController,
                this::showUndoSnackbar,
                searchService
        );
    }

    /**
     * Collega il player ai controller di sezione.
     * Quando cambia lo stato della riproduzione, vengono aggiornate la Track Library,
     * la tabella della selected playlist e la sezione Most Played.
     */
    private void configurePlaybackRefresh() {
        if (playerControlController == null) {
            return;
        }

        playerControlController.setPlaybackChangeListener(() -> {
            librarySectionController.refreshTrackLibrary();
            playlistSectionController.refreshSelectedPlaylistTable();
            librarySectionController.refreshHighlights();
            librarySectionController.updateMostPlayedSectionIfNeeded();
        });
    }

    /**
     * Gestisce la pulizia della ricerca globale.
     */
    @FXML
    private void handleClearSearch() {
        if (searchField != null) {
            searchField.clear();
        }
    }

    /**
     * Apre la schermata di aggiunta traccia.
     */
    @FXML
    private void handleAddTrack() {
        librarySectionController.addTrack();
    }

    /**
     * Apre la schermata di modifica della traccia selezionata.
     */
    @FXML
    private void handleEditTrack() {
        librarySectionController.editSelectedTrack();
    }

    /**
     * Elimina temporaneamente la traccia selezionata.
     */
    @FXML
    private void handleDeleteTrack() {
        librarySectionController.deleteSelectedTrack();
    }

    /**
     * Aggiunge la traccia selezionata alla playlist selezionata.
     * Questo metodo resta nel MainController perché il pulsante si trova ancora
     * nella sezione centrale "Selected playlist" della view principale.
     */
    @FXML
    private void handleAddToPlaylist() {
        playlistSectionController.addSelectedTrackToPlaylist();
    }

    /**
     * Rimuove la traccia selezionata dalla playlist selezionata.
     */
    @FXML
    private void handleRemoveFromPlaylist() {
        playlistSectionController.removeSelectedTrackFromPlaylist();
    }

    /**
     * Sposta verso l'alto la traccia selezionata nella playlist.
     */
    @FXML
    private void handleMoveTrackUp() {
        playlistSectionController.moveSelectedTrackUp();
    }

    /**
     * Sposta verso il basso la traccia selezionata nella playlist.
     */
    @FXML
    private void handleMoveTrackDown() {
        playlistSectionController.moveSelectedTrackDown();
    }

    /**
     * Applica i filtri avanzati alla Track Library.
     */
    @FXML
    private void handleApplyFilters() {
        librarySectionController.applyFilters();
    }

    /**
     * Rimuove i filtri avanzati.
     */
    @FXML
    private void handleResetFilters() {
        librarySectionController.resetFilters();
    }

    /**
     * Riproduce la classifica Most Played.
     */
    @FXML
    private void handlePlayMostPlayed() {
        librarySectionController.playAllMostPlayed();
    }

    /**
     * Predisposizione per Undo generale.
     */
    @FXML
    private void handleUndo() {
        System.out.println("Undo");
    }

    /**
     * Predisposizione per Redo generale.
     */
    @FXML
    private void handleRedo() {
        System.out.println("Redo");
    }

    /**
     * Mostra lo snackbar globale.
     * Lo snackbar resta nel MainController perché è un componente globale della
     * schermata. I controller di sezione forniscono però le azioni da eseguire
     * in caso di Undo o di conferma definitiva.
     *
     * @param message messaggio da mostrare
     * @param undoAction azione da eseguire se l'utente preme Undo
     * @param confirmAction azione da eseguire se lo snackbar scade
     */
    private void showUndoSnackbar(String message, Runnable undoAction, Runnable confirmAction) {
        if (undoSnackbar == null || snackbarMessageLabel == null) {
            return;
        }

        this.pendingUndoAction = undoAction;
        this.pendingConfirmAction = confirmAction;

        snackbarMessageLabel.setText(message);

        undoSnackbar.setVisible(true);
        undoSnackbar.setManaged(true);

        if (snackbarTimer != null) {
            snackbarTimer.stop();
        }

        snackbarTimer = new PauseTransition(Duration.seconds(5));
        snackbarTimer.setOnFinished(event -> confirmPendingSnackbarAction());
        snackbarTimer.playFromStart();
    }

    /**
     * Gestisce il click sul pulsante Undo dello snackbar.
     */
    @FXML
    private void handleSnackbarUndo() {
        if (snackbarTimer != null) {
            snackbarTimer.stop();
        }

        if (pendingUndoAction != null) {
            pendingUndoAction.run();
        }

        clearSnackbarActions();
        hideUndoSnackbar();
    }

    /**
     * Conferma definitivamente l'operazione associata allo snackbar.
     */
    private void confirmPendingSnackbarAction() {
        if (pendingConfirmAction != null) {
            pendingConfirmAction.run();
        }

        clearSnackbarActions();
        hideUndoSnackbar();
    }

    /**
     * Rimuove le azioni pendenti dello snackbar.
     */
    private void clearSnackbarActions() {
        pendingUndoAction = null;
        pendingConfirmAction = null;
    }

    /**
     * Nasconde lo snackbar.
     */
    private void hideUndoSnackbar() {
        if (undoSnackbar != null) {
            undoSnackbar.setVisible(false);
            undoSnackbar.setManaged(false);
        }
    }
}