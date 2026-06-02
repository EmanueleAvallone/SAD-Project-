package com.musicplayer.controller;

import com.musicplayer.command.Command;
import com.musicplayer.command.CommandManager;
import com.musicplayer.command.RemoveTrackPCommand;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Track;
import com.musicplayer.service.PlaylistService;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

/**
 * Controller di sezione dedicato alla gestione delle playlist.
 *
 * Questa classe non è collegata direttamente a un file FXML separato.
 * Viene inizializzata dal MainController, che le passa i riferimenti alle
 * componenti grafiche presenti nella schermata principale.
 *
 * In questo modo MainController resta il coordinatore generale della view,
 * mentre PlaylistController gestisce tutte le interazioni specifiche della
 * sezione playlist.
 *
 * Responsabilità principali:
 * - creazione playlist;
 * - rinomina playlist;
 * - eliminazione playlist;
 * - visualizzazione tracce della playlist selezionata;
 * - aggiunta di una traccia dalla libreria alla playlist;
 * - rimozione di una traccia dalla playlist;
 * - aggiornamento della UI relativa alla playlist selezionata.
 *
 * La business logic viene comunque delegata a PlaylistService.
 */
public class PlaylistController {

    private ListView<Playlist> playlistListView;

    private TableView<Track> trackTableView;

    private TableView<Track> playlistTrackTableView;

    private TableColumn<Track, Integer> playlistTrackOrderColumn;

    private TableColumn<Track, String> playlistTrackTitleColumn;

    private TableColumn<Track, String> playlistTrackAuthorColumn;

    private TableColumn<Track, String> playlistTrackLengthColumn;

    private TableColumn<Track, String> playlistTrackGenreColumn;

    private Label statusLabel;

    private ObservableList<Playlist> playlists;

    private ObservableList<Track> selectedPlaylistTracks;

    private PlayerController playerControlController;

    private final PlaylistService playlistService;

    private final CommandManager commandManager;

    /**
     * Costruisce il controller di sezione playlist.
     *
     * @param playlistService service che contiene la logica applicativa sulle playlist
     * @param commandManager gestore dei comandi annullabili
     */
    public PlaylistController(PlaylistService playlistService, CommandManager commandManager) {
        this.playlistService = playlistService;
        this.commandManager = commandManager;
    }

    /**
     * Collega questo controller alle componenti grafiche della sezione playlist.
     *
     * Questo metodo viene chiamato dal MainController durante l'inizializzazione
     * della schermata principale.
     *
     * @param playlistListView lista grafica delle playlist
     * @param trackTableView tabella principale della Track Library
     * @param playlistTrackTableView tabella delle tracce della playlist selezionata
     * @param playlistTrackOrderColumn colonna ordine tracce nella playlist
     * @param playlistTrackTitleColumn colonna titolo
     * @param playlistTrackAuthorColumn colonna autore
     * @param playlistTrackLengthColumn colonna durata
     * @param playlistTrackGenreColumn colonna genere
     * @param statusLabel label di stato della schermata
     * @param playlists lista osservabile delle playlist
     * @param selectedPlaylistTracks lista osservabile delle tracce della playlist selezionata
     * @param playerControlController controller del player, usato per aggiornare la traccia corrente
     */
    public void initializeSection(
            ListView<Playlist> playlistListView,
            TableView<Track> trackTableView,
            TableView<Track> playlistTrackTableView,
            TableColumn<Track, Integer> playlistTrackOrderColumn,
            TableColumn<Track, String> playlistTrackTitleColumn,
            TableColumn<Track, String> playlistTrackAuthorColumn,
            TableColumn<Track, String> playlistTrackLengthColumn,
            TableColumn<Track, String> playlistTrackGenreColumn,
            Label statusLabel,
            ObservableList<Playlist> playlists,
            ObservableList<Track> selectedPlaylistTracks,
            PlayerController playerControlController
    ) {
        this.playlistListView = playlistListView;
        this.trackTableView = trackTableView;
        this.playlistTrackTableView = playlistTrackTableView;
        this.playlistTrackOrderColumn = playlistTrackOrderColumn;
        this.playlistTrackTitleColumn = playlistTrackTitleColumn;
        this.playlistTrackAuthorColumn = playlistTrackAuthorColumn;
        this.playlistTrackLengthColumn = playlistTrackLengthColumn;
        this.playlistTrackGenreColumn = playlistTrackGenreColumn;
        this.statusLabel = statusLabel;
        this.playlists = playlists;
        this.selectedPlaylistTracks = selectedPlaylistTracks;
        this.playerControlController = playerControlController;

        configurePlaylistListView();
        configureSelectedPlaylistTable();
    }

    /**
     * Configura la ListView delle playlist.
     */
    private void configurePlaylistListView() {
        playlistListView.setItems(playlists);
        playlistListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        playlistListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldPlaylist, newPlaylist) ->
                        updateSelectedPlaylistView(newPlaylist)
                );
    }

    /**
     * Configura la tabella delle tracce appartenenti alla playlist selezionata.
     */
    private void configureSelectedPlaylistTable() {
        playlistTrackTableView.setItems(selectedPlaylistTracks);
        playlistTrackTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        playlistTrackTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldTrack, newTrack) -> {
                    if (playerControlController != null) {
                        playerControlController.setSelectedTrack(newTrack);
                        playerControlController.setCurrentPlaylist(playlistTrackTableView.getItems());
                    }

                    if (newTrack != null) {
                        setStatus(
                                "Traccia selezionata nella playlist: "
                                        + newTrack.getTitle()
                                        + " - "
                                        + newTrack.getAuthor()
                        );
                    }
                });

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
     * Gestisce la creazione di una nuova playlist.
     *
     * Il controller raccoglie il nome tramite dialog e delega al PlaylistService
     * la validazione e la creazione effettiva della playlist.
     */
    public void createPlaylist() {
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

            setStatus("Playlist creata: " + playlist.getName());

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Gestisce la rinomina della playlist selezionata.
     */
    public void renamePlaylist() {
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

            setStatus("Playlist rinominata: " + selectedPlaylist.getName());

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Gestisce l'eliminazione della playlist selezionata.
     */
    public void deletePlaylist() {
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
            setStatus("Eliminazione playlist annullata.");
            return;
        }

        try {
            playlistService.deletePlaylist(playlists, selectedPlaylist);

            selectedPlaylistTracks.clear();
            playlistListView.getSelectionModel().clearSelection();

            setStatus("Playlist eliminata: " + selectedPlaylist.getName());

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Aggiunge alla playlist selezionata la traccia selezionata nella Track Library.
     */
    public void addSelectedTrackToPlaylist() {
        Playlist selectedPlaylist = getSelectedPlaylist();
        Track selectedTrack = getSelectedTrackFromLibrary();

        try {
            playlistService.addTrackToPlaylist(selectedPlaylist, selectedTrack);
            updateSelectedPlaylistView(selectedPlaylist);

            setStatus(
                    "Traccia aggiunta alla playlist: "
                            + selectedTrack.getTitle()
                            + " → "
                            + selectedPlaylist.getName()
            );

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Rimuove dalla playlist la traccia selezionata.
     *
     * L'operazione viene eseguita tramite CommandManager, così da predisporre
     * Undo/Redo secondo il Command Pattern.
     */
    public void removeSelectedTrackFromPlaylist() {
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
                setStatus("Rimozione annullata.");
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

            if (!removedTrackWasPlaying) {
                setStatus(
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
     * Aggiorna la tabella della playlist selezionata.
     *
     * @param playlist playlist selezionata
     */
    public void updateSelectedPlaylistView(Playlist playlist) {
        selectedPlaylistTracks.clear();

        if (playlist == null) {
            setStatus("Nessuna playlist selezionata.");
            return;
        }

        selectedPlaylistTracks.addAll(playlist.getTracks());

        if (playlist.getTracks().isEmpty()) {
            setStatus("Playlist selezionata: " + playlist.getName() + " - nessuna traccia presente.");
        } else {
            setStatus(
                    "Playlist selezionata: "
                            + playlist.getName()
                            + " - "
                            + playlist.getTracks().size()
                            + " tracce"
            );
        }
    }

    /**
     * Gestisce il caso in cui una traccia rimossa dalla playlist fosse in riproduzione.
     *
     * @param playlist playlist da cui la traccia è stata rimossa
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
            setStatus("La playlist è vuota: riproduzione fermata.");
            return;
        }

        Track nextTrack;

        if (removedIndex < playlist.getTracks().size()) {
            nextTrack = playlist.getTracks().get(removedIndex);
        } else {
            nextTrack = playlist.getTracks().get(playlist.getTracks().size() - 1);
        }

        playerControlController.playTrackFromPlaylist(nextTrack);

        setStatus(
                "Traccia rimossa durante la riproduzione. Ora in riproduzione: "
                        + nextTrack.getTitle()
        );
    }

    /**
     * Restituisce la playlist selezionata.
     *
     * @return playlist selezionata, oppure null
     */
    public Playlist getSelectedPlaylist() {
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
     * @return traccia selezionata, oppure null
     */
    private Track getSelectedTrackFromPlaylist() {
        return playlistTrackTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Imposta un messaggio nella status label.
     *
     * @param message messaggio da mostrare
     */
    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Mostra un pop-up di errore.
     *
     * @param message messaggio di errore
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}