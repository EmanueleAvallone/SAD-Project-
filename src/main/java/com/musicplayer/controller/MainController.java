package com.musicplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
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
        System.out.println("New playlist");
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

    @FXML
    private void handleAddTrack() {
        System.out.println("Add track");
    }

    @FXML
    private void handleEditTrack() {
        System.out.println("Edit track");
    }

    @FXML
    private void handleDeleteTrack() {
        System.out.println("Delete track");
    }

    @FXML
    private void handleAddToPlaylist() {
        System.out.println("Add selected track to playlist");
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

    @FXML
    private void handlePlay() {
        System.out.println("Play");
    }

    @FXML
    private void handlePause() {
        System.out.println("Pause");
    }

    @FXML
    private void handleSkip() {
        System.out.println("Skip");
    }

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
}