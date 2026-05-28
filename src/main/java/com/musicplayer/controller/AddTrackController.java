package com.musicplayer.controller;

import com.musicplayer.model.Track;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTrackController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField lengthField;

    @FXML
    private TextField genreField;

    @FXML
    private TextField yearField;

    @FXML
    private TextArea notesArea;

    @FXML
    private CheckBox favouriteCheckBox;

    @FXML
    private CheckBox explicitCheckBox;

    @FXML
    private CheckBox newReleaseCheckBox;

    @FXML
    private Label statusLabel;

    private Track createdTrack;

    public Track getCreatedTrack() {
        return createdTrack;
    }

    @FXML
    private void initialize() {
        statusLabel.setText("Ready");
    }

    @FXML
    private void handleAddTrack() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String length = lengthField.getText().trim();
        String genre = genreField.getText().trim();
        String yearText = yearField.getText().trim();

        if (title.isEmpty()) {
            showError("Il titolo della traccia è obbligatorio.");
            return;
        }

        if (author.isEmpty()) {
            showError("L'autore della traccia è obbligatorio.");
            return;
        }

        if (length.isEmpty()) {
            showError("La durata della traccia è obbligatoria.");
            return;
        }

        if (genre.isEmpty()) {
            showError("Il genere della traccia è obbligatorio.");
            return;
        }

        int year;
        
        if (yearText.isEmpty()) {
            showError("L'anno della traccia è obbligatorio.");
            return;
        }
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException exception) {
            showError("L'anno deve essere un numero valido.");
            return;
        }

        createdTrack = new Track(title, author, length, genre, year);

        closeWindow();
    }

    @FXML
    private void handleReset() {
        titleField.clear();
        authorField.clear();
        lengthField.clear();
        genreField.clear();
        yearField.clear();
        notesArea.clear();

        favouriteCheckBox.setSelected(false);
        explicitCheckBox.setSelected(false);
        newReleaseCheckBox.setSelected(false);

        statusLabel.setText("Form reset.");
    }

    @FXML
    private void handleCancel() {
        createdTrack = null;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}