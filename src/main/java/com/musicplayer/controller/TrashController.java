package com.musicplayer.controller;

import com.musicplayer.model.Track;
import com.musicplayer.service.TrackService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller dedicato alla gestione visiva del Cestino (Trash Bin).
 * Gestisce l'apertura del Dialog e il ripristino delle tracce.
 */
public class TrashController {

    private final TrackService trackService;
    private final ObservableList<Track> mainLibrary;
    private final ObservableList<Track> trashList;

    public TrashController(TrackService trackService, ObservableList<Track> mainLibrary, ObservableList<Track> trashList) {
        this.trackService = trackService;
        this.mainLibrary = mainLibrary;
        this.trashList = trashList;
    }

    /**
     * Apre la finestra di dialogo del Cestino.
     */
    public void showTrashBinDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Trash Bin");
        dialog.setHeaderText("Tracce eliminate di recente");

        DialogPane dialogPane = dialog.getDialogPane();
        StyleManager.applyToDialog(dialog);
        dialogPane.getStyleClass().add("dialog-pane");

        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setPrefWidth(450);
        content.setPrefHeight(400);

        ListView<Track> trashListView = new ListView<>();
        trashListView.setItems(trashList);
        trashListView.setPlaceholder(new Label("Trash is empty"));

        trashListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Track track, boolean empty) {
                super.updateItem(track, empty);
                if (empty || track == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setStyle("-fx-alignment: center-left;");

                    VBox infoBox = new VBox(2);
                    Label titleLabel = new Label(track.getTitle() + " - " + track.getAuthor());
                    titleLabel.setStyle("-fx-font-weight: bold;");

                    String dateStr = track.getDeletedAt() != null ?
                            track.getDeletedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Sconosciuta";
                    Label dateLabel = new Label("Eliminata il: " + dateStr);
                    dateLabel.getStyleClass().add("subtitle");

                    infoBox.getChildren().addAll(titleLabel, dateLabel);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button restoreBtn = new Button("Restore");
                    restoreBtn.setOnAction(e -> trackService.restoreFromTrash(mainLibrary, trashList, track));

                    row.getChildren().addAll(infoBox, spacer, restoreBtn);
                    setGraphic(row);
                }
            }
        });


        Button emptyTrashBtn = new Button("Empty Trash");
        emptyTrashBtn.getStyleClass().add("danger-button");
        emptyTrashBtn.setMaxWidth(Double.MAX_VALUE);
        emptyTrashBtn.setOnAction(e -> {
            if (trashList.isEmpty()) return;

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Empty Trash");
            confirm.setHeaderText("Svuotare il cestino?");
            confirm.setContentText("Tutte le tracce verranno rimosse definitivamente dal sistema.");

            StyleManager.applyToDialog(confirm);

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                trashList.clear();
            }
        });

        content.getChildren().addAll(trashListView, emptyTrashBtn);
        VBox.setVgrow(trashListView, Priority.ALWAYS);

        dialogPane.setContent(content);
        dialog.showAndWait();
    }

}