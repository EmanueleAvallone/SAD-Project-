package com.musicplayer.controller;

import com.musicplayer.model.Playlist;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;

import com.musicplayer.model.Track;
import com.musicplayer.service.TrackService;
import com.musicplayer.service.PlaylistService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ButtonType;
import java.util.Optional;
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

  /*  @FXML
    private RadioButton sequentialModeRadio;

    @FXML
    private RadioButton shuffleModeRadio;

    @FXML
    private RadioButton loopModeRadio;
*/
    @FXML
    private PlayerController playerControlController;

    private final ObservableList<Track> tracks = FXCollections.observableArrayList(); //lista delle tracce
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList(); //lista delle playlist
    private final ObservableList<Track> selectedPlaylistTracks = FXCollections.observableArrayList();
    private final TrackService trackService = new TrackService();
    private final PlaylistService playlistService = new PlaylistService();
    //private final PlayerController playerController = new PlayerController();

    @FXML
    private void initialize() {
        playlistListView.setItems(playlists);
        playlistListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        playlistListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldPlaylist, newPlaylist) -> {
                    updateSelectedPlaylistView(newPlaylist);
                });
        trackTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); //gestisce la selezione di una riga nella tabella track library

        /*trackTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldTrack, newTrack) ->
                        playerController.setSelectedTrack(newTrack)
                );*/
        trackTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldTrack, newTrack) -> {
                    if (playerControlController != null) {
                        playerControlController.setSelectedTrack(newTrack);
                    }
                });
        trackTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        trackAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        trackLengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        trackGenreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        trackYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        trackPlayCountColumn.setCellValueFactory(new PropertyValueFactory<>("playedCount"));

        trackTableView.setItems(tracks);
        playlistTrackOrderColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(
                        playlistTrackTableView.getItems().indexOf(cellData.getValue()) + 1
                ).asObject()
        );

        playlistTrackTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        playlistTrackAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        playlistTrackLengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        playlistTrackGenreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));

        playlistTrackTableView.setItems(selectedPlaylistTracks);

        playlistListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldPlaylist, newPlaylist) -> {
                    updateSelectedPlaylistView(newPlaylist);
                });

        /* ToggleGroup playbackModeGroup = new ToggleGroup();
        sequentialModeRadio.setToggleGroup(playbackModeGroup);
        shuffleModeRadio.setToggleGroup(playbackModeGroup);
        loopModeRadio.setToggleGroup(playbackModeGroup);*/

        if (statusLabel != null) {
            statusLabel.setText("Applicazione avviata correttamente.");
        }

        System.out.println("FXML collegato correttamente al MainController.");
    }

    @FXML
    private void handleClearSearch() {
        System.out.println("Clear search");
    }

    @FXML
    private void handleNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New playlist");
        dialog.setHeaderText("Crea una nuova playlist");
        dialog.setContentText("Nome playlist:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String playlistName = result.get().trim();

            if (playlistName.isEmpty()) {
                showError("Il nome della playlist non può essere vuoto.");
                return;
            }

            boolean alreadyExists = playlists.stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(playlistName));

            if (alreadyExists) {
                showError("Esiste già una playlist con questo nome.");
                return;
            }

            Playlist playlist = new Playlist(playlistName);
            playlists.add(playlist);
            playlistListView.getSelectionModel().select(playlist);

            updateSelectedPlaylistView(playlist);
        }
    }

    @FXML
    private void handleRenamePlaylist() {
        System.out.println("Rename playlist");
    }

    @FXML
    private void handleDeletePlaylist() {
        System.out.println("Delete playlist");
    }

    @FXML
    private void handleGenerateByGenre() {
        System.out.println("Generate by genre");
    }

    @FXML
    private void handleGenerateByYear() {
        System.out.println("Generate by year");
    }

    @FXML
    private void handleGenerateByTag() {
        System.out.println("Generate by tag");
    }

    /**
     * Apre la schermata Add Track e aggiunge al catalogo la traccia creata.
     *
     * La creazione della traccia viene gestita da AddTrackController, mentre
     * MainController si occupa di aggiornare la Track Library.
     */
    @FXML
    private void handleAddTrack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/musicplayer/view/AddTrackView.fxml")
            );

            Parent root = loader.load();

            AddTrackController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Add track");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(trackTableView.getScene().getWindow());
            stage.setResizable(false);

            stage.showAndWait();

            Track createdTrack = controller.getCreatedTrack();

            if (createdTrack != null) {
                trackService.addTrack(tracks, createdTrack);                trackTableView.getSelectionModel().select(createdTrack);

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
     * vengono aggiornati solo i campi modificabili.
     */
    @FXML
    private void handleEditTrack() {
        Track selectedTrack = trackTableView.getSelectionModel().getSelectedItem();

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

            Stage stage = new Stage();
            stage.setTitle("Edit track");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(trackTableView.getScene().getWindow());
            stage.setResizable(false);

            stage.showAndWait();

            Track editedTrack = controller.getCreatedTrack();

            if (editedTrack != null) {
                trackService.updateEditableFields(selectedTrack, editedTrack);
                trackTableView.refresh();

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
     * Gestisce il click sul pulsante Delete della Track Library.
     *
     * Recupera la traccia selezionata, mostra un pop-up di conferma e,
     * in caso di conferma, delega la rimozione al metodo removeTrackFromCatalog.
     */

    @FXML
    private void handleDeleteTrack() {
        Track selectedTrack = trackTableView.getSelectionModel().getSelectedItem();

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
     * Il controller delega la logica applicativa al TrackService e si occupa solo
     * dell'aggiornamento della selezione nella UI.
     *
     * @param track traccia da eliminare
     */
    private void removeTrackFromCatalog(Track track) {
        if (track == null) {
            return;
        }

        trackService.removeTrack(tracks, playlists, track);
        trackTableView.getSelectionModel().clearSelection();
    }


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
                statusLabel.setText("Playlist selezionata: " + playlist.getName() + " - nessuna traccia presente.");
            } else {
                statusLabel.setText("Playlist selezionata: " + playlist.getName());
            }
        }
    }


    private Playlist getSelectedPlaylist() {
        return playlistListView.getSelectionModel().getSelectedItem();

    }

    private Track getSelectedTrackFromLibrary() {
        return trackTableView.getSelectionModel().getSelectedItem();

    }


    @FXML
    private void handleAddToPlaylist() {
        Playlist selectedPlaylist = getSelectedPlaylist();
        Track selectedTrack = getSelectedTrackFromLibrary();

        try {
            playlistService.validateTrackAdditionSelection(selectedPlaylist, selectedTrack);
            System.out.println("Playlist selezionata: " + selectedPlaylist.getName());
            System.out.println("Traccia selezionata: " + selectedTrack.getTitle());
            if (statusLabel != null) {
                statusLabel.setText("Selezione valida: " + selectedTrack.getTitle() + " → " + selectedPlaylist.getName());
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void handleRemoveFromPlaylist() {
        System.out.println("Remove from playlist");
    }

    @FXML
    private void handleMoveTrackUp() {
        System.out.println("Move track up");
    }

    @FXML
    private void handleMoveTrackDown() {
        System.out.println("Move track down");
    }

    /*@FXML
    private void handlePlay() {
        Track selectedTrack = trackTableView.getSelectionModel().getSelectedItem();

        if (selectedTrack == null) {
            showError("Seleziona una traccia da riprodurre.");
            return;
        }

        playerController.setSelectedTrack(selectedTrack);
        playerController.handlePlay();

        if (statusLabel != null) {
            statusLabel.setText("Riproduzione avviata: " + selectedTrack.getTitle());
        }

        trackTableView.refresh();
    }

    @FXML
    private void handlePause() {
        playerController.handlePause();

        if (statusLabel != null) {
            statusLabel.setText("Riproduzione sospesa.");
        }
    }

    @FXML
    private void handleSkip() {
        System.out.println("Skip");
    }*/

    @FXML
    private void handleApplyFilters() {
        System.out.println("Apply filters");
    }

    @FXML
    private void handleResetFilters() {
        System.out.println("Reset filters");
    }

    @FXML
    private void handleUndo() {
        System.out.println("Undo");
    }

    @FXML
    private void handleRedo() {
        System.out.println("Redo");
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}