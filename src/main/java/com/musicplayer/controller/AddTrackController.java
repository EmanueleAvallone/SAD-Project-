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
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.musicplayer.service.TrackService;

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

    private final TrackService trackService = new TrackService();
    public Track getCreatedTrack() {
        return createdTrack;
    }

    @FXML
    private void initialize() {
        statusLabel.setText("Ready");
    }

    /**
     * Gestisce il salvataggio della traccia.
     *
     * In modalità Add crea una nuova traccia usando i dati inseriti.
     * In modalità Edit crea una traccia temporanea con i nuovi dati,
     * mantenendo invariata la durata originale.
     */
    @FXML
    private void handleAddTrack() {
        String title = titleField.getText();
        String author = authorField.getText();
        String length = lengthField.getText();
        String genre = genreField.getText();
        String yearText = yearField.getText();

        try {
            if (editMode && trackToEdit != null) {
                createdTrack = trackService.createTrack(
                        title,
                        author,
                        trackToEdit.getLength(),
                        genre,
                        yearText
                );
            } else {
                createdTrack = trackService.createTrack(
                        title,
                        author,
                        length,
                        genre,
                        yearText
                );
            }

            closeWindow();

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Ripristina i campi della schermata.
     *
     * In modalità Add svuota i campi.
     * In modalità Edit ripristina i valori originali della traccia selezionata.
     */
    @FXML
    private void handleReset() {
        if (editMode && trackToEdit != null) {
            titleField.setText(trackToEdit.getTitle());
            authorField.setText(trackToEdit.getAuthor());
            lengthField.setText(trackToEdit.getLength());
            genreField.setText(trackToEdit.getGenre());
            yearField.setText(String.valueOf(trackToEdit.getYear()));

            favouriteCheckBox.setSelected(false);
            explicitCheckBox.setSelected(false);
            newReleaseCheckBox.setSelected(false);

            statusLabel.setText("Valori originali ripristinati.");
            return;
        }

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
     * @throws IllegalArgumentException se la traccia è null
     */
    public void setTrackToEdit(Track track) {
        if (track == null) {
            throw new IllegalArgumentException("La traccia da modificare non può essere null.");
        }

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

    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importa dati traccia");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt")
        );

        Stage stage = (Stage) titleField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            importDataFromFile(file);
        }
    }

    private void importDataFromFile(File file) {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);

            if (props.getProperty("title") != null) titleField.setText(props.getProperty("title"));
            if (props.getProperty("author") != null) authorField.setText(props.getProperty("author"));
            if (props.getProperty("length") != null) lengthField.setText(props.getProperty("length"));
            if (props.getProperty("genre") != null) genreField.setText(props.getProperty("genre"));
            if (props.getProperty("year") != null) yearField.setText(props.getProperty("year"));
            if (props.getProperty("notes") != null) notesArea.setText(props.getProperty("notes"));

            statusLabel.setText("Dati importati da: " + file.getName());

        } catch (Exception e) {
            showError("Errore durante la lettura del file: " + e.getMessage());
            statusLabel.setText("Errore importazione.");
        }
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