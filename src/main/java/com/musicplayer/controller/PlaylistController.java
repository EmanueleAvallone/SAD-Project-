package com.musicplayer.controller;

import com.musicplayer.command.Command;
import com.musicplayer.command.CommandManager;
import com.musicplayer.command.RemoveTrackPCommand;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Tag;
import com.musicplayer.model.Track;
import com.musicplayer.service.PlaylistService;

import com.musicplayer.service.SearchService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller di sezione dedicato alla gestione delle playlist.
 *
 * Questo controller è collegato direttamente a PlaylistView.fxml per la sidebar
 * delle playlist. Inoltre riceve dal MainController i riferimenti alla tabella
 * "Selected playlist", che rimane nella schermata principale.
 *
 * In questo modo MainController resta il coordinatore generale della view,
 * mentre PlaylistController gestisce tutte le interazioni specifiche della
 * sezione playlist.
 *
 * Responsabilità principali:
 * - creazione playlist;
 * - rinomina playlist;
 * - eliminazione playlist;
 * - generazione smart playlist;
 * - visualizzazione tracce della playlist selezionata;
 * - aggiunta di una traccia dalla libreria alla playlist;
 * - rimozione di una traccia dalla playlist;
 * - spostamento delle tracce dentro una playlist;
 * - aggiornamento della UI relativa alla playlist selezionata.
 *
 * La business logic viene comunque delegata a PlaylistService.
 */
public class PlaylistController {

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

    @FXML
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

    private PlaylistService playlistService;

    private CommandManager commandManager;

    private UndoSnackbarHandler undoSnackbarHandler;

    private SearchService searchService;


    /**
     * Imposta il gestore dello snackbar globale.
     *
     * @param undoSnackbarHandler handler fornito dal MainController
     */
    public void setUndoSnackbarHandler(UndoSnackbarHandler undoSnackbarHandler) {
        this.undoSnackbarHandler = undoSnackbarHandler;
    }

    /**
     * Collega questo controller alle componenti grafiche condivise.
     *
     * La ListView delle playlist viene iniettata direttamente da PlaylistView.fxml.
     * La tabella della selected playlist viene invece passata dal MainController,
     * perché si trova ancora nella view principale.
     */
    public void initializeSection(
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
            PlayerController playerControlController,
            PlaylistService playlistService,
            CommandManager commandManager,
            SearchService searchService) {
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
        this.playlistService = playlistService;
        this.commandManager = commandManager;
        this.searchService = searchService;

        configurePlaylistListView();
        configureSelectedPlaylistTable();
    }

    /**
     * Gestisce il click sul pulsante New della sidebar playlist.
     */
    @FXML
    private void handleNewPlaylist() {
        createPlaylist();
    }

    /**
     * Gestisce il click sul pulsante Rename della sidebar playlist.
     */
    @FXML
    private void handleRenamePlaylist() {
        renamePlaylist();
    }

    /**
     * Gestisce il click sul pulsante Delete selected playlist della sidebar playlist.
     */
    @FXML
    private void handleDeletePlaylist() {
        deletePlaylist();
    }

    /**
     * Gestisce il click sul pulsante Generate by genre.
     */
    @FXML
    private void handleGenerateByGenre() {
        generatePlaylistByGenre(trackTableView.getItems());
    }

    /**
     * Gestisce il click sul pulsante Generate by year.
     */
    @FXML
    private void handleGenerateByYear() {
        generatePlaylistByYear(trackTableView.getItems());
    }

    /**
     * Gestisce il click sul pulsante Generate by tag.
     */
    @FXML
    private void handleGenerateByTag() {
        generatePlaylistByTag(trackTableView.getItems());
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

        configurePlayingTrackStyle();
    }

    /**
     * Configura l'evidenziazione della traccia in riproduzione.
     *
     * Il controller aggiunge solo una classe CSS. Lo stile grafico effettivo
     * viene definito nel file CSS.
     */
    private void configurePlayingTrackStyle() {
        playlistTrackTableView.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(Track track, boolean empty) {
                super.updateItem(track, empty);

                getStyleClass().remove("playing-track");

                if (empty || track == null || playerControlController == null) {
                    return;
                }

                Track currentTrack = playerControlController.getCurrentTrack();

                if (currentTrack != null
                        && currentTrack.equals(track)
                        && playerControlController.isPlaying()) {

                    if (!getStyleClass().contains("playing-track")) {
                        getStyleClass().add("playing-track");
                    }
                }
            }
        });
    }

    /**
     * Gestisce la creazione di una nuova playlist.
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
     * Gestisce l'eliminazione temporanea della playlist selezionata.
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

        playlistService.softDeletePlaylist(playlists, selectedPlaylist);

        playlistListView.getSelectionModel().clearSelection();
        updateSelectedPlaylistView(null);

        if (undoSnackbarHandler != null) {
            undoSnackbarHandler.showUndoSnackbar(
                    "Playlist rimossa",
                    this::restorePendingDeletedPlaylist,
                    this::confirmPendingDeletedPlaylist
            );
        }

        setStatus("Playlist rimossa temporaneamente: " + selectedPlaylist.getName());
    }

    /**
     * Ripristina una playlist eliminata temporaneamente.
     */
    private void restorePendingDeletedPlaylist() {
        if (!playlistService.hasPendingDeletedPlaylist()) {
            return;
        }

        Playlist restoredPlaylist = playlistService.getPendingDeletedPlaylist();

        playlistService.restorePendingDeletedPlaylist(playlists);

        playlistListView.getSelectionModel().select(restoredPlaylist);
        updateSelectedPlaylistView(restoredPlaylist);

        setStatus("Eliminazione playlist annullata: " + restoredPlaylist.getName());
    }

    /**
     * Conferma definitivamente l'eliminazione temporanea della playlist.
     */
    private void confirmPendingDeletedPlaylist() {
        if (!playlistService.hasPendingDeletedPlaylist()) {
            return;
        }

        Playlist deletedPlaylist = playlistService.getPendingDeletedPlaylist();

        playlistService.clearPendingDeletedPlaylist();

        setStatus("Playlist eliminata definitivamente: " + deletedPlaylist.getName());
    }

    /**
     * Genera una smart playlist filtrando per genere.
     */
    public void generatePlaylistByGenre(ObservableList<Track> tracks) {
        List<String> uniqueGenres = tracks.stream()
                .map(Track::getGenre)
                .filter(genre -> genre != null && !genre.trim().isEmpty())
                .map(genre -> genre.trim().toLowerCase())
                .map(genre -> genre.substring(0, 1).toUpperCase() + genre.substring(1))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (uniqueGenres.isEmpty()) {
            showError("Nessun genere presente nella libreria.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(uniqueGenres.get(0), uniqueGenres);
        dialog.setTitle("Generate Smart Playlist");
        dialog.setHeaderText("Filtra per genere");
        dialog.setContentText("Seleziona il genere desiderato:");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        try {
            Playlist generated = playlistService.generatePlaylistByGenre(playlists, tracks, result.get());

            playlistListView.getSelectionModel().select(generated);
            updateSelectedPlaylistView(generated);

            setStatus("Smart playlist creata: " + generated.getName());

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Genera una smart playlist filtrando per anno.
     */
    public void generatePlaylistByYear(ObservableList<Track> tracks) {
        List<Integer> uniqueYears = tracks.stream()
                .map(Track::getYear)
                .filter(year -> year > 0)
                .distinct()
                .sorted(java.util.Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (uniqueYears.isEmpty()) {
            showError("Nessun anno presente nella libreria.");
            return;
        }

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(uniqueYears.get(0), uniqueYears);
        dialog.setTitle("Generate Smart Playlist");
        dialog.setHeaderText("Filtra per anno");
        dialog.setContentText("Seleziona l'anno desiderato:");

        Optional<Integer> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        try {
            Playlist generated = playlistService.generatePlaylistByYear(playlists, tracks, result.get());

            playlistListView.getSelectionModel().select(generated);
            updateSelectedPlaylistView(generated);

            setStatus("Smart playlist creata: " + generated.getName());

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Genera una smart playlist filtrando per tag.
     */
    public void generatePlaylistByTag(ObservableList<Track> tracks) {
        ChoiceDialog<Tag> dialog = new ChoiceDialog<>(Tag.FAV, Tag.values());
        dialog.setTitle("Generate Smart Playlist");
        dialog.setHeaderText("Filtra per tag");
        dialog.setContentText("Seleziona il tag desiderato:");

        Optional<Tag> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        try {
            Playlist generated = playlistService.generatePlaylistByTag(playlists, tracks, result.get());

            playlistListView.getSelectionModel().select(generated);
            updateSelectedPlaylistView(generated);

            setStatus("Smart playlist creata: " + generated.getName());

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

            selectedPlaylistTracks.add(selectedTrack);
            playlistTrackTableView.getSelectionModel().select(selectedTrack);
            playlistTrackTableView.scrollTo(selectedTrack);

            if (playerControlController != null) {
                playerControlController.setCurrentPlaylist(selectedPlaylistTracks);
            }

            playlistTrackTableView.refresh();

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

            selectedPlaylistTracks.remove(selectedTrack);

            if (playerControlController != null) {
                playerControlController.setCurrentPlaylist(selectedPlaylistTracks);
            }

            playlistTrackTableView.getSelectionModel().clearSelection();
            playlistTrackTableView.refresh();

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
     * Sposta la traccia selezionata verso l'alto.
     */
    public void moveSelectedTrackUp() {
        Playlist selectedPlaylist = getSelectedPlaylist();
        Track selectedTrack = getSelectedTrackFromPlaylist();

        if (selectedPlaylist == null || selectedTrack == null) {
            return;
        }

        int currentIndex = selectedPlaylist.getTracks().indexOf(selectedTrack);

        if (currentIndex <= 0) {
            return;
        }

        int targetIndex = currentIndex - 1;

        try {
            playlistService.moveTrackInPlaylist(selectedPlaylist, currentIndex, targetIndex);

            updateSelectedPlaylistView(selectedPlaylist);

            playlistTrackTableView.getSelectionModel().select(targetIndex);

            if (playerControlController != null) {
                playerControlController.syncQueueWithoutInterrupting(selectedPlaylist.getTracks());
            }

            setStatus("Traccia spostata su: " + selectedTrack.getTitle());

        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Sposta la traccia selezionata verso il basso.
     */
    public void moveSelectedTrackDown() {
        Playlist selectedPlaylist = getSelectedPlaylist();
        Track selectedTrack = getSelectedTrackFromPlaylist();

        if (selectedPlaylist == null || selectedTrack == null) {
            return;
        }

        int currentIndex = selectedPlaylist.getTracks().indexOf(selectedTrack);

        if (currentIndex == -1 || currentIndex >= selectedPlaylist.getTracks().size() - 1) {
            return;
        }

        int targetIndex = currentIndex + 1;

        try {
            playlistService.moveTrackInPlaylist(selectedPlaylist, currentIndex, targetIndex);

            updateSelectedPlaylistView(selectedPlaylist);

            playlistTrackTableView.getSelectionModel().select(targetIndex);

            if (playerControlController != null) {
                playerControlController.syncQueueWithoutInterrupting(selectedPlaylist.getTracks());
            }

            setStatus("Traccia spostata giù: " + selectedTrack.getTitle());

        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Aggiorna la tabella della playlist selezionata.
     */
    public void updateSelectedPlaylistView(Playlist playlist) {
        selectedPlaylistTracks.clear();

        if (playlist == null) {
            if (playlistTrackTableView != null) {
                playlistTrackTableView.refresh();
            }

            setStatus("Nessuna playlist selezionata.");
            return;
        }

        selectedPlaylistTracks.addAll(playlist.getTracks());

        if (playlistTrackTableView != null) {
            playlistTrackTableView.refresh();
        }

        if (playerControlController != null) {
            playerControlController.setCurrentPlaylist(selectedPlaylistTracks);
        }

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
     * Forza l'aggiornamento grafico della tabella della playlist selezionata.
     */
    public void refreshSelectedPlaylistTable() {
        if (playlistTrackTableView != null) {
            playlistTrackTableView.refresh();
        }
    }

    /**
     * Gestisce il caso in cui una traccia rimossa dalla playlist fosse in riproduzione.
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
     */
    public Playlist getSelectedPlaylist() {
        return playlistListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Restituisce la traccia selezionata nella Track Library.
     */
    private Track getSelectedTrackFromLibrary() {
        return trackTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Restituisce la traccia selezionata nella tabella della playlist.
     */
    private Track getSelectedTrackFromPlaylist() {
        return playlistTrackTableView.getSelectionModel().getSelectedItem();
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
     * Mostra un pop-up di errore.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Applica il filtro di ricerca alla lista delle playlist visibili.
     * <p>
     * Il metodo aggiorna gli elementi mostrati nella {@code ListView} mantenendo
     * solo le playlist che corrispondono alla query tramite {@link SearchService}.
     * Se i riferimenti grafici o i dati necessari non sono disponibili,
     * il metodo non esegue alcuna operazione.
     * </p>
     *
     * @param query testo da cercare; se {@code null} o vuoto, vengono mostrate
     *              tutte le playlist disponibili
     */
    public void applySearch(String query) {
        if (playlistListView == null || searchService == null || playlists == null) {
            return;
        }

        playlistListView.setItems(FXCollections.observableArrayList(
                playlists.stream()
                        .filter(playlist -> searchService.matchesPlaylist(playlist, query))
                        .toList()
        ));
    }
}