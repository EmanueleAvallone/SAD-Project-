package com.musicplayer.controller;

import com.musicplayer.model.Track;
import com.musicplayer.service.TrackService;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class LibraryController {

    private ScrollPane mostPlayedScrollPane;
    private VBox topTracksContainer; // Ora è VBox!
    private VBox emptyMostPlayedView;

    private TrackService trackService;
    private ObservableList<Track> allTracks;
    private PlayerController playerController;

    public void initializeSection(ScrollPane mostPlayedScrollPane,
                                  VBox topTracksContainer, // Ora è VBox!
                                  VBox emptyMostPlayedView,
                                  TrackService trackService,
                                  ObservableList<Track> allTracks,
                                  PlayerController playerController) {

        this.mostPlayedScrollPane = mostPlayedScrollPane;
        this.topTracksContainer = topTracksContainer;
        this.emptyMostPlayedView = emptyMostPlayedView;
        this.trackService = trackService;
        this.allTracks = allTracks;
        this.playerController = playerController;

        updateMostPlayedSection();
    }

    public void updateMostPlayedSection() {
        topTracksContainer.getChildren().clear();

        List<Track> topTracks = trackService.getTopPlayedTracks(allTracks, 10);

        if (topTracks.isEmpty()) {
            mostPlayedScrollPane.setVisible(false);
            mostPlayedScrollPane.setManaged(false);
            emptyMostPlayedView.setVisible(true);
            emptyMostPlayedView.setManaged(true);
        } else {
            emptyMostPlayedView.setVisible(false);
            emptyMostPlayedView.setManaged(false);
            mostPlayedScrollPane.setVisible(true);
            mostPlayedScrollPane.setManaged(true);

            for (Track track : topTracks) {
                HBox card = createTrackCard(track);
                topTracksContainer.getChildren().add(card);
            }
        }
    }

    /**
     * Crea una Card orizzontale senza stili invasivi.
     */
    private HBox createTrackCard(Track track) {
        HBox card = new HBox(10);
        card.setStyle("-fx-padding: 8; -fx-alignment: center-left;");

        VBox infoBox = new VBox(2);
        Label titleLabel = new Label(track.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label playsLabel = new Label(track.getPlayedCount() + " ascolti");
        infoBox.getChildren().addAll(titleLabel, playsLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button playButton = new Button("Play");

        playButton.setOnAction(event -> {
            if (playerController != null) {
                playerController.playTrackFromPlaylist(track);
                playerController.setCurrentPlaylist(allTracks);
            }

            updateMostPlayedSection();
        });

        card.getChildren().addAll(infoBox, spacer, playButton);
        return card;
    }
}