package com.musicplayer.controller;

import com.musicplayer.model.Track;
import com.musicplayer.model.engine.MediaPlayerEngine;
import com.musicplayer.model.engine.PlayerObserver;
import com.musicplayer.service.PlaybackService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;

/**
 * Controller responsabile della gestione dei comandi del player.
 *
 * Gestisce Play, Pause, Skip e aggiorna la sezione Simulated Playback.
 */
public class PlayerController implements PlayerObserver {

    @FXML
    private Label nowPlayingLabel;

    @FXML
    private Label elapsedTimeLabel;

    @FXML
    private Label totalTimeLabel;

    @FXML
    private ProgressBar playbackProgressBar;

    @FXML
    private Slider playbackSlider;

    @FXML
    private RadioButton sequentialModeRadio;

    @FXML
    private RadioButton shuffleModeRadio;

    @FXML
    private RadioButton loopModeRadio;

    private final PlaybackService playbackService;
    private Track selectedTrack;
    private String lastStatusMessage;

    /**
     * Crea un controller del player con un nuovo PlaybackService.
     */
    public PlayerController() {
        this.playbackService = new PlaybackService();
        this.lastStatusMessage = "";
    }

    /**
     * Inizializza la sezione del player.
     */
    @FXML
    private void initialize() {
        ToggleGroup playbackModeGroup = new ToggleGroup();
        sequentialModeRadio.setToggleGroup(playbackModeGroup);
        shuffleModeRadio.setToggleGroup(playbackModeGroup);
        loopModeRadio.setToggleGroup(playbackModeGroup);
        sequentialModeRadio.setSelected(true);
        refreshPlaybackView();
    }

    /**
     * Imposta la traccia attualmente selezionata.
     *
     * @param selectedTrack traccia selezionata, oppure {@code null}
     */
    public void setSelectedTrack(Track selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    /**
     * Restituisce la traccia attualmente selezionata.
     *
     * @return traccia selezionata, oppure {@code null}
     */
    public Track getSelectedTrack() {
        return selectedTrack;
    }

    /**
     * Gestisce l'azione Play.
     */
    @FXML
    public void handlePlay() {
        if (selectedTrack == null) {
            updateStatus("Seleziona una traccia da riprodurre.");
            refreshPlaybackView();
            return;
        }

        playbackService.playTrack(selectedTrack);
        updateStatus("Riproduzione avviata: " + selectedTrack.getTitle());
        refreshPlaybackView();
    }

    /**
     * Gestisce l'azione Pause.
     */
    @FXML
    public void handlePause() {
        playbackService.pauseTrack();
        updateStatus("Riproduzione sospesa.");
        refreshPlaybackView();
    }

    /**
     * Gestisce l'azione Skip.
     *
     * Per ora il comportamento non è ancora implementato.
     */
    @FXML
    public void handleSkip() {
        updateStatus("Skip non ancora implementato.");
    }

    /**
     * Aggiorna la sezione Simulated Playback.
     */
    public void refreshPlaybackView() {
        Track currentTrack = playbackService.getCurrentTrack();

        if (currentTrack == null) {
            setLabelText(nowPlayingLabel, "No track selected");
            setLabelText(elapsedTimeLabel, "00:00");
            setLabelText(totalTimeLabel, "00:00");
            setProgress(0.0);
            setSliderValue(0.0);
            return;
        }

        setLabelText(nowPlayingLabel, buildNowPlayingText(currentTrack));
        setLabelText(elapsedTimeLabel, formatTime(playbackService.getCurrentTime()));
        setLabelText(totalTimeLabel, currentTrack.getLength());
        setProgress(calculateProgress());
        setSliderValue(calculateSliderValue());
    }

    /**
     * Restituisce il PlaybackService usato dal controller.
     *
     * @return service della riproduzione simulata
     */
    public PlaybackService getPlaybackService() {
        return playbackService;
    }

    /**
     * Restituisce l'ultimo messaggio di stato.
     *
     * @return ultimo messaggio prodotto dal controller
     */
    public String getLastStatusMessage() {
        return lastStatusMessage;
    }

    private void updateStatus(String message) {
        this.lastStatusMessage = message;
    }

    private String buildNowPlayingText(Track track) {
        if (playbackService.isPlaying()) {
            return track.getTitle() + " - In riproduzione";
        }

        return track.getTitle() + " - In pausa";
    }

    private void setLabelText(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    private void setProgress(double progress) {
        if (playbackProgressBar != null) {
            playbackProgressBar.setProgress(progress);
        }
    }

    private void setSliderValue(double value) {
        if (playbackSlider != null) {
            playbackSlider.setValue(value);
        }
    }

    private double calculateProgress() {
        int duration = playbackService.getDuration();

        if (duration <= 0) {
            return 0.0;
        }

        return (double) playbackService.getCurrentTime() / duration;
    }

    private double calculateSliderValue() {
        return calculateProgress() * 100.0;
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    @Override
    public void update(MediaPlayerEngine engine) {
        updateStatus("Stato player: " + engine.getCurrentStateName());
    }
}