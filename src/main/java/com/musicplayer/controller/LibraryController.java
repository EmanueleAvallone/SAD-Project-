package com.musicplayer.controller;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Tag;
import com.musicplayer.model.Track;
import com.musicplayer.model.filter.TagFilterStrategy;
import com.musicplayer.model.filter.TrackFilterStrategy;
import com.musicplayer.service.PlaylistService;
import com.musicplayer.service.SearchService;
import com.musicplayer.service.TrackService;
import com.musicplayer.service.sort.AuthorSortStrategy;
import com.musicplayer.service.sort.LengthSortStrategy;
import com.musicplayer.service.sort.PlayedCountSortStrategy;
import com.musicplayer.service.sort.TitleSortStrategy;
import com.musicplayer.service.sort.TrackSortStrategy;
import com.musicplayer.service.sort.YearSortStrategy;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Controller di sezione dedicato alla Track Library.
 *
 * Questa classe gestisce la parte centrale e destra della schermata relativa a:
 * - Track Library;
 * - add/edit/delete track;
 * - filtri avanzati;
 * - sezione Most Played.
 *
 * La business logic non viene implementata direttamente qui, ma delegata a:
 * - TrackService per le operazioni sulle tracce;
 * - PlaylistService per la sincronizzazione delle tracce nelle playlist.
 *
 * In questo modo MainController resta un coordinatore generale e questa classe
 * mantiene una responsabilità più specifica.
 */
public class LibraryController {

    /**
     * Interfaccia funzionale usata per richiedere al MainController
     * la visualizzazione dello snackbar globale.
     */
    @FunctionalInterface
    public interface UndoSnackbarHandler {

        /**
         * Mostra lo snackbar con azione di undo e azione di conferma.
         *
         * @param message messaggio da visualizzare
         * @param undoAction azione da eseguire se l'utente annulla
         * @param confirmAction azione da eseguire se lo snackbar scade
         */
        void showUndoSnackbar(String message, Runnable undoAction, Runnable confirmAction);
    }

    private TableView<Track> trackTableView;
    private TableColumn<Track, String> trackTitleColumn;
    private TableColumn<Track, String> trackAuthorColumn;
    private TableColumn<Track, String> trackLengthColumn;
    private TableColumn<Track, String> trackGenreColumn;
    private TableColumn<Track, Integer> trackYearColumn;
    private TableColumn<Track, String> trackTagsColumn;
    private TableColumn<Track, Integer> trackPlayCountColumn;
    private SearchService searchService;
    private CheckBox favouriteTagCheckBox;
    private CheckBox explicitTagCheckBox;
    private CheckBox newReleaseTagCheckBox;

    private ScrollPane mostPlayedScrollPane;
    private VBox topTracksContainer;
    private VBox emptyMostPlayedView;

    private Label statusLabel;

    private TrackService trackService;
    private PlaylistService playlistService;

    private ObservableList<Track> tracks;
    private ObservableList<Playlist> playlists;

    private PlayerController playerController;
    private PlaylistController playlistController;

    private UndoSnackbarHandler undoSnackbarHandler;

    private FilteredList<Track> filteredTracks;
    private SortedList<Track> sortedTracks;

    private final TrackSortStrategy titleSortStrategy = new TitleSortStrategy();
    private final TrackSortStrategy authorSortStrategy = new AuthorSortStrategy();
    private final TrackSortStrategy lengthSortStrategy = new LengthSortStrategy();
    private final TrackSortStrategy yearSortStrategy = new YearSortStrategy();
    private final TrackSortStrategy playedCountSortStrategy = new PlayedCountSortStrategy();

    private TableColumn<Track, ?> activeTrackLibrarySortColumn;

    private boolean trackLibrarySortAscending = true;

    private String currentSearchQuery = "";

    private int lastTotalPlays = -1;

    private ObservableList<Track> trashList;

    /**
     * Inizializza la sezione Track Library.
     */
    public void initializeSection(
            TableView<Track> trackTableView,
            TableColumn<Track, String> trackTitleColumn,
            TableColumn<Track, String> trackAuthorColumn,
            TableColumn<Track, String> trackLengthColumn,
            TableColumn<Track, String> trackGenreColumn,
            TableColumn<Track, Integer> trackYearColumn,
            TableColumn<Track, String> trackTagsColumn,
            TableColumn<Track, Integer> trackPlayCountColumn,
            CheckBox favouriteTagCheckBox,
            CheckBox explicitTagCheckBox,
            CheckBox newReleaseTagCheckBox,
            ScrollPane mostPlayedScrollPane,
            VBox topTracksContainer,
            VBox emptyMostPlayedView,
            Label statusLabel,
            TrackService trackService,
            PlaylistService playlistService,
            ObservableList<Track> tracks,
            ObservableList<Playlist> playlists,
            ObservableList<Track> trashList,
            PlayerController playerController,
            PlaylistController playlistController,
            UndoSnackbarHandler undoSnackbarHandler,
            SearchService searchService) {
        this.trackTableView = trackTableView;
        this.trackTitleColumn = trackTitleColumn;
        this.trackAuthorColumn = trackAuthorColumn;
        this.trackLengthColumn = trackLengthColumn;
        this.trackGenreColumn = trackGenreColumn;
        this.trackYearColumn = trackYearColumn;
        this.trackTagsColumn = trackTagsColumn;
        this.trackPlayCountColumn = trackPlayCountColumn;

        this.favouriteTagCheckBox = favouriteTagCheckBox;
        this.explicitTagCheckBox = explicitTagCheckBox;
        this.newReleaseTagCheckBox = newReleaseTagCheckBox;

        this.mostPlayedScrollPane = mostPlayedScrollPane;
        this.topTracksContainer = topTracksContainer;
        this.emptyMostPlayedView = emptyMostPlayedView;

        this.statusLabel = statusLabel;

        this.trackService = trackService;
        this.playlistService = playlistService;

        this.tracks = tracks;
        this.playlists = playlists;

        this.playerController = playerController;
        this.playlistController = playlistController;

        this.searchService = searchService;

        this.undoSnackbarHandler = undoSnackbarHandler;

        this.trashList = trashList;

        configureTrackLibraryTable();
        updateMostPlayedSection();
    }

    /**
     * Configura la tabella principale della Track Library.
     */
    private void configureTrackLibraryTable() {
        trackTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        trackTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldTrack, newTrack) -> {
                    if (playerController != null) {
                        playerController.setSelectedTrack(newTrack);
                        playerController.setCurrentPlaylist(trackTableView.getItems());
                    }

                    if (newTrack != null) {
                        setStatus(
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

        if (trackPlayCountColumn != null) {
            trackPlayCountColumn.setCellValueFactory(new PropertyValueFactory<>("playedCount"));
        }

        if (trackTagsColumn != null) {
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
        }

        filteredTracks = new FilteredList<>(tracks, track -> true);

        sortedTracks = new SortedList<>(filteredTracks);

        trackTableView.setItems(sortedTracks);

        configureTrackLibrarySorting();
        configurePlayingTrackStyle();
    }


    /**
     * Configura l'ordinamento locale della Track Library.
     * <p>
     * Solo alcune colonne sono ordinabili: titolo, autore, durata, anno
     * e numero di riproduzioni. Le colonne genere e tag vengono escluse
     * perché sono usate da altre funzionalità, come filtri e smart playlist.
     * </p>
     * <p>
     * L'ordinamento viene applicato alla SortedList usata dalla tabella,
     * quindi modifica solo l'ordine visualizzato nella Track Library senza
     * alterare l'ordine salvato nel Model.
     * </p>
     */
    private void configureTrackLibrarySorting() {
        trackTitleColumn.setSortable(true);
        trackAuthorColumn.setSortable(true);
        trackLengthColumn.setSortable(true);
        trackYearColumn.setSortable(true);

        if (trackPlayCountColumn != null) {
            trackPlayCountColumn.setSortable(true);
        }

        trackGenreColumn.setSortable(true);

        if (trackTagsColumn != null) {
            trackTagsColumn.setSortable(true);
        }

        trackTableView.setSortPolicy(tableView -> {
            if (sortedTracks == null) {
                return true;
            }

            if (tableView.getSortOrder().isEmpty()) {
                sortedTracks.setComparator(null);
                activeTrackLibrarySortColumn = null;
                trackLibrarySortAscending = true;
                setStatus("Ordinamento Track Library rimosso: ordine originale ripristinato.");
                return true;
            }

            TableColumn<Track, ?> sortColumn = tableView.getSortOrder().get(0);
            TrackSortStrategy sortStrategy = getTrackLibrarySortStrategy(sortColumn);

            if (sortStrategy == null) {
                tableView.getSortOrder().clear();

                sortedTracks.setComparator(null);
                activeTrackLibrarySortColumn = null;
                trackLibrarySortAscending = true;

                setStatus(
                        "La colonna "
                                + getTrackLibrarySortLabel(sortColumn)
                                + " non è ordinabile."

                );

                return true;
            }

            activeTrackLibrarySortColumn = sortColumn;
            trackLibrarySortAscending = sortColumn.getSortType() != TableColumn.SortType.DESCENDING;

            Comparator<Track> comparator = sortStrategy.getComparator();

            if (!trackLibrarySortAscending) {
                comparator = comparator.reversed();
            }

            sortedTracks.setComparator(comparator);

            setStatus(
                    "Track Library ordinata per "
                            + getTrackLibrarySortLabel(sortColumn)
                            + " in ordine "
                            + (trackLibrarySortAscending ? "crescente." : "decrescente.")
            );

            return true;
        });
    }


    /**
     * Restituisce la strategia di ordinamento associata alla colonna selezionata
     * nella Track Library.
     *
     * @param sortColumn colonna cliccata dall'utente
     * @return strategia di ordinamento corrispondente, oppure null se la colonna
     *         non è ordinabile
     */
    private TrackSortStrategy getTrackLibrarySortStrategy(TableColumn<Track, ?> sortColumn) {
        if (sortColumn == trackTitleColumn) {
            return titleSortStrategy;
        }

        if (sortColumn == trackAuthorColumn) {
            return authorSortStrategy;
        }

        if (sortColumn == trackLengthColumn) {
            return lengthSortStrategy;
        }

        if (sortColumn == trackYearColumn) {
            return yearSortStrategy;
        }

        if (sortColumn == trackPlayCountColumn) {
            return playedCountSortStrategy;
        }

        return null;
    }

    /**
     * Restituisce l'etichetta testuale della colonna usata per ordinare
     * la Track Library.
     *
     * @param sortColumn colonna selezionata per l'ordinamento
     * @return nome leggibile del criterio di ordinamento
     */
    private String getTrackLibrarySortLabel(TableColumn<Track, ?> sortColumn) {
        if (sortColumn == trackTitleColumn) {
            return "titolo";
        }

        if (sortColumn == trackAuthorColumn) {
            return "autore";
        }

        if (sortColumn == trackLengthColumn) {
            return "durata";
        }

        if (sortColumn == trackYearColumn) {
            return "anno";
        }

        if (sortColumn == trackPlayCountColumn) {
            return "numero di riproduzioni";
        }

        if (sortColumn == trackGenreColumn) {
            return "genere";
        }

        if (sortColumn == trackTagsColumn) {
            return "tag";
        }

        return "metadato";
    }


    /**
     * Configura l'evidenziazione grafica della traccia in riproduzione.
     *
     * Il controller aggiunge solo una classe CSS; lo stile effettivo viene
     * deciso dal file CSS, separando logica e presentazione.
     */
    private void configurePlayingTrackStyle() {
        trackTableView.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(Track track, boolean empty) {
                super.updateItem(track, empty);

                getStyleClass().remove("playing-track");

                if (empty || track == null || playerController == null) {
                    return;
                }

                Track currentTrack = playerController.getCurrentTrack();

                if (currentTrack != null
                        && currentTrack.equals(track)
                        && playerController.isPlaying()) {

                    if (!getStyleClass().contains("playing-track")) {
                        getStyleClass().add("playing-track");
                    }
                }
            }
        });
    }

    /**
     * Apre la schermata Add Track e aggiunge la nuova traccia al catalogo.
     */
    public void addTrack() {
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
                updateMostPlayedSection();

                setStatus("Traccia aggiunta: " + createdTrack.getTitle());
            }

        } catch (IOException exception) {
            exception.printStackTrace();
            showError("Impossibile aprire la schermata di aggiunta traccia.");
        }
    }

    /**
     * Apre la schermata Add Track in modalità modifica.
     *
     * La stessa view viene riutilizzata per evitare duplicazione dell'interfaccia.
     */
    public void editSelectedTrack() {
        Track selectedTrack = getSelectedTrack();

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

                playlistService.refreshSmartPlaylists(playlists, selectedTrack);
                refreshTrackLibrary();

                if (playlistController != null) {
                    Playlist selectedPlaylist = playlistController.getSelectedPlaylist();
                    playlistController.updateSelectedPlaylistView(selectedPlaylist);
                }

                updateMostPlayedSection();

                setStatus("Traccia modificata: " + selectedTrack.getTitle());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            showError("Impossibile aprire la schermata di modifica traccia.");
        }
    }

    /**
     * Avvia il flusso di eliminazione temporanea della traccia selezionata.
     */
    public void deleteSelectedTrack() {
        Track selectedTrack = getSelectedTrack();

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

        StyleManager.applyToDialog(confirmationAlert);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isEmpty() || result.get() == cancelButton) {
            setStatus("Eliminazione annullata.");
            return;
        }

        softDeleteTrackFromCatalog(selectedTrack);

        if (undoSnackbarHandler != null) {
            undoSnackbarHandler.showUndoSnackbar(
                    "Traccia rimossa",
                    this::restorePendingDeletedTrackFromCatalog,
                    this::confirmPendingDeletedTrack
            );
        }

        setStatus("Traccia rimossa temporaneamente: " + selectedTrack.getTitle());
    }

    /**
     * Rimuove temporaneamente una traccia dal catalogo visibile e dalle playlist.
     *
     * La rimozione effettiva viene delegata ai service.
     */
    private void softDeleteTrackFromCatalog(Track track) {
        if (track == null) {
            return;
        }

        trackService.softDeleteTrack(tracks, track);
        playlistService.softDeleteTrackFromPlaylists(playlists, track);

        stopPlaybackIfRemovedTrackIsCurrent(track);

        trackTableView.getSelectionModel().clearSelection();
        refreshTrackLibrary();

        if (playlistController != null) {
            Playlist selectedPlaylist = playlistController.getSelectedPlaylist();
            playlistController.updateSelectedPlaylistView(selectedPlaylist);
        }

        updateMostPlayedSection();
    }

    /**
     * Ripristina una traccia eliminata temporaneamente.
     */
    private void restorePendingDeletedTrackFromCatalog() {
        if (!trackService.hasPendingDeletedTrack()) {
            return;
        }

        Track restoredTrack = trackService.getPendingDeletedTrack();

        trackService.restorePendingDeletedTrack(tracks);
        playlistService.restorePendingDeletedTrackInPlaylists(restoredTrack);

        refreshTrackLibrary();
        trackTableView.getSelectionModel().select(restoredTrack);

        if (playlistController != null) {
            Playlist selectedPlaylist = playlistController.getSelectedPlaylist();
            playlistController.updateSelectedPlaylistView(selectedPlaylist);
        }

        updateMostPlayedSection();

        setStatus("Eliminazione annullata: " + restoredTrack.getTitle());
    }

    /**
     * Conferma definitivamente l'eliminazione temporanea della traccia.
     */
    private void confirmPendingDeletedTrack() {
        if (!trackService.hasPendingDeletedTrack()) {
            return;
        }

        Track deletedTrack = trackService.getPendingDeletedTrack();

        trackService.moveToTrash(tracks, playlists, trashList, deletedTrack);

        trackService.clearPendingDeletedTrack();
        playlistService.clearPendingDeletedTrackFromPlaylists();

        if (playerController != null) {
            playerController.stopPlaybackIfCurrentTrackWasRemoved(deletedTrack);
        }

        if (playlistController != null) {
            playlistController.refreshSelectedPlaylistTable();
        }

        setStatus("Track moved to Trash: " + deletedTrack.getTitle());
    }

    /**
     * Interrompe la riproduzione se la traccia eliminata è quella corrente.
     */
    private void stopPlaybackIfRemovedTrackIsCurrent(Track track) {
        if (playerController == null || track == null) {
            return;
        }

        playerController.stopPlaybackIfCurrentTrackWasRemoved(track);
    }

    /**
     * Applica i filtri avanzati usando il Pattern Strategy.
     */
    public void applyFilters() {
        updateCombinedFilter();

        setStatus("Filtri applicati.");
    }

    /**
     * Rimuove tutti i filtri applicati alla Track Library.
     */
    public void resetFilters() {
        if (favouriteTagCheckBox != null) {
            favouriteTagCheckBox.setSelected(false);
        }

        if (explicitTagCheckBox != null) {
            explicitTagCheckBox.setSelected(false);
        }

        if (newReleaseTagCheckBox != null) {
            newReleaseTagCheckBox.setSelected(false);
        }

        updateCombinedFilter();
        setStatus("Filtri rimossi.");
    }


    /**
     * Aggiorna la sezione Most Played solo se cambia il totale delle riproduzioni.
     */
    public void updateMostPlayedSectionIfNeeded() {
        if (tracks == null) {
            return;
        }

        int currentTotalPlays = tracks.stream()
                .mapToInt(Track::getPlayedCount)
                .sum();

        if (currentTotalPlays != lastTotalPlays) {
            updateMostPlayedSection();
            lastTotalPlays = currentTotalPlays;
        }
    }

    /**
     * Aggiorna la sezione Most Played.
     */
    public void updateMostPlayedSection() {
        if (topTracksContainer == null || trackService == null || tracks == null) {
            return;
        }

        topTracksContainer.getChildren().clear();

        List<Track> topTracks = trackService.getTopPlayedTracks(tracks, 10);

        if (topTracks == null || topTracks.isEmpty()) {
            if (mostPlayedScrollPane != null) {
                mostPlayedScrollPane.setVisible(false);
                mostPlayedScrollPane.setManaged(false);
            }

            if (emptyMostPlayedView != null) {
                emptyMostPlayedView.setVisible(true);
                emptyMostPlayedView.setManaged(true);
            }

            return;
        }

        if (emptyMostPlayedView != null) {
            emptyMostPlayedView.setVisible(false);
            emptyMostPlayedView.setManaged(false);
        }

        if (mostPlayedScrollPane != null) {
            mostPlayedScrollPane.setVisible(true);
            mostPlayedScrollPane.setManaged(true);
        }

        for (Track track : topTracks) {
            topTracksContainer.getChildren().add(createTrackCard(track));
        }
    }

    /**
     * Crea una card grafica per una traccia nella sezione Most Played.
     */
    private HBox createTrackCard(Track track) {
        HBox card = new HBox(10);
        card.setUserData(track);
        card.getStyleClass().add("top-track-card");

        if (isCurrentPlayingTrack(track)) {
            card.getStyleClass().add("playing-track");
        }

        VBox infoBox = new VBox(2);

        Label titleLabel = new Label(track.getTitle());
        titleLabel.getStyleClass().add("top-track-title");

        Label playsLabel = new Label(track.getPlayedCount() + " ascolti");
        playsLabel.getStyleClass().add("top-track-subtitle");

        infoBox.getChildren().addAll(titleLabel, playsLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button playButton = new Button("Play");
        playButton.setOnAction(event -> {
            if (playerController != null) {
                playerController.setCurrentPlaylist(tracks);
                playerController.playTrackFromPlaylist(track);
            }

            updateMostPlayedSection();
        });

        card.getChildren().addAll(infoBox, spacer, playButton);

        return card;
    }

    /**
     * Riproduce in sequenza la classifica Most Played.
     */
    public void playAllMostPlayed() {
        if (playerController == null) {
            return;
        }

        List<Track> topTracks = trackService.getTopPlayedTracks(tracks, 10);

        if (topTracks == null || topTracks.isEmpty()) {
            setStatus("Nessuna traccia disponibile nella classifica.");
            return;
        }

        playerController.setCurrentPlaylist(topTracks);
        playerController.playTrackFromPlaylist(topTracks.get(0));

        setStatus("Riproduzione classifica Most Played avviata.");
    }

    /**
     * Aggiorna l'evidenziazione delle card Most Played.
     */
    public void refreshHighlights() {
        if (topTracksContainer == null) {
            return;
        }

        for (javafx.scene.Node node : topTracksContainer.getChildren()) {
            if (!(node instanceof HBox card)) {
                continue;
            }

            Object userData = card.getUserData();

            if (!(userData instanceof Track track)) {
                continue;
            }

            if (isCurrentPlayingTrack(track)) {
                if (!card.getStyleClass().contains("playing-track")) {
                    card.getStyleClass().add("playing-track");
                }
            } else {
                card.getStyleClass().remove("playing-track");
            }
        }
    }

    /**
     * Aggiorna graficamente la Track Library.
     */
    public void refreshTrackLibrary() {
        if (trackTableView != null) {
            trackTableView.refresh();
        }
    }

    /**
     * Verifica se una traccia coincide con la traccia attualmente in riproduzione.
     */
    private boolean isCurrentPlayingTrack(Track track) {
        return playerController != null
                && playerController.isPlaying()
                && track != null
                && track.equals(playerController.getCurrentTrack());
    }

    /**
     * Restituisce la traccia selezionata nella Track Library.
     */
    private Track getSelectedTrack() {
        return trackTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Crea uno stage modale per Add/Edit Track e applica il CSS globale.
     *
     * @param title titolo della finestra
     * @param root nodo radice caricato da FXML
     * @return stage modale configurato
     */
    private Stage createModalStage(String title, Parent root) {
        Stage stage = new Stage();

        Scene scene = new Scene(root);
        StyleManager.applyToScene(scene);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(trackTableView.getScene().getWindow());
        stage.setResizable(false);

        return stage;
    }

    /**
     * Aggiorna la status label.
     */
    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Mostra un messaggio di errore applicando il CSS globale al popup.
     *
     * @param message messaggio da visualizzare
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);

        StyleManager.applyToDialog(alert);

        alert.showAndWait();
    }
    /**
     * Applica il testo di ricerca corrente al filtro combinato della libreria.
     * <p>
     * Il valore passato viene normalizzato rimuovendo gli spazi iniziali e finali.
     * Dopo l'aggiornamento della query, il metodo ricalcola il predicato combinato
     * che tiene conto sia dei tag selezionati sia della ricerca testuale.
     * </p>
     *
     * @param query testo da usare per la ricerca; se {@code null}, viene trattato
     *              come stringa vuota
     */
    public void applySearch(String query) {
        this.currentSearchQuery = query == null ? "" : query.trim();
        updateCombinedFilter();
    }
    /**
     * Ricalcola il filtro combinato della libreria in base ai tag selezionati
     * e alla query di ricerca corrente.
     * <p>
     * Il filtro risultante mantiene solo le tracce che soddisfano sia i tag
     * selezionati sia il testo di ricerca. Se non sono presenti elementi filtrabili,
     * il metodo termina senza eseguire alcuna operazione.
     * </p>
     * <p>
     * Se non è attivo alcun filtro e la query di ricerca è vuota, viene mostrato
     * un messaggio di stato che indica l'assenza di filtri attivi; negli altri casi
     * viene indicato che filtri e ricerca sono stati applicati.
     * </p>
     */
    private void updateCombinedFilter() {
        if (filteredTracks == null) {
            return;
        }

        Set<Tag> selectedTags = new HashSet<>();

        if (favouriteTagCheckBox != null && favouriteTagCheckBox.isSelected()) {
            selectedTags.add(Tag.FAV);
        }

        if (explicitTagCheckBox != null && explicitTagCheckBox.isSelected()) {
            selectedTags.add(Tag.EXPLICIT);
        }

        if (newReleaseTagCheckBox != null && newReleaseTagCheckBox.isSelected()) {
            selectedTags.add(Tag.NEW);
        }

        TrackFilterStrategy tagStrategy = new TagFilterStrategy(selectedTags);

        filteredTracks.setPredicate(track -> {
            boolean matchesTags = tagStrategy.matches(track);
            boolean matchesSearch = searchService == null
                    || searchService.matchesTrack(track, currentSearchQuery);

            return matchesTags && matchesSearch;
        });

        if (currentSearchQuery.isBlank() && selectedTags.isEmpty()) {
            setStatus("Nessun filtro attivo.");
        } else {
            setStatus("Filtri e ricerca applicati.");
        }
    }
}