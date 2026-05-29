package com.musicplayer.controller;

import com.musicplayer.model.Track;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;

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
    @FXML
    private Label formTitleLabel;

    @FXML
    private Button submitButton;

    private Track createdTrack;
    private Track trackToEdit;
    private boolean editMode;


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

        if (editMode && trackToEdit != null) {
            createdTrack = new Track(title, author, trackToEdit.getLength(), genre, year);
        } else {
            createdTrack = new Track(title, author, length, genre, year);
        }

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

    /**
     * Configura la schermata in modalità modifica.
     *
     * I campi vengono precompilati con i valori della traccia selezionata.
     * La durata viene mostrata ma non resa modificabile.
     *
     * @param track traccia da modificare
     */
    public void setTrackToEdit(Track track) {
        this.trackToEdit = track;
        this.editMode = true;

        formTitleLabel.setText("Edit track");
        submitButton.setText("Save");

        titleField.setText(track.getTitle());
        authorField.setText(track.getAuthor());
        lengthField.setText(track.getLength());
        genreField.setText(track.getGenre());
        yearField.setText(String.valueOf(track.getYear()));

        lengthField.setDisable(true);

        statusLabel.setText("Editing track: " + track.getTitle());
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