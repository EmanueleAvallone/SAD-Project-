
package com.musicplayer.controller;

import com.musicplayer.command.Command;
import com.musicplayer.command.CommandManager;
import com.musicplayer.command.RemoveTrackPCommand;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import com.musicplayer.service.PlaylistService;
import com.musicplayer.service.TrackService;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller principale dell'applicazione Music Playlist Manager.
 *
 * Questa classe coordina la schermata principale e gestisce gli eventi generati
 * dalla UI. Per rispettare la separazione delle responsabilità, la business logic
 * viene delegata ai service:
 * - TrackService per le operazioni sulle tracce;
 * - PlaylistService per le operazioni sulle playlist;
 * - CommandManager per le operazioni annullabili.
 *
 * Il controller si occupa principalmente di:
 * - leggere selezioni da TableView e ListView;
 * - aprire finestre FXML secondarie;
 * - mostrare dialog e messaggi di errore;
 * - aggiornare la UI dopo le operazioni sul model.
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

    /**
     * Catalogo principale delle tracce.
     *
     * ObservableList permette l'aggiornamento automatico della TableView quando
     * vengono aggiunte o rimosse tracce.
     */
    private final ObservableList<Track> tracks = FXCollections.observableArrayList();

    /**
     * Lista principale delle playlist create dall'utente.
     */
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    /**
     * Lista osservabile usata per mostrare le tracce della playlist attualmente selezionata.
     */
    private final ObservableList<Track> selectedPlaylistTracks = FXCollections.observableArrayList();

    /**
     * Service per la logica applicativa sulle tracce.
     */
    private final TrackService trackService = new TrackService();

    /**
     * Service per la logica applicativa sulle playlist.
     */
    private final PlaylistService playlistService = new PlaylistService();

    /**
     * Gestore dei comandi annullabili.
     *
     * È usato per predisporre Undo/Redo secondo il Command Pattern.
     */
    private final CommandManager commandManager = new CommandManager();

    /**
     * Inizializza la schermata principale.
     *
     * Collega le liste osservabili alle componenti grafiche, configura le colonne
     * delle tabelle e imposta i listener per aggiornare la UI quando l'utente
     * seleziona tracce o playlist.
     */
    @FXML
    private void initialize() {
        configurePlaylistListView();
        configureTrackLibraryTable();
        configureSelectedPlaylistTable();

        if (statusLabel != null) {
            statusLabel.setText("Applicazione avviata correttamente.");
        }

        System.out.println("FXML collegato correttamente al MainController.");
        System.out.println("playerControlController = " + playerControlController);
    }

    /**
     * Configura la ListView delle playlist.
     */
    private void configurePlaylistListView() {
        playlistListView.setItems(playlists);
        playlistListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        playlistListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldPlaylist, newPlaylist) -> updateSelectedPlaylistView(newPlaylist));
    }

    /**
     * Configura la tabella principale della Track Library.
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
                                "Traccia selezionata: " + newTrack.getTitle() + " - " + newTrack.getAuthor()
                        );
                    }
                });

        trackTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        trackAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        trackLengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        trackGenreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        trackYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        trackPlayCountColumn.setCellValueFactory(new PropertyValueFactory<>("playedCount"));

        trackTableView.setItems(tracks);
    }

    /**
     * Configura la tabella che mostra le tracce della playlist selezionata.
     */
    private void configureSelectedPlaylistTable() {
        playlistTrackTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        playlistTrackTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldTrack, newTrack) -> {
                    if (playerControlController != null) {
                        playerControlController.setSelectedTrack(newTrack);
                        playerControlController.setCurrentPlaylist(playlistTrackTableView.getItems());
                    }

                    if (statusLabel != null && newTrack != null) {
                        statusLabel.setText(
                                "Traccia selezionata nella playlist: "
                                        + newTrack.getTitle()
                                        + " - "
                                        + newTrack.getAuthor()
                        );
                    }
                });

        playlistTrackTableView.setItems(selectedPlaylistTracks);

        playlistTrackOrderColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        playlistTrackTableView.getItems().indexOf(cellData.getValue()) + 1
                ).asObject()
        );

        playlistTrackTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        playlistTrackAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        playlistTrackLengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        playlistTrackGenreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
    }

    /**
     * Gestisce la pulizia del campo di ricerca globale.
     *
     * La logica completa di ricerca/filtro verrà integrata nella relativa User Story.
     */
    @FXML
    private void handleClearSearch() {
        System.out.println("Clear search");
    }

    /**
     * Gestisce la creazione di una nuova playlist.
     *
     * Il controller raccoglie il nome tramite dialog, mentre PlaylistService
     * valida il nome, controlla eventuali duplicati e crea la playlist.
     */
    @FXML
    private void handleNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New playlist");
        dialog.setHeaderText("Crea una nuova playlist");
        dialog.setContentText("Nome playlist:");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        try {
            Playlist playlist = playlistService.createPlaylist(playlists, result.get());

            playlistListView.getSelectionModel().select(playlist);
            updateSelectedPlaylistView(playlist);

            if (statusLabel != null) {
                statusLabel.setText("Playlist creata: " + playlist.getName());
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Gestisce la rinomina della playlist selezionata.
     *
     * Il controller recupera la playlist selezionata e il nuovo nome inserito
     * dall'utente. La validazione e la modifica effettiva vengono delegate
     * a PlaylistService.
     */
    @FXML
    private void handleRenamePlaylist() {
        Playlist selectedPlaylist = getSelectedPlaylist();

        if (selectedPlaylist == null) {
            showError("Seleziona una playlist da rinominare.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedPlaylist.getName());
        dialog.setTitle("Rename playlist");
        dialog.setHeaderText("Rinomina playlist");
        dialog.setContentText("Nuovo nome:");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        try {
            playlistService.renamePlaylist(playlists, selectedPlaylist, result.get());

            playlistListView.refresh();
            updateSelectedPlaylistView(selectedPlaylist);

            if (statusLabel != null) {
                statusLabel.setText("Playlist rinominata: " + selectedPlaylist.getName());
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Gestisce l'eliminazione della playlist selezionata.
     *
     * Il controller mostra un pop-up di conferma e delega la rimozione effettiva
     * a PlaylistService.
     */
    @FXML
    private void handleDeletePlaylist() {
        Playlist selectedPlaylist = getSelectedPlaylist();

        if (selectedPlaylist == null) {
            showError("Seleziona una playlist da eliminare.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete playlist");
        confirmationAlert.setHeaderText("Conferma eliminazione");
        confirmationAlert.setContentText(
                "Vuoi eliminare la playlist \"" + selectedPlaylist.getName() + "\"?"
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

        try {
            playlistService.deletePlaylist(playlists, selectedPlaylist);

            selectedPlaylistTracks.clear();
            playlistListView.getSelectionModel().clearSelection();

            if (statusLabel != null) {
                statusLabel.setText("Playlist eliminata: " + selectedPlaylist.getName());
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Genera una playlist automatica filtrando per genere.
     *
     * La logica completa sarà collegata alla factory delle smart playlist.
     */
    @FXML
    private void handleGenerateByGenre() {
        System.out.println("Generate by genre");
    }

    /**
     * Genera una playlist automatica filtrando per anno.
     *
     * La logica completa sarà collegata alla factory delle smart playlist.
     */
    @FXML
    private void handleGenerateByYear() {
        System.out.println("Generate by year");
    }

    /**
     * Genera una playlist automatica filtrando per tag.
     *
     * La logica completa sarà collegata alla factory delle smart playlist.
     */
    @FXML
    private void handleGenerateByTag() {
        System.out.println("Generate by tag");
    }

    /**
     * Apre la schermata Add Track e aggiunge al catalogo la traccia creata.
     *
     * La creazione della traccia viene gestita da AddTrackController, mentre
     * MainController aggiorna la Track Library delegando l'aggiunta a TrackService.
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
     * Apre la schermata AddTrackView in modalità modifica.
     *
     * La stessa view viene riutilizzata per evitare duplicazione dell'interfaccia.
     * I dati della traccia selezionata vengono precompilati e, al salvataggio,
     * vengono aggiornati solo i campi modificabili tramite TrackService.
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
     * Gestisce il click sul pulsante Delete della Track Library.
     *
     * Recupera la traccia selezionata, mostra un pop-up di conferma e,
     * in caso di conferma, delega la rimozione a TrackService.
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

        removeTrackFromCatalog(selectedTrack);

        if (statusLabel != null) {
            statusLabel.setText("Traccia eliminata: " + selectedTrack.getTitle());
        }
    }

    /**
     * Rimuove una traccia dal catalogo principale e da tutte le playlist.
     *
     * Il controller delega la logica applicativa a TrackService e aggiorna solo
     * la selezione e le tabelle della UI.
     *
     * @param track traccia da eliminare
     */
    private void removeTrackFromCatalog(Track track) {
        if (track == null) {
            return;
        }

        trackService.removeTrack(tracks, playlists, track);

        trackTableView.getSelectionModel().clearSelection();

        Playlist selectedPlaylist = getSelectedPlaylist();
        updateSelectedPlaylistView(selectedPlaylist);
    }

    /**
     * Aggiorna la tabella della playlist selezionata.
     *
     * @param playlist playlist attualmente selezionata
     */
    private void updateSelectedPlaylistView(Playlist playlist) {
        selectedPlaylistTracks.clear();

        if (playlist == null) {
            if (statusLabel != null) {
                statusLabel.setText("Nessuna playlist selezionata.");
            }
            return;
        }

        selectedPlaylistTracks.addAll(playlist.getTracks());

        if (statusLabel != null) {
            if (playlist.getTracks().isEmpty()) {
                statusLabel.setText(
                        "Playlist selezionata: " + playlist.getName() + " - nessuna traccia presente."
                );
            } else {
                statusLabel.setText(
                        "Playlist selezionata: "
                                + playlist.getName()
                                + " - "
                                + playlist.getTracks().size()
                                + " tracce"
                );
            }
        }
    }

    /**
     * Gestisce il caso in cui venga rimossa dalla playlist una traccia attualmente in riproduzione.
     *
     * Se la playlist diventa vuota, la riproduzione viene fermata.
     * Altrimenti viene scelta una traccia successiva coerente con la posizione della traccia rimossa.
     *
     * @param playlist playlist da cui è stata rimossa la traccia
     * @param removedTrack traccia rimossa
     * @param removedIndex indice originale della traccia rimossa
     */
    private void handleRemovedTrackPlayback(Playlist playlist, Track removedTrack, int removedIndex) {
        if (playlist == null || removedTrack == null || playerControlController == null) {
            return;
        }

        Track currentTrack = playerControlController.getCurrentTrack();

        if (currentTrack == null || !currentTrack.equals(removedTrack)) {
            return;
        }

        if (playlist.getTracks().isEmpty()) {
            playerControlController.stopPlayback();

            if (statusLabel != null) {
                statusLabel.setText("La playlist è vuota: riproduzione fermata.");
            }

            return;
        }

        Track nextTrack;

        if (removedIndex < playlist.getTracks().size()) {
            nextTrack = playlist.getTracks().get(removedIndex);
        } else {
            nextTrack = playlist.getTracks().get(playlist.getTracks().size() - 1);
        }

        playerControlController.playTrackFromPlaylist(nextTrack);

        if (statusLabel != null) {
            statusLabel.setText(
                    "Traccia rimossa durante la riproduzione. Ora in riproduzione: "
                            + nextTrack.getTitle()
            );
        }
    }

    /**
     * Restituisce la playlist attualmente selezionata nella ListView.
     *
     * @return playlist selezionata, oppure null
     */
    private Playlist getSelectedPlaylist() {
        return playlistListView.getSelectionModel().getSelectedItem();
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
     * Restituisce la traccia selezionata nella tabella della playlist.
     *
     * @return traccia selezionata nella playlist, oppure null
     */
    private Track getSelectedTrackFromPlaylist() {
        return playlistTrackTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Aggiunge la traccia selezionata dalla libreria alla playlist selezionata.
     *
     * La validazione e l'aggiunta vengono delegate a PlaylistService.
     */
    @FXML
    private void handleAddToPlaylist() {
        Playlist selectedPlaylist = getSelectedPlaylist();
        Track selectedTrack = getSelectedTrackFromLibrary();

        try {
            playlistService.addTrackToPlaylist(selectedPlaylist, selectedTrack);
            updateSelectedPlaylistView(selectedPlaylist);

            if (statusLabel != null) {
                statusLabel.setText(
                        "Traccia aggiunta alla playlist: "
                                + selectedTrack.getTitle()
                                + " → "
                                + selectedPlaylist.getName()
                );
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Rimuove dalla playlist la traccia selezionata.
     *
     * L'operazione viene eseguita tramite Command Pattern, così da predisporre
     * Undo/Redo. Il controller gestisce il pop-up di conferma e l'aggiornamento
     * della UI.
     */
    @FXML
    private void handleRemoveFromPlaylist() {
        Playlist selectedPlaylist = getSelectedPlaylist();
        Track selectedTrack = getSelectedTrackFromPlaylist();

        try {
            playlistService.validateTrackRemovalSelection(selectedPlaylist, selectedTrack);

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Remove track");
            confirmationAlert.setHeaderText("Conferma rimozione");
            confirmationAlert.setContentText(
                    "Vuoi rimuovere la traccia \""
                            + selectedTrack.getTitle()
                            + "\" dalla playlist \""
                            + selectedPlaylist.getName()
                            + "\"?"
            );

            ButtonType cancelButton = new ButtonType("Cancel");
            ButtonType removeButton = new ButtonType("Remove");

            confirmationAlert.getButtonTypes().setAll(cancelButton, removeButton);

            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isEmpty() || result.get() == cancelButton) {
                if (statusLabel != null) {
                    statusLabel.setText("Rimozione annullata.");
                }
                return;
            }

            int removedIndex = selectedPlaylist.getTracks().indexOf(selectedTrack);
            boolean removedTrackWasPlaying = playerControlController != null
                    && playerControlController.getCurrentTrack() != null
                    && playerControlController.getCurrentTrack().equals(selectedTrack);

            Command removeCommand = new RemoveTrackPCommand(
                    playlistService,
                    selectedPlaylist,
                    selectedTrack
            );

            commandManager.executeCommand(removeCommand);

            handleRemovedTrackPlayback(selectedPlaylist, selectedTrack, removedIndex);

            updateSelectedPlaylistView(selectedPlaylist);
            playlistTrackTableView.getSelectionModel().clearSelection();

            if (statusLabel != null && !removedTrackWasPlaying) {
                statusLabel.setText(
                        "Traccia rimossa dalla playlist: "
                                + selectedTrack.getTitle()
                                + " ← "
                                + selectedPlaylist.getName()
                );
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Sposta verso l'alto la traccia selezionata nella playlist.
     *
     * Funzionalità predisposta per una User Story successiva.
     */
    @FXML
    private void handleMoveTrackUp() {
        System.out.println("Move track up");
    }

    /**
     * Sposta verso il basso la traccia selezionata nella playlist.
     *
     * Funzionalità predisposta per una User Story successiva.
     */
    @FXML
    private void handleMoveTrackDown() {
        System.out.println("Move track down");
    }

    /**
     * Applica i filtri avanzati alla Track Library.
     *
     * Funzionalità predisposta per una User Story successiva.
     */
    @FXML
    private void handleApplyFilters() {
        System.out.println("Apply filters");
    }

    /**
     * Ripristina i filtri avanzati.
     *
     * Funzionalità predisposta per una User Story successiva.
     */
    @FXML
    private void handleResetFilters() {
        System.out.println("Reset filters");
    }

    /**
     * Esegue l'undo dell'ultima operazione annullabile.
     *
     * Il CommandManager è già presente, ma il collegamento completo con la UI
     * verrà completato nelle User Story dedicate.
     */
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
     * Mostra un messaggio di errore all'utente.
     *
     * @param message messaggio da visualizzare nel pop-up
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}