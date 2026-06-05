package com.musicplayer.controller;

import com.musicplayer.command.CommandManager;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import com.musicplayer.service.PlaylistService;
import com.musicplayer.service.TrackService;
import com.musicplayer.model.Tag;
import com.musicplayer.model.filter.TrackFilterStrategy;
import com.musicplayer.model.filter.TagFilterStrategy;

import java.util.HashSet;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableRow;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;


import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.beans.property.SimpleStringProperty;
import java.util.stream.Collectors;

import javafx.animation.PauseTransition;

import javafx.scene.layout.HBox;

import java.io.IOException;

import java.util.Optional;
import javafx.util.Duration;

/**
 * Controller principale dell'applicazione Music Playlist Manager.
 *
 * Questa classe coordina la schermata principale dell'applicazione.
 * Per rispettare la separazione delle responsabilità, MainController non contiene
 * tutta la logica applicativa, ma delega:
 *
 * - a TrackService la logica sulle tracce;
 * - a PlaylistService la logica sulle playlist;
 * - a PlaylistController la gestione della sezione playlist della UI;
 * - a PlayerController la gestione della sezione player;
 * - a CommandManager la gestione delle operazioni annullabili.
 *
 * MainController resta quindi responsabile soprattutto di:
 * - inizializzare la schermata principale;
 * - collegare le tabelle principali;
 * - aprire finestre secondarie;
 * - coordinare i controller di sezione;
 * - mostrare messaggi di stato o errore.
 */
public class MainController {

    @FXML
    private Label statusLabel;

    @FXML
    private ListView<Playlist> playlistListView;

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
    private BorderPane rootPane;

    @FXML
    private PlayerController playerControlController;

    @FXML
    private ScrollPane mostPlayedScrollPane;

    @FXML
    private VBox topTracksContainer;

    @FXML
    private VBox emptyMostPlayedView;

    @FXML
    private HBox undoSnackbar;

    @FXML
    private Label snackbarMessageLabel;

    @FXML
    private Button snackbarUndoButton;

    @FXML
    private CheckBox favouriteTagCheckBox;

    @FXML
    private CheckBox explicitTagCheckBox;

    @FXML
    private CheckBox newReleaseTagCheckBox;

    @FXML
    private TableColumn<Track, String> trackTagsColumn;

    private FilteredList<Track> filteredTracks;

    /**
     * Controller di sezione dedicato alla libreria (Most Played).
     */
    private final LibraryController librarySectionController = new LibraryController();

    private int lastTotalPlays = -1;

    /**
     * Timer usato per nascondere automaticamente lo snackbar dopo pochi secondi.
     */
    private PauseTransition snackbarTimer;

    /**
     * Catalogo principale delle tracce.
     *
     * È una ObservableList perché la TableView viene aggiornata automaticamente
     * quando una traccia viene aggiunta o rimossa.
     */
    private final ObservableList<Track> tracks = FXCollections.observableArrayList();

    /**
     * Lista principale delle playlist.
     */
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    /**
     * Lista osservabile delle tracce della playlist attualmente selezionata.
     */
    private final ObservableList<Track> selectedPlaylistTracks = FXCollections.observableArrayList();

    /**
     * Service dedicato alla logica applicativa sulle tracce.
     */
    private final TrackService trackService = new TrackService();

    /**
     * Service dedicato alla logica applicativa sulle playlist.
     */
    private final PlaylistService playlistService = new PlaylistService();

    /**
     * Gestore dei comandi annullabili.
     *
     * Viene passato anche al PlaylistController per supportare il Command Pattern
     * nella rimozione di tracce da playlist.
     */
    private final CommandManager commandManager = new CommandManager();

    /**
     * Controller di sezione dedicato alle playlist.
     *
     * Non è collegato direttamente a un FXML separato: viene inizializzato da
     * MainController passandogli i riferimenti ai componenti grafici della sezione
     * playlist presenti nella schermata principale.
     */
    private final PlaylistController playlistSectionController =
            new PlaylistController(playlistService, commandManager);

    /**
     * Inizializza la schermata principale.
     *
     * Configura la Track Library e delega la configurazione della sezione playlist
     * al PlaylistController. In questo modo MainController resta più leggero e
     * mantiene il ruolo di coordinatore generale.
     */
    @FXML
    private void initialize() {
        configureTrackLibraryTable();

        playlistSectionController.initializeSection(
                playlistListView,
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
                playerControlController
        );

        librarySectionController.initializeSection(
                mostPlayedScrollPane,
                topTracksContainer,
                emptyMostPlayedView,
                trackService,
                tracks,
                playerControlController
        );

        if (playerControlController != null) {
            playerControlController.setPlaybackChangeListener(() -> {
                trackTableView.refresh();
                playlistSectionController.refreshSelectedPlaylistTable();

                int currentTotalPlays = tracks.stream()
                        .mapToInt(Track::getPlayedCount)
                        .sum();

                if (currentTotalPlays != lastTotalPlays) {
                    librarySectionController.updateMostPlayedSection();
                    lastTotalPlays = currentTotalPlays;
                }

            });
        }

        if (statusLabel != null) {
            statusLabel.setText("Applicazione avviata correttamente.");
        }

        System.out.println("FXML collegato correttamente al MainController.");
        System.out.println("playerControlController = " + playerControlController);
    }

    /**
     * Configura la tabella principale della Track Library.
     *
     * La Track Library mostra tutte le tracce importate nel catalogo principale.
     * Quando l'utente seleziona una traccia, questa viene comunicata anche al
     * PlayerController.
     */
    private void configureTrackLibraryTable() {
        trackTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        trackTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldTrack, newTrack) -> {
                    if (playerControlController != null) {
                        playerControlController.setSelectedTrack(newTrack);
                        playerControlController.setCurrentPlaylist(trackTableView.getItems());
                    }

                    if (statusLabel != null && newTrack != null) {
                        statusLabel.setText(
                                "Traccia selezionata: "
                                        + newTrack.getTitle()
                                        + " - "
                                        + newTrack.getAuthor()
                        );
                    }
                });

        trackTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        trackAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        trackLengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        trackGenreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        trackYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        trackPlayCountColumn.setCellValueFactory(new PropertyValueFactory<>("playedCount"));

        // Configurazione della colonna dei Tag
        trackTagsColumn.setCellValueFactory(cellData -> {
            Set<Tag> tags = cellData.getValue().getTags();

            if (tags == null || tags.isEmpty()) {
                return new SimpleStringProperty("");
            }

            String tagsString = tags.stream()
                    .map(Tag::name)
                    .collect(Collectors.joining(", "));

            return new SimpleStringProperty(tagsString);
        });

        // Inizializza la FilteredList (di default mostra tutte le tracce)
        filteredTracks = new FilteredList<>(tracks, p -> true);

        // Wrapper SortedList per mantenere l'ordinamento cliccando sulle colonne
        SortedList<Track> sortedTracks = new SortedList<>(filteredTracks);
        sortedTracks.comparatorProperty().bind(trackTableView.comparatorProperty());

        // Imposta la lista filtrata e ordinata nella tabella
        trackTableView.setItems(sortedTracks);

        trackTableView.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(Track track, boolean empty) {
                super.updateItem(track, empty);
                if (empty || track == null || playerControlController == null) {
                    setStyle("");
                    return;
                }
                Track currentTrack = playerControlController.getCurrentTrack();
                if (currentTrack != null && currentTrack.equals(track) && playerControlController.isPlaying()) {
                    setStyle("-fx-font-weight: bold; -fx-background-color: #fff3b0;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    /**
     * Gestisce la pulizia del campo di ricerca globale.
     *
     * La logica completa di ricerca/filtro è predisposta per una User Story
     * successiva.
     */
    @FXML
    private void handleClearSearch() {
        System.out.println("Clear search");
    }

    /**
     * Delega al PlaylistController la creazione di una nuova playlist.
     */
    @FXML
    private void handleNewPlaylist() {
        playlistSectionController.createPlaylist();
    }

    /**
     * Delega al PlaylistController la rinomina della playlist selezionata.
     */
    @FXML
    private void handleRenamePlaylist() {
        playlistSectionController.renamePlaylist();
    }

    /**
     * Gestisce l'eliminazione temporanea della playlist selezionata.
     * <p>
     * La playlist non viene cancellata definitivamente subito: viene rimossa
     * dalla sidebar e salvata temporaneamente dal service delle playlist,
     * così da poter essere ripristinata tramite il pulsante "Annulla"
     * dello snackbar.
     * </p>
     */
    @FXML
    private void handleDeletePlaylist() {
        Playlist selectedPlaylist = playlistSectionController.getSelectedPlaylist();

        if (selectedPlaylist == null) {
            showError("Seleziona una playlist da eliminare.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete playlist");
        confirmationAlert.setHeaderText("Conferma eliminazione");
        confirmationAlert.setContentText(
                "Vuoi eliminare la playlist \""
                        + selectedPlaylist.getName()
                        + "\"?"
        );

        ButtonType cancelButton = new ButtonType("Cancel");
        ButtonType deleteButton = new ButtonType("Delete");

        confirmationAlert.getButtonTypes().setAll(cancelButton, deleteButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isEmpty() || result.get() == cancelButton) {
            if (statusLabel != null) {
                statusLabel.setText("Eliminazione playlist annullata.");
            }
            return;
        }

        softDeletePlaylist(selectedPlaylist);

        showUndoSnackbar("Elemento rimosso");

        if (statusLabel != null) {
            statusLabel.setText("Playlist rimossa temporaneamente: " + selectedPlaylist.getName());
        }
    }

    @FXML
    private void handleGenerateByGenre() {
        java.util.List<String> uniqueGenres = tracks.stream()
                .map(Track::getGenre)
                .filter(g -> g != null && !g.trim().isEmpty())
                .map(g -> g.trim().toLowerCase())
                .map(g -> g.substring(0, 1).toUpperCase() + g.substring(1)) // Capitalizza la prima lettera
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (uniqueGenres.isEmpty()) {
            showError("Nessun genere presente nella libreria.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(uniqueGenres.get(0), uniqueGenres);
        dialog.setTitle("Generate Smart Playlist");
        dialog.setHeaderText("Filtra per Genere");
        dialog.setContentText("Seleziona il genere desiderato:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Playlist generated = playlistService.generatePlaylistByGenre(playlists, tracks, result.get());
                playlistListView.getSelectionModel().select(generated);
                if (statusLabel != null) statusLabel.setText("Smart playlist creata: " + generated.getName());
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        }
    }

    @FXML
    private void handleGenerateByYear() {
        java.util.List<Integer> uniqueYears = tracks.stream()
                .map(Track::getYear)
                .filter(y -> y != null && y > 0)
                .distinct()
                .sorted(java.util.Comparator.reverseOrder()) 
                .collect(Collectors.toList());

        if (uniqueYears.isEmpty()) {
            showError("Nessun anno presente nella libreria.");
            return;
        }

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(uniqueYears.get(0), uniqueYears);
        dialog.setTitle("Generate Smart Playlist");
        dialog.setHeaderText("Filtra per Anno");
        dialog.setContentText("Seleziona l'anno desiderato:");

        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Playlist generated = playlistService.generatePlaylistByYear(playlists, tracks, result.get());
                playlistListView.getSelectionModel().select(generated);
                if (statusLabel != null) statusLabel.setText("Smart playlist creata: " + generated.getName());
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        }
    }

    /**
     * Genera una playlist automatica filtrando per tag.
     * Mostra un ChoiceDialog per far scegliere il tag all'utente.
     */
    @FXML
    private void handleGenerateByTag() {

        ChoiceDialog<Tag> dialog = new ChoiceDialog<>(Tag.FAV, Tag.values());
        dialog.setTitle("Generate Smart Playlist");
        dialog.setHeaderText("Genera una playlist casuale");
        dialog.setContentText("Seleziona il Tag desiderato:");

        Optional<Tag> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {

                Playlist generated = playlistService.generatePlaylistByTag(playlists, tracks, result.get());

                playlistListView.getSelectionModel().select(generated);

                if (statusLabel != null) {
                    statusLabel.setText("Smart playlist creata: " + generated.getName());
                }
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        }
    }

    /**
     * Apre la schermata Add Track e aggiunge al catalogo la traccia creata.
     *
     * La form viene gestita da AddTrackController, mentre l'aggiunta al catalogo
     * viene delegata a TrackService.
     */
    @FXML
    private void handleAddTrack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/musicplayer/view/AddTrackView.fxml")
            );

            Parent root = loader.load();
            AddTrackController controller = loader.getController();

            Stage stage = createModalStage("Add track", root);
            stage.showAndWait();

            Track createdTrack = controller.getCreatedTrack();

            if (createdTrack != null) {
                trackService.addTrack(tracks, createdTrack);
                trackTableView.getSelectionModel().select(createdTrack);

                if (statusLabel != null) {
                    statusLabel.setText("Traccia aggiunta: " + createdTrack.getTitle());
                }
            }

        } catch (IOException exception) {
            exception.printStackTrace();
            showError("Impossibile aprire la schermata di aggiunta traccia.");
        }
    }

    /**
     * Apre AddTrackView in modalità modifica.
     *
     * La stessa view viene riutilizzata sia per Add Track sia per Edit Track,
     * rispettando il principio DRY. Al salvataggio vengono aggiornati solo i
     * campi modificabili della traccia tramite TrackService.
     */
    @FXML
    private void handleEditTrack() {
        Track selectedTrack = getSelectedTrackFromLibrary();

        if (selectedTrack == null) {
            showError("Seleziona una traccia da modificare.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/musicplayer/view/AddTrackView.fxml")
            );

            Parent root = loader.load();

            AddTrackController controller = loader.getController();
            controller.setTrackToEdit(selectedTrack);

            Stage stage = createModalStage("Edit track", root);
            stage.showAndWait();

            Track editedTrack = controller.getCreatedTrack();

            if (editedTrack != null) {
                trackService.updateEditableFields(selectedTrack, editedTrack);

                trackTableView.refresh();
                playlistTrackTableView.refresh();

                if (statusLabel != null) {
                    statusLabel.setText("Traccia modificata: " + selectedTrack.getTitle());
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            showError("Impossibile aprire la schermata di modifica traccia.");
        }
    }

    /**
     * Crea uno stage modale riutilizzabile per le schermate secondarie.
     *
     * @param title titolo della finestra
     * @param root nodo radice della scena
     * @return stage configurato
     */
    private Stage createModalStage(String title, Parent root) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(trackTableView.getScene().getWindow());
        stage.setResizable(false);
        return stage;
    }

    /**
     * Gestisce l'eliminazione di una traccia dal catalogo principale.
     *
     * Il controller mostra il pop-up di conferma, poi delega la rimozione a
     * TrackService. Dopo la rimozione aggiorna anche la sezione playlist tramite
     * PlaylistController.
     */
    @FXML
    private void handleDeleteTrack() {
        Track selectedTrack = getSelectedTrackFromLibrary();

        if (selectedTrack == null) {
            showError("Seleziona una traccia da eliminare.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete track");
        confirmationAlert.setHeaderText("Conferma eliminazione");
        confirmationAlert.setContentText(
                "Vuoi eliminare definitivamente la traccia \""
                        + selectedTrack.getTitle()
                        + "\" dalla libreria?"
        );

        ButtonType cancelButton = new ButtonType("Cancel");
        ButtonType deleteButton = new ButtonType("Delete");

        confirmationAlert.getButtonTypes().setAll(cancelButton, deleteButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isEmpty() || result.get() == cancelButton) {
            if (statusLabel != null) {
                statusLabel.setText("Eliminazione annullata.");
            }
            return;
        }

        softDeleteTrackFromCatalog(selectedTrack);
        showUndoSnackbar("Elemento rimosso");

        if (statusLabel != null) {
            statusLabel.setText("Traccia eliminata: " + selectedTrack.getTitle());
        }
    }

    /**
     * Rimuove una traccia dal catalogo principale e da tutte le playlist.
     *
     * La logica di rimozione viene delegata a TrackService.
     * L'aggiornamento della tabella della playlist selezionata viene delegato
     * a PlaylistController.
     *
     * @param track traccia da eliminare
     */
    private void removeTrackFromCatalog(Track track) {
        if (track == null) {
            return;
        }

        trackService.removeTrack(tracks, playlists, track);

        trackTableView.getSelectionModel().clearSelection();

        Playlist selectedPlaylist = playlistSectionController.getSelectedPlaylist();
        playlistSectionController.updateSelectedPlaylistView(selectedPlaylist);
    }

    /**
     * Rimuove temporaneamente una traccia dal catalogo visibile.
     * <p>
     * Questo metodo viene usato nella logica di soft delete: quando l'utente
     * conferma l'eliminazione, la traccia viene rimossa dalla Track Library
     * mostrata nell'interfaccia, ma non viene ancora cancellata definitivamente
     * dal sistema.
     * </p>
     * <p>
     * La rimozione temporanea viene delegata al service delle tracce, che conserva
     * la traccia e la sua posizione originale in memoria temporanea. In questo modo
     * l'elemento potrà essere ripristinato se l'utente preme "Annulla" nello
     * snackbar, oppure eliminato definitivamente quando il timer dello snackbar
     * scade.
     * </p>
     * <p>
     * Dopo la rimozione visiva, il metodo pulisce la selezione della tabella,
     * aggiorna la Track Library e sincronizza la vista della playlist selezionata.
     * </p>
     *
     * @param track traccia da rimuovere temporaneamente dal catalogo visibile
     */
    private void softDeleteTrackFromCatalog(Track track) {
        if (track == null) {
            return;
        }

        trackService.softDeleteTrack(tracks, track);
        playlistService.softDeleteTrackFromPlaylists(playlists, track);
        stopPlaybackIfRemovedTrackIsCurrent(track);

        trackTableView.getSelectionModel().clearSelection();
        trackTableView.refresh();

        Playlist selectedPlaylist = playlistSectionController.getSelectedPlaylist();
        playlistSectionController.updateSelectedPlaylistView(selectedPlaylist);
    }

    /**
     * Rimuove temporaneamente una playlist dalla lista visibile.
     * <p>
     * La playlist sparisce dalla sidebar, ma non viene cancellata definitivamente.
     * La rimozione temporanea viene delegata al service delle playlist, che
     * conserva la playlist e la sua posizione originale in memoria temporanea.
     * </p>
     * <p>
     * Poiché viene mantenuto l'oggetto Playlist completo, se l'utente preme
     * "Annulla" verranno ripristinate anche tutte le tracce contenute nella
     * playlist al momento dell'eliminazione.
     * </p>
     *
     * @param playlist playlist da rimuovere temporaneamente
     */
    private void softDeletePlaylist(Playlist playlist) {
        if (playlist == null) {
            return;
        }

        playlistService.softDeletePlaylist(playlists, playlist);

        playlistListView.getSelectionModel().clearSelection();
        playlistSectionController.updateSelectedPlaylistView(null);
    }

    /**
     * Interrompe la riproduzione se la traccia rimossa è quella attualmente
     * in esecuzione nel player.
     * <p>
     * Quando una traccia viene rimossa dalla libreria tramite soft delete,
     * non deve continuare a essere riprodotta se era la traccia corrente.
     * Il controllo viene delegato al controller del player, che conosce lo
     * stato della riproduzione e può aggiornare correttamente la propria UI.
     * </p>
     *
     * @param track traccia rimossa temporaneamente
     */
    private void stopPlaybackIfRemovedTrackIsCurrent(Track track) {
        if (playerControlController == null || track == null) {
            return;
        }

        playerControlController.stopPlaybackIfCurrentTrackWasRemoved(track);
    }

    /**
     * Ripristina nel catalogo visibile la traccia rimossa temporaneamente.
     * <p>
     * Il metodo viene invocato quando l'utente preme il pulsante "Annulla"
     * nello snackbar. Il ripristino della traccia nella Track Library viene
     * delegato al service delle tracce, mentre il ripristino nelle eventuali
     * playlist viene delegato al service delle playlist.
     * </p>
     * <p>
     * Dopo il ripristino, la tabella principale viene aggiornata, la traccia
     * ripristinata viene selezionata nuovamente e la vista della playlist
     * selezionata viene sincronizzata.
     * </p>
     */
    private void restorePendingDeletedTrackFromCatalog() {
        if (!trackService.hasPendingDeletedTrack()) {
            return;
        }

        Track restoredTrack = trackService.getPendingDeletedTrack();

        trackService.restorePendingDeletedTrack(tracks);
        playlistService.restorePendingDeletedTrackInPlaylists(restoredTrack);

        trackTableView.refresh();
        trackTableView.getSelectionModel().select(restoredTrack);

        Playlist selectedPlaylist = playlistSectionController.getSelectedPlaylist();
        playlistSectionController.updateSelectedPlaylistView(selectedPlaylist);

        if (statusLabel != null) {
            statusLabel.setText("Eliminazione annullata: " + restoredTrack.getTitle());
        }
    }

    /**
     * Ripristina l'eventuale elemento rimosso temporaneamente.
     * <p>
     * Lo snackbar è condiviso tra eliminazione di tracce ed eliminazione di
     * playlist. Per questo motivo il metodo controlla quale tipo di elemento
     * è attualmente in attesa di annullamento e invoca il ripristino corretto.
     * </p>
     */
    private void restorePendingDeletion() {
        if (trackService.hasPendingDeletedTrack()) {
            restorePendingDeletedTrackFromCatalog();
            return;
        }

        if (playlistService.hasPendingDeletedPlaylist()) {
            restorePendingDeletedPlaylist();
        }
    }

    /**
     * Ripristina nella sidebar la playlist rimossa temporaneamente.
     * <p>
     * Il metodo viene invocato quando l'utente preme "Annulla" dopo aver
     * eliminato una playlist. Il ripristino viene delegato al service delle
     * playlist, poi la playlist ripristinata viene selezionata e la vista
     * centrale viene aggiornata.
     * </p>
     * <p>
     * La playlist viene ripristinata come oggetto completo, quindi mantiene
     * anche tutte le tracce che conteneva prima dell'eliminazione.
     * </p>
     */
    private void restorePendingDeletedPlaylist() {
        if (!playlistService.hasPendingDeletedPlaylist()) {
            return;
        }

        Playlist restoredPlaylist = playlistService.getPendingDeletedPlaylist();

        playlistService.restorePendingDeletedPlaylist(playlists);

        playlistListView.getSelectionModel().select(restoredPlaylist);
        playlistSectionController.updateSelectedPlaylistView(restoredPlaylist);

        if (statusLabel != null) {
            statusLabel.setText("Eliminazione playlist annullata: " + restoredPlaylist.getName());
        }
    }

    /**
     * Conferma definitivamente l'eliminazione temporanea quando scade lo snackbar.
     * <p>
     * Se l'utente non preme "Annulla" entro il tempo disponibile, l'elemento
     * rimosso temporaneamente viene considerato eliminato in modo definitivo.
     * L'elemento può essere una traccia oppure una playlist.
     * </p>
     */
    private void confirmPendingDeletion() {
        if (trackService.hasPendingDeletedTrack()) {
            Track deletedTrack = trackService.getPendingDeletedTrack();

            trackService.clearPendingDeletedTrack();
            playlistService.clearPendingDeletedTrackFromPlaylists();

            hideUndoSnackbar();

            if (statusLabel != null) {
                statusLabel.setText("Traccia eliminata definitivamente: " + deletedTrack.getTitle());
            }

            return;
        }

        if (playlistService.hasPendingDeletedPlaylist()) {
            Playlist deletedPlaylist = playlistService.getPendingDeletedPlaylist();

            playlistService.clearPendingDeletedPlaylist();

            hideUndoSnackbar();

            if (statusLabel != null) {
                statusLabel.setText("Playlist eliminata definitivamente: " + deletedPlaylist.getName());
            }

            return;
        }

        hideUndoSnackbar();
    }

    /**
     * Restituisce la traccia selezionata nella Track Library.
     *
     * @return traccia selezionata, oppure null
     */
    private Track getSelectedTrackFromLibrary() {
        return trackTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Delega al PlaylistController l'aggiunta della traccia selezionata
     * alla playlist selezionata.
     */
    @FXML
    private void handleAddToPlaylist() {
        playlistSectionController.addSelectedTrackToPlaylist();
    }

    /**
     * Delega al PlaylistController la rimozione della traccia selezionata
     * dalla playlist selezionata.
     */
    @FXML
    private void handleRemoveFromPlaylist() {
        playlistSectionController.removeSelectedTrackFromPlaylist();
    }

    /**
     * Sposta verso l'alto una traccia nella playlist.
     *
     * Funzionalità predisposta per una User Story successiva.
     */
    @FXML
    private void handleMoveTrackUp() {
        System.out.println("Move track up");
    }

    /**
     * Sposta verso il basso una traccia nella playlist.
     *
     * Funzionalità predisposta per una User Story successiva.
     */
    @FXML
    private void handleMoveTrackDown() {
        System.out.println("Move track down");
    }

    /**
     * Applica i filtri avanzati alla libreria usando il Pattern Strategy.
     */
    @FXML
    private void handleApplyFilters() {
        Set<Tag> selectedTags = new HashSet<>();

        if (favouriteTagCheckBox.isSelected()) selectedTags.add(Tag.FAV);
        if (explicitTagCheckBox.isSelected()) selectedTags.add(Tag.EXPLICIT);
        if (newReleaseTagCheckBox.isSelected()) selectedTags.add(Tag.NEW);

        // Applica il Pattern Strategy
        TrackFilterStrategy strategy = new TagFilterStrategy(selectedTags);

        // Applica il filtro alla lista visibile
        filteredTracks.setPredicate(track -> strategy.matches(track));

        if (statusLabel != null) {
            statusLabel.setText("Filtri applicati. Mostrate solo le tracce corrispondenti.");
        }
    }

    /**
     * Ripristina i filtri avanzati.
     */
    @FXML
    private void handleResetFilters() {
        // Deseleziona le checkbox
        if (favouriteTagCheckBox != null) favouriteTagCheckBox.setSelected(false);
        if (explicitTagCheckBox != null) explicitTagCheckBox.setSelected(false);
        if (newReleaseTagCheckBox != null) newReleaseTagCheckBox.setSelected(false);

        // Rimuove il filtro (mostra tutto)
        filteredTracks.setPredicate(p -> true);

        if (statusLabel != null) {
            statusLabel.setText("Filtri rimossi. Mostro tutte le tracce.");
        }
    }
    
    @FXML
    private void handleUndo() {
        System.out.println("Undo");
    }

    /**
     * Esegue il redo dell'ultima operazione annullata.
     *
     * Il CommandManager è già presente, ma il collegamento completo con la UI
     * verrà completato nelle User Story dedicate.
     */
    @FXML
    private void handleRedo() {
        System.out.println("Redo");
    }

    /**
     * Mostra un messaggio di errore.
     *
     * @param message messaggio da visualizzare
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Mostra il banner temporaneo di annullamento dopo un'eliminazione.
     * <p>
     * Il banner viene visualizzato nella parte inferiore della schermata
     * con un messaggio informativo e un pulsante "Annulla".
     * Dopo alcuni secondi scompare automaticamente. Se l'utente non preme
     * "Annulla" entro questo intervallo, l'eliminazione temporanea viene
     * confermata definitivamente.
     * </p>
     *
     * @param message messaggio da mostrare nel banner
     */
    private void showUndoSnackbar(String message) {
        if (undoSnackbar == null || snackbarMessageLabel == null) {
            return;
        }

        snackbarMessageLabel.setText(message);

        undoSnackbar.setVisible(true);
        undoSnackbar.setManaged(true);

        if (snackbarTimer != null) {
            snackbarTimer.stop();
        }

        snackbarTimer = new PauseTransition(Duration.seconds(5));
        snackbarTimer.setOnFinished(event -> confirmPendingDeletion());
        snackbarTimer.playFromStart();
    }

    /**
     * Nasconde il banner temporaneo di annullamento.
     */
    private void hideUndoSnackbar() {
        if (undoSnackbar != null) {
            undoSnackbar.setVisible(false);
            undoSnackbar.setManaged(false);
        }
    }

    /**
     * Gestisce il click sul pulsante "Annulla" del banner.
     * <p>
     * Quando l'utente preme "Annulla", il timer dello snackbar viene fermato
     * e l'eventuale elemento rimosso temporaneamente viene ripristinato.
     * Lo snackbar è condiviso sia per l'eliminazione delle tracce sia per
     * l'eliminazione delle playlist.
     * </p>
     */
    @FXML
    private void handleSnackbarUndo() {
        if (snackbarTimer != null) {
            snackbarTimer.stop();
        }

        restorePendingDeletion();

        hideUndoSnackbar();
    }


}